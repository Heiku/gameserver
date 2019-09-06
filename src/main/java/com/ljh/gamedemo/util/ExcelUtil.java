package com.ljh.gamedemo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
public class ExcelUtil {

    private static DataFormatter dataFormatter = new DataFormatter();

    private static InputStream is = null;
    private static Workbook workbook = null;

    /**
     * if file belongs to xls or xlsx and return correct format workbook
     *
     * @param file
     * @return
     */
    public static Workbook formatWorkBook(File file){
        try {
            is = new FileInputStream(file);
            String filePath = file.getPath();

            if ("xls".equals(getPostFix(filePath))) {

                //xls对应的HSSFWorkbook工作簿对象
                workbook = new HSSFWorkbook(is);
            } else if ("xlsx".equals(getPostFix(filePath))) {

                //xlsx对应的XSSFWorkbook工作簿对象
                workbook = new XSSFWorkbook(is);
            } else {
                log.error("can not read the filePath");
                return workbook;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (workbook == null) {
            log.error("workbook is null, please recheck the site file resolve");
            return workbook;
        }

        return workbook;
    }

    // get value from cell
    public static String getValue(Cell cell){
        return dataFormatter.formatCellValue(cell);
    }

    public static Integer getIntValue(Cell cell){
        return Integer.valueOf(getValue(cell));
    }

    public static Long getLongValue(Cell cell){
        return Long.valueOf(getValue(cell));
    }

    // get filePath postfix
    public static String getPostFix(String path) {
        if(path == null || "".equals(path.trim())){
            return "";
        }

        if(path.contains(".") && path.lastIndexOf(".") != path.length() -1 ){
            return path.substring(path.lastIndexOf(".") + 1, path.length());
        }

        return "";
    }

}
