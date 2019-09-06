package com.ljh.gamedemo.module.pk.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.pk.bean.PKReward;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import static com.ljh.gamedemo.util.ExcelUtil.formatWorkBook;
import static com.ljh.gamedemo.util.ExcelUtil.getValue;

/**
 * @Author: Heiku
 * @Date: 2019/8/12
 */

@Slf4j
public class LocalPKRewardMap {

    /**
     * 玩家pk奖励文件
     */
    private static File pkRewardFile = null;

    /**
     * pk reward map
     */
    private static Map<Integer, PKReward> pkLevelRewardMap = Maps.newConcurrentMap();


    static {
        try {
            pkRewardFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/pk_reward.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find pkRewardFile,please check file location");
            e.printStackTrace();
        }
    }

    public static void readExcel() {

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(pkRewardFile);

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
                    PKReward pkReward = new PKReward();

                    pkReward.setId(Long.valueOf(getValue(row.getCell(0))));
                    pkReward.setWin(Integer.valueOf(getValue(row.getCell(1))));
                    pkReward.setLose(Integer.valueOf(getValue(row.getCell(2))));
                    pkReward.setLevel(Integer.valueOf(getValue(row.getCell(3))));

                    pkLevelRewardMap.put(pkReward.getLevel(), pkReward);
                }
            }
        }
    }

    public static Map<Integer, PKReward> getPkLevelRewardMap() {
        return pkLevelRewardMap;
    }
}
