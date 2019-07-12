package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Creep;
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
 * 野兽本地存储数据
 *
 */

@Slf4j
public class LocalCreepMap {

    private static File creepFile = null;

    // creep idMap：<creepId, Creep>
    private static Map<Integer, Creep> idCreepMap = Maps.newConcurrentMap();

    // creep nameCreepMap：<creepName, Creep>
    private static Map<String, Creep> nameCreepMap = Maps.newConcurrentMap();

    // creep siteCreepMap：<siteName, List<Creep>>
    private static Map<Integer, List<Creep>> siteCreepMap = Maps.newConcurrentMap();

    static {
        try {
            creepFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/creeps.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find creepFile,please check file location");
            e.printStackTrace();
        }
    }

    public static void readExcel(){

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(creepFile);

        // 遍历获取数据
        for (int i = 0; i < workbook.getNumberOfSheets(); i++){
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null){
                log.error("excel sheet is null, please recheck the creep file resolve");
                break;
            }

            for (int j = 1; j <= sheet.getLastRowNum(); j++){
                Row row = sheet.getRow(j);
                if (row != null){
                    Creep creep = new Creep();

                    // 设置野怪的属性
                    creep.setCreepId(Integer.valueOf(getValue(row.getCell(0))));
                    creep.setName(getValue(row.getCell(1)));
                    creep.setType(Integer.valueOf(getValue(row.getCell(2))));
                    creep.setNum(Integer.valueOf(getValue(row.getCell(3))));
                    creep.setLevel(Integer.valueOf(getValue(row.getCell(4))));
                    creep.setHp(Integer.valueOf(getValue(row.getCell(5))));
                    creep.setDamage(Integer.valueOf(getValue(row.getCell(6))));
                    creep.setSiteId(Integer.valueOf(getValue(row.getCell(7))));

                    idCreepMap.put(creep.getCreepId(), creep);
                    nameCreepMap.put(creep.getName(), creep);

                    // 存放 siteCreepMap
                    List<Creep> creepList;
                    Integer siteId = creep.getSiteId();
                    String siteName = LocalSiteMap.idSiteMap.get(creep.getSiteId()).getName();
                    creepList = siteCreepMap.get(siteId);
                    if (creepList == null){
                        creepList = new ArrayList<>();
                    }
                    creepList.add(creep);
                    siteCreepMap.put(siteId, creepList);
                }
            }
        }
    }

    public static Map<Integer, List<Creep>> getSiteCreepMap() {
        return siteCreepMap;
    }

    public static void setSiteCreepMap(Map<Integer, List<Creep>> siteCreepMap) {
        LocalCreepMap.siteCreepMap = siteCreepMap;
    }

    public static Map<Integer, Creep> getIdCreepMap() {
        return idCreepMap;
    }

    public static void setIdCreepMap(Map<Integer, Creep> idCreepMap) {
        LocalCreepMap.idCreepMap = idCreepMap;
    }

    public static Map<String, Creep> getNameCreepMap() {
        return nameCreepMap;
    }

    public static void setNameCreepMap(Map<String, Creep> nameCreepMap) {
        LocalCreepMap.nameCreepMap = nameCreepMap;
    }

    public static void main(String[] args) {

        LocalSiteMap.readExcel();
        readExcel();

        nameCreepMap.forEach((k, v) ->{
            System.out.println("k: " + k + " value: " + v);
        });


        System.out.println("======================");

        idCreepMap.forEach((k, v) -> {
            System.out.println("k: " + k + " value: " + v);
        });

        System.out.println("======================");


        siteCreepMap.forEach((k, v) -> {
            System.out.println("k: " + k + " value: " + v);


        });

    }

}
