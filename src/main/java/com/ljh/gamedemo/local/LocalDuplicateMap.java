package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Boss;
import com.ljh.gamedemo.entity.Duplicate;
import com.ljh.gamedemo.entity.Equip;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ljh.gamedemo.util.ExcelUtil.formatWorkBook;
import static com.ljh.gamedemo.util.ExcelUtil.getValue;

/**
 * @Author: Heiku
 * @Date: 2019/7/29
 */
@Slf4j
public class LocalDuplicateMap {

    private static File duplicateFile = null;

    private static Map<Long, Duplicate> duplicateMap = Maps.newConcurrentMap();


    static {
        try {
            duplicateFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/duplicate.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find duplicateFile,please check file location");
            e.printStackTrace();
        }
    }

    public static void readExcel() {

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(duplicateFile);

        // 遍历获取数据
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                log.error("excel sheet is null, please recheck the duplicate file resolve");
                break;
            }

            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);
                if (row != null) {
                    Duplicate duplicate = new Duplicate();
                    duplicate.setId(Long.valueOf(getValue(row.getCell(0))));
                    duplicate.setName(getValue(row.getCell(1)));
                    duplicate.setGoldReward(Integer.valueOf(getValue(row.getCell(2))));
                    duplicate.setLimitTime(Integer.valueOf(getValue(row.getCell(3))));
                    duplicate.setProgress(Integer.valueOf(getValue(row.getCell(4))));

                    String bossStr = getValue(row.getCell(5));
                    String[] bossArr = bossStr.split("\\|");
                    List<Boss> bosses = new ArrayList<>();
                    for (String s : bossArr) {
                        Long id = Long.valueOf(s);
                        Boss boss = LocalBossMap.getBossMap().get(id);
                        if (boss != null) {
                            bosses.add(boss);
                        }
                    }
                    duplicate.setBosses(bosses);

                    String equipStr = getValue(row.getCell(6));
                    String[] equipArr = equipStr.split("\\|");
                    List<Equip> equips = new ArrayList<>();
                    for (String e : equipArr) {
                        Long id = Long.valueOf(e);
                        Equip equip = LocalEquipMap.getIdEquipMap().get(id);
                        if (equip != null){
                            equips.add(equip);
                        }
                    }
                    duplicate.setEquipReward(equips);

                    duplicateMap.put(duplicate.getId(), duplicate);
                }
            }
        }
    }

    public static Map<Long, Duplicate> getDuplicateMap() {
        return duplicateMap;
    }

    public static void main(String[] args) {

    }
}
