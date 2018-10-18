package org.chrisli.utils.excel;

import org.chrisli.utils.Assert;
import org.chrisli.utils.excel.convertor.HtmlConvertor;
import org.chrisli.utils.file.FileUtil;

import java.io.File;

/**
 * [Excel工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class ExcelUtil {
    /**
     * [将excel转换为Html文件]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static void convertToHtmlFile(String excelPath, String htmlPath) {
        Assert.notBlank(excelPath, "excel文件路径不允许为空!");
        Assert.notBlank(htmlPath, "html文件路径不允许为空!");
        File htmlFile = FileUtil.createFile(htmlPath);
        FileUtil.writeData(htmlFile, HtmlConvertor.create(excelPath).convert(), "UTF-8");
    }

    public static void main(String[] args) {
        convertToHtmlFile("D:\\零售出库单.xlsx", "D:\\零售出库单.html");
    }
}
