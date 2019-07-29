package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Boss;
import com.ljh.gamedemo.entity.BossSpell;
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
public class LocalBossMap {

    private static File bossFile = null;

    private static File spellBossFile = null;


    // boss bossSpellMap <bossSpellId, bossSpell>
    private static Map<Long, BossSpell> bossSpellMap = Maps.newConcurrentMap();

    // boss bossMap <bossId, boss>
    private static Map<Long, Boss> bossMap = Maps.newConcurrentMap();

    static {
        try {
            spellBossFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/spellBoss.xlsx");
            bossFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/boss.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find bossFile,please check file location");
            e.printStackTrace();
        }
    }

    // 先读取boss技能数据，再将数据装进boss中
    public static void readExcel() {

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(spellBossFile);

        // 遍历获取数据
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                log.error("excel sheet is null, please recheck the spellBoss file resolve");
                break;
            }

            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);
                if (row != null) {
                    BossSpell bossSpell = new BossSpell();
                    bossSpell.setSpellId(Long.valueOf(getValue(row.getCell(0))));
                    bossSpell.setName(getValue(row.getCell(1)));
                    bossSpell.setSchool(Integer.valueOf(getValue(row.getCell(2))));
                    bossSpell.setCd(Integer.valueOf(getValue(row.getCell(3))));
                    bossSpell.setRange(Integer.valueOf(getValue(row.getCell(4))));
                    bossSpell.setSec(Integer.valueOf(getValue(row.getCell(5))));
                    bossSpell.setDamage(Integer.valueOf(getValue(row.getCell(6))));

                    bossSpellMap.put(bossSpell.getSpellId(), bossSpell);
                }
            }
        }

        workbook = formatWorkBook(bossFile);
        // 遍历获取数据
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                log.error("excel sheet is null, please recheck the boss file resolve");
                break;
            }

            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);
                if (row != null) {
                    Boss boss = new Boss();
                    boss.setId(Long.valueOf(getValue(row.getCell(0))));
                    boss.setName(getValue(row.getCell(1)));
                    boss.setHp(Integer.valueOf(getValue(row.getCell(2))));

                    String spellStr = getValue(row.getCell(3));
                    String[] spellArr = spellStr.split("\\|");
                    List<BossSpell> spellList = new ArrayList<>();
                    for (String s : spellArr) {
                        Long id = Long.valueOf(s);
                        BossSpell spell = bossSpellMap.get(id);
                        if (spell != null){
                            spellList.add(spell);
                        }
                    }
                    boss.setSpellList(spellList);

                    bossMap.put(boss.getId(), boss);
                }
            }
        }
    }

    public static Map<Long, Boss> getBossMap() {
        return bossMap;
    }

    public static Map<Long, BossSpell> getBossSpellMap() {
        return bossSpellMap;
    }

    public static void main(String[] args) {
        readExcel();

        bossSpellMap.forEach((k, v) -> {
            System.out.println("k: " + k + " v: " + v);
        });

        bossMap.forEach((k, v) -> {
            System.out.println("k: " + k + " v: " + v);
        });
    }
}
