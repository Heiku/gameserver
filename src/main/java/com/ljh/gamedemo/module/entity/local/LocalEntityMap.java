package com.ljh.gamedemo.module.entity.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.entity.bean.Entity;
import com.ljh.gamedemo.module.site.bean.Site;
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

import static com.ljh.gamedemo.util.ExcelUtil.formatWorkBook;
import static com.ljh.gamedemo.util.ExcelUtil.getValue;

/**
 * 实体数据本地存储
 */

@Slf4j
public class LocalEntityMap {

    /**
     *  实体数据文件
     */
    private static File entityFile;

    /**
     * 存储实体的数据 <entityName, entity>
     */
    public static Map<String, Entity> entityMap = Maps.newHashMap();

    /**
     * 存储实体的位置数据 <siteName, List<Entity>>
     */
    public static Map<String, List<Entity>> siteEntityMap = Maps.newHashMap();

    static {
        try {
            entityFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/entity.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find creepFile,please check file location");
            e.printStackTrace();
        }
    }

    /**
     * 载入数据文件
     */
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
                    transformEntity(entity, row);

                    // npc的场景id，并设置entity的位置信息
                    int destId = Integer.valueOf(getValue(row.getCell(5)));
                    if (!LocalSiteMap.idSiteMap.containsKey(destId)) {
                        entity.setSite(null);
                    }
                    Site site = LocalSiteMap.idSiteMap.get(destId);
                    entity.setSite(site);

                    // 另存一张映射表，用于表示场景中的实体信息
                    List<Entity> entityList;
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
        log.info("Entity 数据载入成功");
    }


    public static Map<String, Entity> getEntityMap() {
        return entityMap;
    }

    public static Map<String, List<Entity>> getSiteEntityMap() {
        return siteEntityMap;
    }

    private static Entity transformEntity(Entity entity, Row row) {
        entity.setId(Long.valueOf(getValue(row.getCell(0))));
        entity.setType(Integer.valueOf(getValue(row.getCell(1))));
        entity.setName(getValue(row.getCell(2)));
        entity.setLevel(Integer.valueOf(getValue(row.getCell(3))));
        entity.setAlive(Integer.valueOf(getValue(row.getCell(4))));

        return entity;
    }

}
