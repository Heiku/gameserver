package com.ljh.gamedemo.module.role.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.role.bean.RoleInit;
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
 * 载入玩家职业的初始化信息
 *
 * @Author: Heiku
 * @Date: 2019/8/19
 */
@Slf4j
public class LocalRoleInitMap {

    /**
     * 职业初始化表文件
     */
    private static File bossInitFile = null;

    /**
     * 职业初始化数据map <type, RoleInit>
     */
    private static Map<Integer, RoleInit> roleInitMap = Maps.newHashMap();


    static {
        try {
            bossInitFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/role_init.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find pkRewardFile,please check file location");
            e.printStackTrace();
        }
    }

    /**
     * 读取文件，载入信息
     */
    public static void readExcel() throws Exception{

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(bossInitFile);

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
                    RoleInit roleInit = new RoleInit();

                    roleInit.setType(Integer.valueOf(getValue(row.getCell(0))));
                    roleInit.setName(getValue(row.getCell(1)));
                    roleInit.setHp(Integer.valueOf(getValue(row.getCell(2))));
                    roleInit.setMp(Integer.valueOf(getValue(row.getCell(3))));
                    roleInit.setDesc(getValue(row.getCell(4)));

                    roleInitMap.put(roleInit.getType(), roleInit);
                }
            }
        }

        workbook.close();
        log.info("RoleInit 数据载入成功");
    }

    public static Map<Integer, RoleInit> getRoleInitMap() {
        return roleInitMap;
    }
}
