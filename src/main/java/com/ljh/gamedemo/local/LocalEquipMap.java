package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Equip;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static com.ljh.gamedemo.util.ExcelUtil.formatWorkBook;
import static com.ljh.gamedemo.util.ExcelUtil.getValue;

/**
 * @Author: Heiku
 * @Date: 2019/7/18
 */

@Slf4j
public class LocalEquipMap {

    private static File equipFile = null;

    // equip idEquipMap: <Long, Equip>
    private static Map<Integer, Equip> idEquipMap = Maps.newConcurrentMap();

    // equip roleEquipMap: <Long, List<Equip>>
    private static Map<Long, List<Equip>> roleEquipMap = Maps.newConcurrentMap();


    static {
        try {
            equipFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/equip.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find creepFile,please check file location");
            e.printStackTrace();
        }
    }

    public static void readExcel() {

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(equipFile);

        // 遍历获取数据
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                log.error("excel sheet is null, please recheck the creep file resolve");
                break;
            }

            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);
                if (row != null) {
                    Equip equip = new Equip();

                    equip.setEquipId(Long.valueOf(getValue(row.getCell(0))));
                }

            }
        }
    }

}
