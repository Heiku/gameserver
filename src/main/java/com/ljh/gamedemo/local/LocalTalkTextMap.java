package com.ljh.gamedemo.local;

import com.ljh.gamedemo.entity.TalkText;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ljh.gamedemo.util.ExcelUtil.formatWorkBook;
import static com.ljh.gamedemo.util.ExcelUtil.getValue;

@Slf4j
public class LocalTalkTextMap {

    private static File talkTextFile = null;

    // npc对话文本 <name,List<entity>>
    private static Map<String, List<TalkText>> talkTextMap = new HashMap<>();

    static {
        try {
            talkTextFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/talk_entity.xlsx");
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    // 获取csv文件信息，得到map集合
    public static void readExcel(){

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(talkTextFile);

        // 遍历获取数据
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                log.error("excel sheet is null, please recheck the creep file resolve");
                break;
            }

            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);
                if (row != null) {

                    String npcName = getValue(row.getCell(0));
                    int level = Integer.valueOf(getValue(row.getCell(1)));

                    // 将实体数据转为实体类
                    TalkText talkText = new TalkText();
                    talkText.setEntityName(npcName);
                    talkText.setLevel(level);
                    talkText.setFirst(Integer.valueOf(getValue(row.getCell(2))));
                    talkText.setContent(getValue(row.getCell(3)));

                    // 先存放<level,entity>
                    List<TalkText> talkTextList = talkTextMap.get(npcName);
                    if (talkTextList == null || talkTextList.isEmpty()){
                        talkTextList = new ArrayList<>();
                    }
                    talkTextList.add(talkText);

                    // 再放big map
                    talkTextMap.put(npcName, talkTextList);
                }
            }
        }
    }

    public static Map<String,List<TalkText>> getTalkTextMap(){
        return talkTextMap;
    }

    public static void setTalkTextMap(Map<String, List<TalkText>> map){
        talkTextMap = map;
    }

    public static void main(String[] args) {

        readExcel();

        talkTextMap.forEach((k,v) -> {
            System.out.println("k: " + k + " v: " + v);
        });
    }

}
