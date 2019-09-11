package com.ljh.gamedemo.module.duplicate.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.duplicate.bean.Boss;
import com.ljh.gamedemo.module.duplicate.bean.Duplicate;
import com.ljh.gamedemo.module.equip.bean.Equip;
import com.ljh.gamedemo.module.equip.local.LocalEquipMap;
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
 * 本地副本信息
 *
 * @Author: Heiku
 * @Date: 2019/7/29
 */
@Slf4j
public class LocalDuplicateMap {

    /**
     * 副本数据文件
     */
    private static File duplicateFile = null;

    /**
     * 副本数据map <duplicateId, Duplicate>
     */
    private static Map<Long, Duplicate> duplicateMap = Maps.newConcurrentMap();


    static {
        try {
            duplicateFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/duplicate.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find duplicateFile,please check file location");
            e.printStackTrace();
        }
    }

    /**
     * 载入数据文件
     */
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

                    String probStr = getValue(row.getCell(7));
                    String[] proArr = probStr.split("\\|");
                    List<Double> proList = new ArrayList<>();
                    for (String s : proArr) {
                        Double d = Double.valueOf(s);
                        proList.add(d);
                    }
                    duplicate.setProbability(proList);


                    duplicateMap.put(duplicate.getId(), duplicate);
                }
            }
        }
        log.info("Duplicate 数据载入成功");
    }

    public static Map<Long, Duplicate> getDuplicateMap() {
        return duplicateMap;
    }

    public static void main(String[] args) {

    }
}
