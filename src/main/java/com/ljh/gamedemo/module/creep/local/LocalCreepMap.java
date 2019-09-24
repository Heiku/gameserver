package com.ljh.gamedemo.module.creep.local;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.creep.bean.Creep;
import com.ljh.gamedemo.module.site.local.LocalSiteMap;
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
import java.util.Optional;

import static com.ljh.gamedemo.util.ExcelUtil.formatWorkBook;
import static com.ljh.gamedemo.util.ExcelUtil.getValue;

/**
 * 野兽本地存储数据
 */

@Slf4j
public class LocalCreepMap {

    /**
     * 野怪文件
     */
    private static File creepFile = null;

    /**
     * creep idMap：<creepId, Creep>
     */
    private static Map<Long, Creep> idCreepMap = Maps.newConcurrentMap();


    /**
     * creep siteCreepMap：<siteName, List<Creep>>
     */
    private static Map<Integer, List<Creep>> siteCreepMap = Maps.newConcurrentMap();

    static {
        try {
            creepFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/creeps.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find creepFile,please check file location");
            e.printStackTrace();
        }
    }


    /**
     * 读取文件数据
     */
    public static void readExcel() throws Exception{
        long creepId = 20000L;

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
                    int num = Integer.valueOf(getValue(row.getCell(3)));
                    while (num-- > 0) {
                        Creep creep = new Creep();

                        // 设置野怪的属性
                        creep.setId(creepId++);
                        creep.setCreepId(Long.valueOf(getValue(row.getCell(0))));
                        creep.setName(getValue(row.getCell(1)));
                        creep.setType(Integer.valueOf(getValue(row.getCell(2))));
                        creep.setLevel(Integer.valueOf(getValue(row.getCell(4))));
                        creep.setHp(Integer.valueOf(getValue(row.getCell(5))));
                        creep.setMaxHp(Integer.valueOf(getValue(row.getCell(6))));
                        creep.setDamage(Integer.valueOf(getValue(row.getCell(7))));
                        creep.setSiteId(Integer.valueOf(getValue(row.getCell(8))));
                        creep.setCoolDown(Integer.valueOf(getValue(row.getCell(8))));

                        idCreepMap.put(creep.getId(), creep);

                        // 存放 siteCreepMap
                        Integer siteId = creep.getSiteId();
                        List<Creep> creepList = Optional.ofNullable(siteCreepMap.get(siteId)).orElse(Lists.newArrayList());
                        creepList.add(creep);
                        siteCreepMap.put(siteId, creepList);
                    }
                }
            }
        }

        workbook.close();
        log.info("Creep 数据载入成功");
    }

    public static Map<Integer, List<Creep>> getSiteCreepMap() {
        return siteCreepMap;
    }

    public static Map<Long, Creep> getIdCreepMap() {
        return idCreepMap;
    }


    public static void main(String[] args) throws Exception{

        LocalSiteMap.readExcel();
        readExcel();

        System.out.println("======================");

        idCreepMap.forEach((k, v) ->
            System.out.println("k: " + k + " value: " + v));

        System.out.println("======================");


        siteCreepMap.forEach((k, v) ->
            System.out.println("k: " + k + " value: " + v.size()));

    }

}
