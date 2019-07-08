package com.ljh.gamedemo.local;

import com.google.common.base.Strings;
import com.ljh.gamedemo.entity.Site;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LocalSiteMap {
    private static String charset = "utf-8";

    private static File sitefile = null;

    // 存储实体的数据 <name, site>
    public static Map<String, Site> siteMap = new ConcurrentHashMap<>();

    // 存储实体的命名对应 <cName, name>
    public static Map<String, String> nameMap = new HashMap<>();

    // 存储实体的数据 <id, entity>
    public static Map<Integer, Site> idSiteMap = new ConcurrentHashMap<>();

    static {
        try {
            sitefile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "site.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 将.csv文件解析为实体数据
    public static void readCsv(){
        try {
            CSVReader csvReader = new CSVReaderBuilder(new BufferedReader(
                    new InputStreamReader(new FileInputStream(sitefile), charset))).build();
            Iterator<String[]> iterator = csvReader.iterator();
            while (iterator.hasNext()){
                String[] strArr = iterator.next();

                // 将地图资源解析成实体类
                Site site = new Site();
                site.setId(Integer.valueOf(strArr[0]));
                site.setName(strArr[1]);
                site.setCName(strArr[2]);

                // 解析相邻的site，如果空字符串则跳出
                String nextStr = strArr[3];
                if (Strings.isNullOrEmpty(nextStr)){
                    break;
                }

                siteMap.put(site.getName(), site);
                idSiteMap.put(site.getId(), site);
                nameMap.put(site.getCName(), site.getName());
            }

            fillSiteBeanNext();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 补充场景间的实体关系 List
    public static void fillSiteBeanNext(){
        try {
            CSVReader reader = new CSVReaderBuilder(new BufferedReader(
                    new InputStreamReader(new FileInputStream(sitefile), charset))).build();
            Iterator<String[]> iterator = reader.iterator();

            Site site = null;
            while (iterator.hasNext()){
                String[] strArr = iterator.next();

                // 空相邻，直接返回
                if (Strings.isNullOrEmpty(strArr[3])){
                    break;
                }

                // 遍历的当前实体类
                String name = strArr[1];
                if (siteMap.containsKey(name)){
                    site = siteMap.get(name);
                }

                // 解析字符串列表，并添加进去
                String[] nextStrArr = strArr[3].split(",");
                for (String str : nextStrArr){
                    site.getNext().add(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        readCsv();
/*
        for (Map.Entry<String, Site> entry : siteMap.entrySet()){
            System.out.println("key= " + entry.getKey() + " values= " + entry.getValue());
        }*/

        //System.out.println(ResourceUtils.CLASSPATH_URL_PREFIX);
    }
}
