package org.chrisli.utils.file;

import org.chrisli.utils.exception.FrameworkException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

/**
 * [文件工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class FileUtil {
    /**
     * [创建文件]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static File createFile(String fileName) {
        File file = new File(fileName);
        if (file.isDirectory()) {
            file.mkdirs();
            return file;
        }
        file = file.getParentFile();
        file.mkdirs();
        file = new File(fileName);
        // 如果已存在,删除旧文件
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            return file;
        } catch (Exception e) {
            throw new FrameworkException("创建文件失败!", e);
        }
    }

    /**
     * [文件{file}中写入数据{data},编码格式为{characterEncoding}]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static void writeData(File file, String data, String characterEncoding) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), characterEncoding));
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new FrameworkException("文件中写入数据失败!", e);
        }
    }
}
