package com.ljh.gamedemo.module.site.local;

import com.google.common.base.Strings;
import com.ljh.gamedemo.module.site.bean.Site;
import com.ljh.gamedemo.run.SiteCreepExecutorManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ljh.gamedemo.util.ExcelUtil.formatWorkBook;
import static com.ljh.gamedemo.util.ExcelUtil.getValue;


/**
 * 场景信息数据存储
 */
@Slf4j
public class LocalSiteMap {

    /**
     * 场景信息数据文件
     */
    private static File siteFile = null;

    /**
     * 存储实体的数据 <name, site>
     */
    public static Map<String, Site> siteMap = new ConcurrentHashMap<>();

    /**
     * 存储实体的命名对应 <cName, name>
     */
    public static Map<String, String> nameMap = new HashMap<>();

    /**
     * 存储实体的数据 <id, entity>
     */
    public static Map<Integer, Site> idSiteMap = new ConcurrentHashMap<>();

    static {
        try {
            siteFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/site.xlsx");
        } catch (FileNotFoundException e) {
            log.error("can not find creepFile,please check file location");
            e.printStackTrace();
        }
    }

    /**
     * 载入数据文件
     */
    public static void readExcel() {

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(siteFile);

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
                    // 将地图资源解析成实体类
                    Site site = new Site();
                    site.setId(Integer.valueOf(getValue(row.getCell(0))));
                    site.setName(getValue(row.getCell(1)));
                    site.setCName(getValue(row.getCell(2)));

                    // map存储
                    siteMap.put(site.getName(), site);
                    idSiteMap.put(site.getId(), site);
                    nameMap.put(site.getCName(), site.getName());

                }
            }
        }

        // 再读一次文件，重构对象关联
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(0);

            Site site = null;
            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);

                // 空相邻，直接返回
                if (Strings.isNullOrEmpty(getValue(row.getCell(3)))) {
                    break;
                }

                // 遍历的当前实体类
                String name = getValue(row.getCell(1));
                if (siteMap.containsKey(name)) {
                    site = siteMap.get(name);
                }

                // 解析字符串列表，并添加进去
                if (site == null) {
                    return;
                }
                String[] nextStrArr = getValue(row.getCell(3)).split(",");
                for (String str : nextStrArr) {
                    site.getNext().add(str);
                }
            }
        }
        idSiteMap.forEach((k,v) -> {
            // 为每一个 siteId 绑定一个线程池
            SiteCreepExecutorManager.bindSiteExecutor(k);
        } );

        log.info("site 数据载入成功");
    }
    public static void main(String[] args) {
        readExcel();

        SiteCreepExecutorManager.siteCreepExecutorMap.forEach(
                (k, v) ->
            System.out.println("k: " + k + " ,v: " + v));

    }
}
