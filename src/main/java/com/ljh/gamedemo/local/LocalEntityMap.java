package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.dao.UserRoleDao;
import com.ljh.gamedemo.entity.Entity;
import com.ljh.gamedemo.entity.Site;
import com.ljh.gamedemo.util.SpringUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class LocalEntityMap {

    @Autowired
    private static UserRoleDao userRoleDao;

    static {
        userRoleDao = SpringUtil.getBean(UserRoleDao.class);
    }

    private static String srcPath = "src/main/resources/entity.csv";
    private static String charset = "utf-8";

    // 存储实体的数据 <name, entity>
    public static Map<String, Entity> entityMap = Maps.newHashMap();

    // 存储实体的位置数据 <siteName, List<Entity>>
    public static Map<String, List<Entity>> entitySiteMap = Maps.newHashMap();

    // 将.csv文件解析成实体数据
    public static void readCsv(){
        try {
            CSVReader csvReader = new CSVReaderBuilder(new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File(srcPath)), charset))).build();
            Iterator<String[]> iterator = csvReader.iterator();
            while (iterator.hasNext()) {
                String[] strArr = iterator.next();

                Entity entity = new Entity();
                entity = transformEntity(entity, strArr);

                // npc的场景id，并设置entity的位置信息
                int destId = Integer.valueOf(strArr[5]);
                if (!LocalSiteMap.idSiteMap.containsKey(destId)){
                    entity.setSite(null);
                }
                Site site = LocalSiteMap.idSiteMap.get(destId);
                entity.setSite(site);

                // 另存一张映射表，用于表示场景中的实体信息
                List<Entity> entityList;
                String siteName = site.getName();
                if (!entitySiteMap.containsKey(siteName)){
                    entityList = new ArrayList<>();
                    entityList.add(entity);
                }else {
                    entityList = entitySiteMap.get(site.getName());
                    entityList.add(entity);
                }
                entitySiteMap.put(site.getName(), entityList);

                // 实体映射表
                entityMap.put(entity.getName(), entity);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private static Entity transformEntity(Entity entity, String[] entityStr){
        entity.setId(Integer.valueOf(entityStr[0]));
        entity.setType(Integer.valueOf(entityStr[1]));
        entity.setName(entityStr[2]);
        entity.setLevel(Integer.valueOf(entityStr[3]));
        entity.setAlive(Integer.valueOf(entityStr[4]));

        return entity;
    }

    public static void main(String[] args) {
        /*LocalSiteMap.readCsv();
        readCsv();

        for (Map.Entry<String, List<Entity>> entry : entitySiteMap.entrySet()){
            System.out.println("key= " + entry.getKey() + "  value= " + entry.getValue());
        }*/

    }
}
