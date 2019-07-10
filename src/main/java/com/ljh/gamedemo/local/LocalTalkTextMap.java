package com.ljh.gamedemo.local;

import com.ljh.gamedemo.entity.TalkText;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class LocalTalkTextMap {

    private static String charset = "utf-8";

    private static File talkTestFile = null;

    // npc对话文本 <name,List<entity>>
    private static Map<String, List<TalkText>> talkTextMap = new HashMap<>();

    static {
        try {
            talkTestFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "talk_entity.csv");
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }


    // 获取csv文件信息，得到map集合
    public static void readCsv(){
        try {
            CSVReader csvReader = new CSVReaderBuilder(
                    new BufferedReader(new FileReader(talkTestFile))).build();
            Iterator<String[]> iterator = csvReader.iterator();
            while (iterator.hasNext()){
                String[] strArr = iterator.next();

                String npcName = strArr[0];
                int level = Integer.valueOf(strArr[1]);

                // 将实体数据转为实体类
                TalkText talkText = new TalkText();
                talkText.setEntityName(npcName);
                talkText.setLevel(level);
                talkText.setFirst(Integer.valueOf(strArr[2]));
                talkText.setContent(strArr[3]);

                // 先存放<level,entity>
                List<TalkText> talkTextList = talkTextMap.get(npcName);
                if (talkTextList == null || talkTextList.isEmpty()){
                    talkTextList = new ArrayList<>();
                }
                talkTextList.add(talkText);

                // 再放big map
                talkTextMap.put(npcName, talkTextList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Map<String,List<TalkText>> getTalkTextMap(){
        return talkTextMap;
    }

    public static void setTalkTextMap(Map<String, List<TalkText>> map){
        talkTextMap = map;
    }

}
