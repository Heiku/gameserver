package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.common.CommodityType;
import com.ljh.gamedemo.dao.RoleItemsDao;
import com.ljh.gamedemo.entity.Commodity;
import com.ljh.gamedemo.entity.Goods;
import com.ljh.gamedemo.entity.Items;
import com.ljh.gamedemo.entity.dto.RoleItems;
import com.ljh.gamedemo.util.SpringUtil;
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
 * @Author: Heiku
 * @Date: 2019/7/16
 *
 *
 * 用于缓存用户背包内的物品
 */

@Slf4j
public class LocalItemsMap {

    private static File spellFile = null;

    private static RoleItemsDao roleItemsDao;

    // roleItemsMap <roleId, List<Items>>
    private static Map<Long, List<Items>> roleItemsMap = Maps.newConcurrentMap();

    // idItemsMap <itemsId, Items>
    private static Map<Long, Items> idItemsMap = Maps.newConcurrentMap();

    static {
        try {
            roleItemsDao = SpringUtil.getBean(RoleItemsDao.class);
            spellFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/items.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find creepFile,please check file location");
            e.printStackTrace();
        }
    }

    public static void readExcel() {

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(spellFile);

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
                    Items items = new Items();

                    items.setItemsId(Long.valueOf(getValue(row.getCell(0))));
                    items.setType(Integer.valueOf(getValue(row.getCell(1))));
                    items.setName(getValue(row.getCell(2)));
                    items.setUp(Integer.valueOf(getValue(row.getCell(3))));
                    items.setSec(Integer.valueOf(getValue(row.getCell(4))));
                    items.setDesc(getValue(row.getCell(5)));

                    // 存放idItemsMap
                    idItemsMap.put(items.getItemsId(), items);

                    // 背包物品基类
                    Goods goods = new Goods(items.getItemsId(), CommodityType.ITEM.getCode());
                    LocalGoodsMap.getIdGoodsMap().put(goods.getGid(), goods);
                }
            }
        }

        // 存入 roleItemsMap
        List<RoleItems> itemsList = roleItemsDao.selectAllRoleItems();

        List<Items> list;
        // <roleId, items>
        for (RoleItems roleItems : itemsList){
            long roleId = roleItems.getRoleId();

            list = roleItemsMap.get(roleId);
            if (list == null){
                list = new ArrayList<>();
            }

            Items items = idItemsMap.get(roleItems.getObjectsId());
            items.setNum(roleItems.getNum());
            list.add(items);

            roleItemsMap.put(roleId, list);
        }
    }

    public static Map<Long, List<Items>> getRoleItemsMap() {
        return roleItemsMap;
    }

    public static void setRoleItemsMap(Map<Long, List<Items>> roleItemsMap) {
        LocalItemsMap.roleItemsMap = roleItemsMap;
    }

    public static Map<Long, Items> getIdItemsMap() {
        return idItemsMap;
    }

    public static void setIdItemsMap(Map<Long, Items> idItemsMap) {
        LocalItemsMap.idItemsMap = idItemsMap;
    }
}
