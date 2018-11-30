package org.chrisli.utils.excel.convertor;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellFormatResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.chrisli.utils.Assert;
import org.chrisli.utils.exception.FrameworkException;
import org.chrisli.utils.kit.MapUtil;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRElt;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellStyle.*;

/**
 * [Html转换器]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class HtmlConvertor {

    private static final Map<Short, String> ALIGN = MapUtil.toMap(ALIGN_LEFT, "left",
            ALIGN_CENTER, "center", ALIGN_RIGHT, "right", ALIGN_FILL, "left",
            ALIGN_JUSTIFY, "left", ALIGN_CENTER_SELECTION, "center");

    private static final Map<Short, String> VERTICAL_ALIGN = MapUtil.toMap(
            VERTICAL_BOTTOM, "bottom", VERTICAL_CENTER, "middle", VERTICAL_TOP,
            "top");

    private static final Map<Short, String> BORDER = MapUtil.toMap(BORDER_DASH_DOT,
            "dashed 1pt", BORDER_DASH_DOT_DOT, "dashed 1pt", BORDER_DASHED,
            "dashed 1pt", BORDER_DOTTED, "dotted 1pt", BORDER_DOUBLE,
            "double 1pt", BORDER_HAIR, "solid 1pt", BORDER_MEDIUM, "solid 1pt",
            BORDER_MEDIUM_DASH_DOT, "dashed 1pt", BORDER_MEDIUM_DASH_DOT_DOT,
            "dashed 1pt", BORDER_MEDIUM_DASHED, "dashed 1pt", BORDER_NONE,
            "none", BORDER_SLANTED_DASH_DOT, "dashed 1pt", BORDER_THICK,
            "solid 5pt", BORDER_THIN, "solid 1pt");

    private final String HTML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

    private final String LINE_BREAK = "\n";

    private final String FORMAT_CELL_POINT = "(%d,%d)";

    private final String FORMAT_BORDER_POINT = "[%d,%d]";

    private final String FORMAT_IMG_POINT = "{%d,%d}";

    private final Workbook WORKBOOK;

    private Sheet sheet;

    private int startColumnIndex;

    private int endColumnIndex;

    private HashSet<String> cellMergedSet = new HashSet<String>();

    private HashSet<String> cellHasValueSet = new HashSet<String>();

    private HashSet<String> cellHiddenSet = new HashSet<String>();

    private HashSet<String> cellBorderSet = new HashSet<String>();

    private Map<String, String> cellMergedMap = new HashMap<String, String>();

    private Map<String, PictureData> cellImgMap = new HashMap<String, PictureData>();

    private float width = 0f;

    private StringBuilder builder;

    private HtmlConvertor(Workbook workbook) {
        this.WORKBOOK = workbook;
        parseWorkbook();
        builder = new StringBuilder();
    }

    /**
     * [解析workbook]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private void parseWorkbook() {
        sheet = WORKBOOK.getSheetAt(WORKBOOK.getFirstVisibleTab());
        // 解析sheet,整理单元格并分类
        parseSheet();
        // 解析图片
        parsePictures();
    }

    /**
     * [解析sheet,整理单元格并分类]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private void parseSheet() {
        // 获取合并单元格的坐标
        for (CellRangeAddress addr : sheet.getMergedRegions()) {
            int rowspan = addr.getLastRow() - addr.getFirstRow() + 1;
            int colspan = addr.getLastColumn() - addr.getFirstColumn() + 1;
            for (int x = addr.getFirstColumn(); x <= addr.getLastColumn(); x++) {
                for (int y = addr.getFirstRow(); y <= addr.getLastRow(); y++) {
                    cellMergedSet.add("(" + x + "," + y + ")");
                    if (x == addr.getFirstColumn() && y == addr.getFirstRow()) {
                        cellMergedMap.put(String.format(FORMAT_CELL_POINT, x, y), rowspan + "," + colspan);
                    }
                }
            }
        }

        // 获取sheet的边界,获取有值的单元格的坐标
        Iterator<Row> iter = sheet.rowIterator();
        startColumnIndex = (iter.hasNext() ? Integer.MAX_VALUE : 0);
        endColumnIndex = 0;
        while (iter.hasNext()) {
            Row row = iter.next();
            short firstCell = row.getFirstCellNum();
            if (firstCell >= 0) {
                startColumnIndex = Math.min(startColumnIndex, firstCell);
                endColumnIndex = Math.max(endColumnIndex, row.getLastCellNum() - 1);
                for (int i = firstCell; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        CellAddress address = cell.getAddress();
                        cellHasValueSet.add(String.format(FORMAT_CELL_POINT, address.getColumn(), address.getRow()));
                    }
                }
            }
        }

        // 计算表格宽度,获取隐藏的单元格的坐标
        for (int i = startColumnIndex; i <= endColumnIndex; i++) {
            width += sheet.getColumnWidthInPixels(i);
            if (sheet.isColumnHidden(i)) {
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    cellHiddenSet.add(String.format(FORMAT_CELL_POINT, i, j));
                }
            }
        }
    }

    /**
     * [解析图片]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private void parsePictures() {
        if (WORKBOOK instanceof HSSFWorkbook) {
            List<HSSFPictureData> pictureDataList = ((HSSFWorkbook) WORKBOOK).getAllPictures();
            if (CollectionUtils.isNotEmpty(pictureDataList)) {
                HSSFClientAnchor anchor = null;
                for (HSSFShape shape : ((HSSFSheet) sheet).getDrawingPatriarch().getChildren()) {
                    anchor = (HSSFClientAnchor) shape.getAnchor();
                    if (shape instanceof HSSFPicture) {
                        HSSFPicture picture = (HSSFPicture) shape;
                        int pictureIndex = picture.getPictureIndex() - 1;
                        HSSFPictureData pictureData = pictureDataList.get(pictureIndex);
                        String imgPoint = String.format(FORMAT_IMG_POINT, anchor.getCol1(), anchor.getRow1());
                        cellImgMap.put(imgPoint, pictureData);
                    }
                }
            }
        } else {
            XSSFSheet sheet = ((XSSFWorkbook) WORKBOOK).getSheetAt(0);
            for (POIXMLDocumentPart part : sheet.getRelations()) {
                if (part instanceof XSSFDrawing) {
                    XSSFDrawing drawing = (XSSFDrawing) part;
                    List<XSSFShape> shapeList = drawing.getShapes();
                    for (XSSFShape shape : shapeList) {
                        XSSFPicture picture = (XSSFPicture) shape;
                        XSSFClientAnchor anchor = picture.getPreferredSize();
                        XSSFPictureData pictureData = picture.getPictureData();
                        String imgPoint = String.format(FORMAT_IMG_POINT, anchor.getCol1(), anchor.getRow1());
                        cellImgMap.put(imgPoint, pictureData);
                    }
                }
            }
        }
    }

    /**
     * [转换成Html字符串]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static HtmlConvertor create(String filePath) {
        Assert.notBlank(filePath, "excel文件路径不允许为空!");
        return create(new File(filePath));
    }

    /**
     * [转换成Html字符串]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static HtmlConvertor create(File file) {
        Assert.notNull(file, "excel文件不允许为空!");
        try {
            return create(new FileInputStream(file));
        } catch (Exception e) {
            throw new FrameworkException("文件不存在!");
        }
    }

    /**
     * [转换成Html字符串]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static HtmlConvertor create(InputStream stream) {
        Assert.notNull(stream, "excel文件流不允许为空!");
        try {
            Workbook workbook = WorkbookFactory.create(stream);
            return new HtmlConvertor(workbook);
        } catch (Exception e) {
            throw new FrameworkException("无法从文件流中获取Workbook!", e);
        }
    }

    /**
     * [转换成Html字符串]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public String convert() {
        Assert.notNull(WORKBOOK, "excel文件流不允许为空!");
        builder.append(HTML_HEAD).append(LINE_BREAK);
        builder.append("<html>").append(LINE_BREAK);
        builder.append("<head>").append(LINE_BREAK);
        builder.append("</head>").append(LINE_BREAK);
        builder.append("<body>").append(LINE_BREAK);
        convertContent();
        builder.append("</body>").append(LINE_BREAK);
        builder.append("</html>");
        return builder.toString();
    }

    /**
     * [转换excel内容]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private void convertContent() {
        builder.append("<div aligin=\"center\">").append(LINE_BREAK);
        builder.append("<table cellspacing=\"0\" cellpadding=\"0\" ");
        builder.append("style=\"word-break:break-all;width:").append(width).append("pt; \">").append(LINE_BREAK);
        for (int i = startColumnIndex; i <= endColumnIndex; i++) {
            builder.append("<col/>").append(LINE_BREAK);
        }
        builder.append("<tbody>").append(LINE_BREAK);
        Row row = null;
        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            row = sheet.getRow(rowIndex);
            if (row == null) {
                builder.append("<tr><td></td></tr>").append(LINE_BREAK);
            } else {
                convertRow(row, rowIndex);
            }
        }
        builder.append("</tbody>").append(LINE_BREAK);
        builder.append("</table>").append(LINE_BREAK);
        builder.append("</div>");
    }

    /**
     * [转换行内容]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private void convertRow(Row row, int rowIndex) {
        if (!row.getZeroHeight()) {
            builder.append("<tr style=\"height:" + row.getHeightInPoints() + "pt;\">").append(LINE_BREAK);
            String point = "";
            for (int colIndex = startColumnIndex; colIndex <= endColumnIndex; colIndex++) {
                point = String.format(FORMAT_CELL_POINT, colIndex, rowIndex);
                if (!cellHiddenSet.contains(point)) {
                    if (!cellHasValueSet.contains(point)) {
                        // 该单元格为空
                        builder.append("<td></td>").append(LINE_BREAK);
                        continue;
                    }
                    // 该单元格不为空,判断是否需要转换
                    if (shouldConvert(point)) {
                        convertCell(row.getCell(colIndex));
                    }
                }
            }
            builder.append("</tr>").append(LINE_BREAK);
        }
    }

    /**
     * [判断该坐标的单元格是否需要转换]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private boolean shouldConvert(String point) {
        if (cellMergedSet.contains(point)) {
            // 合并的单元格,只需要转换一次
            return cellMergedMap.containsKey(point);
        }
        return true;
    }

    /**
     * [转换单元格内容]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private void convertCell(Cell cell) {
        builder.append("<td ").append(getAttr(cell)).append(">");
        builder.append(getImg(cell));
        builder.append(getContent(cell).replaceAll("\\n", "<br/>"));
        builder.append("</td>").append(LINE_BREAK);
    }

    /**
     * [获取表格单元格属性]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private String getAttr(Cell cell) {
        StringBuilder attrBuilder = new StringBuilder();
        float width = sheet.getColumnWidthInPixels(cell.getAddress().getColumn());
        // 合并单元格
        String cellPoint = String.format(FORMAT_CELL_POINT, cell.getAddress().getColumn(), cell.getAddress().getRow());
        String borderPoint = null;
        // 合并单元格的情况下,存放受影响的的边框坐标
        Set<String> borderTopPointSet = new HashSet<String>();
        Set<String> borderBottomPointSet = new HashSet<String>();
        Set<String> borderLeftPointSet = new HashSet<String>();
        Set<String> borderRightPointSet = new HashSet<String>();
        if (cellMergedMap.containsKey(cellPoint)) {
            String[] pointArray = cellMergedMap.get(cellPoint).split(",");
            attrBuilder.append("rowspan=\"").append(pointArray[0]).append("\" ");
            attrBuilder.append("colspan=\"").append(pointArray[1]).append("\" ");
            int rowspan = Integer.parseInt(pointArray[0]);
            while (rowspan > 1) {
                borderBottomPointSet.add(String.format(FORMAT_BORDER_POINT, cell.getAddress().getColumn() * 2 + 1, (cell.getAddress().getRow() + rowspan - 1) * 2 + 2));
                borderLeftPointSet.add(String.format(FORMAT_BORDER_POINT, cell.getAddress().getColumn() * 2, (cell.getAddress().getRow() + rowspan - 1) * 2 + 1));
                borderRightPointSet.add(String.format(FORMAT_BORDER_POINT, cell.getAddress().getColumn() * 2 + 2, (cell.getAddress().getRow() + rowspan - 1) * 2 + 1));
                rowspan--;
            }
            int colspan = Integer.parseInt(pointArray[1]);
            while (colspan > 1) {
                width += sheet.getColumnWidthInPixels(cell.getAddress().getColumn() + colspan - 1);
                borderTopPointSet.add(String.format(FORMAT_BORDER_POINT, (cell.getAddress().getColumn() + colspan - 1) * 2 + 1, cell.getAddress().getRow() * 2));
                borderBottomPointSet.add(String.format(FORMAT_BORDER_POINT, (cell.getAddress().getColumn() + colspan - 1) * 2 + 1, cell.getAddress().getRow() * 2 + 2));
                borderRightPointSet.add(String.format(FORMAT_BORDER_POINT, (cell.getAddress().getColumn() + colspan - 1) * 2 + 2, cell.getAddress().getRow() * 2 + 1));
                colspan--;
            }
        }
        attrBuilder.append("style=\"");
        CellStyle style = cell.getCellStyle();
        attrBuilder.append("width:").append(width).append("pt;");
        // 设置文本对齐方式
        if (ALIGN.containsKey(style.getAlignment())) {
            attrBuilder.append("text-align:").append(ALIGN.get(style.getAlignment())).append(";");
        }
        // 设置垂直对齐方式
        if (VERTICAL_ALIGN.containsKey(style.getVerticalAlignment())) {
            attrBuilder.append("vertical-align:").append(VERTICAL_ALIGN.get(style.getVerticalAlignment())).append(";");
        }
        // 设置字体
        Font font = WORKBOOK.getFontAt(style.getFontIndex());
        attrBuilder.append("font-family:").append(font.getFontName()).append(";");
        attrBuilder.append("font-size:").append(font.getFontHeightInPoints()).append("pt;");
        if (font.getBold()) {
            attrBuilder.append("font-weight:bold;");
        }
        if (font.getItalic()) {
            attrBuilder.append("font-style:italic;");
        }
        // 设置边框
        if (style.getBorderTop() != 0 && BORDER.containsKey(style.getBorderTop())) {
            borderPoint = String.format(FORMAT_BORDER_POINT, cell.getAddress().getColumn() * 2 + 1, cell.getAddress().getRow() * 2);
            if (cellBorderSet.add(borderPoint)) {
                attrBuilder.append("border-top:").append(BORDER.get(style.getBorderTop())).append(";");
                cellBorderSet.addAll(borderTopPointSet);
            }
        }
        if (style.getBorderBottom() != 0 && BORDER.containsKey(style.getBorderBottom())) {
            borderPoint = String.format(FORMAT_BORDER_POINT, cell.getAddress().getColumn() * 2 + 1, cell.getAddress().getRow() * 2 + 2);
            if (cellBorderSet.add(borderPoint)) {
                attrBuilder.append("border-bottom:").append(BORDER.get(style.getBorderBottom())).append(";");
                cellBorderSet.addAll(borderBottomPointSet);
            }
        }
        if (style.getBorderLeft() != 0 && BORDER.containsKey(style.getBorderLeft())) {
            borderPoint = String.format(FORMAT_BORDER_POINT, cell.getAddress().getColumn() * 2, cell.getAddress().getRow() * 2 + 1);
            if (cellBorderSet.add(borderPoint)) {
                attrBuilder.append("border-left:").append(BORDER.get(style.getBorderLeft())).append(";");
                cellBorderSet.addAll(borderLeftPointSet);
            }
        }
        if (style.getBorderRight() != 0 && BORDER.containsKey(style.getBorderRight())) {
            borderPoint = String.format(FORMAT_BORDER_POINT, cell.getAddress().getColumn() * 2 + 2, cell.getAddress().getRow() * 2 + 1);
            if (cellBorderSet.add(borderPoint)) {
                attrBuilder.append("border-right:").append(BORDER.get(style.getBorderRight())).append(";");
                cellBorderSet.addAll(borderRightPointSet);
            }
        }
        // 设置背景颜色
        if (WORKBOOK instanceof XSSFWorkbook) {
            XSSFCellStyle xssfCellStyle = (XSSFCellStyle) style;
            XSSFColor backgroundColor = xssfCellStyle.getFillForegroundXSSFColor();
            if (backgroundColor != null && !backgroundColor.isAuto()) {
                attrBuilder.append("background-color:#").append(backgroundColor.getARGBHex().substring(2)).append(";");
            }
        } else {
            HSSFCellStyle hssfCellStyle = (HSSFCellStyle) style;
            HSSFColor backgroundColor = hssfCellStyle.getFillForegroundColorColor();
            if (backgroundColor != null) {
                attrBuilder.append("background-color:#").append(backgroundColor.getHexString().substring(2)).append(";");
            }
        }
        attrBuilder.append("\" ");
        return attrBuilder.toString();
    }

    /**
     * [获取单元格图片内容]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private String getImg(Cell cell) {
        String imgPoint = String.format(FORMAT_IMG_POINT, cell.getAddress().getColumn(), cell.getAddress().getRow());
        if (cellImgMap.containsKey(imgPoint)) {
            StringBuilder imgBuilder = new StringBuilder();
            imgBuilder.append("<img src=\"data:image/jpeg|png|gif;base64,");
            imgBuilder.append(Base64.encodeBase64String(cellImgMap.get(imgPoint).getData()));
            imgBuilder.append("\"/>");
            return imgBuilder.toString();
        }
        return "";
    }

    /**
     * [获取表格单元格内容]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private String getContent(Cell cell) {
        try {
            StringBuilder contentBuilder = new StringBuilder();
            XSSFRichTextString rich = (XSSFRichTextString) cell.getRichStringCellValue();
            if (rich.hasFormatting()) {
                int startIndex = 0;
                XSSFFont font = null;
                for (CTRElt ct : rich.getCTRst().getRList()) {
                    font = rich.getFontAtIndex(startIndex);
                    startIndex += ct.getT().length();
                    contentBuilder.append("<font style=\"").append(getFontStyle(font)).append(" \">");
                    contentBuilder.append(ct.getT());
                    contentBuilder.append("</font>");
                }
            } else {
                if (WORKBOOK instanceof XSSFWorkbook) {
                    XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cell.getCellStyle();
                    XSSFColor color = xssfCellStyle.getFont().getXSSFColor();
                    if (color != null && !color.isAuto()) {
                        contentBuilder.append("<font style=\"color:#").append(color.getARGBHex().substring(2)).append(";\">");
                        contentBuilder.append(rich.getString());
                        contentBuilder.append("</font>");
                    } else {
                        contentBuilder.append(rich.getString());
                    }
                } else {
                    HSSFCellStyle hssfCellStyle = (HSSFCellStyle) cell.getCellStyle();
                    HSSFColor color = hssfCellStyle.getFont(WORKBOOK).getHSSFColor((HSSFWorkbook) WORKBOOK);
                    if (color != null) {
                        contentBuilder.append("<font style=\"color:#").append(color.getHexString().substring(2)).append(";\">");
                        contentBuilder.append(rich.getString());
                        contentBuilder.append("</font>");
                    } else {
                        contentBuilder.append(rich.getString());
                    }
                }
            }
            return contentBuilder.toString();
        } catch (Exception e) {
            CellFormat cellFormat;
            if (cell.getCellStyle().getDataFormatString() != null) {
                cellFormat = CellFormat.getInstance(cell.getCellStyle().getDataFormatString());
            } else {
                cellFormat = CellFormat.getInstance("General");
            }
            CellFormatResult result = cellFormat.apply(cell);
            return result.text;
        }
    }

    /**
     * [获取表格单元格字体样式]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private String getFontStyle(XSSFFont font) {
        StringBuilder builder = new StringBuilder();
        if (font.getXSSFColor() != null && !font.getXSSFColor().isAuto()) {
            builder.append("color:#").append(font.getXSSFColor().getARGBHex().substring(2)).append(";");
        }
        if (font.getBold()) {
            builder.append("font-weight:bold;");
        } else {
            builder.append("font-weight:normal;");
        }
        if (font.getItalic()) {
            builder.append("font-style:italic;");
        }
        builder.append("font-family:").append(font.getFontName()).append(";");
        builder.append("font-size:").append(font.getFontHeightInPoints()).append("pt;");
        return builder.toString();
    }
}