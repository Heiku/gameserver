package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Entity;
import com.ljh.gamedemo.entity.Site;
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
 *
 * 实体数据本地存储
 */

@Slf4j
public class LocalEntityMap {

    private static File entityFile;

    // 存储实体的数据 <entityName, entity>
    public static Map<String, Entity> entityMap = Maps.newHashMap();

    // 存储实体的位置数据 <siteName, List<Entity>>
    public static Map<String, List<Entity>> siteEntityMap = Maps.newHashMap();

    static {
        try {
            entityFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/entity.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find creepFile,please check file location");
            e.printStackTrace();
        }
    }

    // 将.csv文件解析成实体数据
    public static void readExcel() {

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(entityFile);

        // 遍历获取数据
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                log.error("excel sheet is null, please recheck the entity file resolve");
                break;
            }
            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);
                if (row != null) {
                    Entity entity = new Entity();
                    entity = transformEntity(entity, row);

                    // npc的场景id，并设置entity的位置信息
                    int destId = Integer.valueOf(getValue(row.getCell(5)));
                    if (!LocalSiteMap.idSiteMap.containsKey(destId)) {
                        entity.setSite(null);
                    }
                    Site site = LocalSiteMap.idSiteMap.get(destId);
                    entity.setSite(site);


                    // 另存一张映射表，用于表示场景中的实体信息
                    List<Entity> entityList;
                    if (site == null){
                        System.out.println(getValue(row.getCell(2)));
                    }
                    String siteName = site.getName();
                    entityList = siteEntityMap.get(siteName);
                    if (entityList == null){
                        entityList = new ArrayList<>();
                    }
                    entityList.add(entity);

                    siteEntityMap.put(site.getName(), entityList);
                    entityMap.put(entity.getName(), entity);
                }

            }
        }
    }

    private static Entity transformEntity(Entity entity, Row row) {
        entity.setId(Integer.valueOf(getValue(row.getCell(0))));
        entity.setType(Integer.valueOf(getValue(row.getCell(1))));
        entity.setName(getValue(row.getCell(2)));
        entity.setLevel(Integer.valueOf(getValue(row.getCell(3))));
        entity.setAlive(Integer.valueOf(getValue(row.getCell(4))));

        return entity;
    }

    public static void main(String[] args) {
        LocalSiteMap.readExcel();

        LocalSiteMap.idSiteMap.forEach((k,v) -> {
            System.out.println("k: " + k + " value: " + v);
        });

    }

}
