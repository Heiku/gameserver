package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Commodity;
import com.ljh.gamedemo.entity.Equip;
import com.ljh.gamedemo.entity.Goods;
import com.ljh.gamedemo.entity.Items;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.ljh.gamedemo.common.CommodityType.EQUIP;
import static com.ljh.gamedemo.common.CommodityType.ITEM;
import static com.ljh.gamedemo.util.ExcelUtil.formatWorkBook;
import static com.ljh.gamedemo.util.ExcelUtil.getValue;

/**
 * @Author: Heiku
 * @Date: 2019/8/2
 *
 * 本地加载商城物品信息
 */

@Slf4j
public class LocalCommodityMap {

    // 物品的刷新量
    private static final int ITEM_NUM = 3;

    // 装备的刷新量
    private static final int EQUIP_NUM = 2;

    // 刷新数组长度
    private static int MAX_ITEMS = 0;

    private static int MAX_EQUIPS = 0;

    // 用于存放概率物品
    private static long[] items;

    private static List<Long> equips;

    // 本地文件
    private static File mallFile = null;


    // 本本地缓存
    private static Map<Long, Commodity> idCommodityMaps = Maps.newConcurrentMap();

    private static Map<Integer, List<Commodity>> typeCommodityMaps = Maps.newConcurrentMap();


    // 存放公共背包物品信息
    private static Map<Long, Goods> idGoodsMap = Maps.newConcurrentMap();

    static {
        try {
            mallFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/mall.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find mallFile,please check file location");
            e.printStackTrace();
        }
    }

    public static void readExcel() throws Exception {

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(mallFile);

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
                    Commodity c = new Commodity();

                    c.setId(Long.valueOf(getValue(row.getCell(0))));
                    c.setType(Integer.valueOf(getValue(row.getCell(1))));
                    c.setPrice(Integer.valueOf(getValue(row.getCell(2))));
                    c.setProbability(Double.valueOf(getValue(row.getCell(3))));
                    c.setLimit(Integer.valueOf(getValue(row.getCell(4))));

                    // 独立存储
                    idCommodityMaps.put(c.getId(), c);


                    // 分类存储
                    List<Commodity> list;
                    list = typeCommodityMaps.get(c.getType());
                    if (list == null){
                        list = new ArrayList<>();
                    }
                    list.add(c);
                    typeCommodityMaps.put(c.getType(), list);
                }
            }
        }

        initCommodity();
    }



    private static void initCommodity(){

        // 获取商店栏中各种类的值
        typeCommodityMaps.forEach((k, v) -> {
            if (k.intValue() == ITEM.getCode()){
                for (Commodity c : v) {
                    MAX_ITEMS += c.getProbability() * 10;
                }
            }else if (k.intValue() == EQUIP.getCode()){
                for (Commodity c : v){
                    MAX_EQUIPS += c.getProbability() * 10;
                }
            }
        });

        // 初始化刷新数组
        items = new long[MAX_ITEMS];
        equips = new ArrayList<>();

        int idx = 0;
        List<Commodity> itemList = typeCommodityMaps.get(ITEM.getCode());
        for (Commodity c : itemList) {
            int num = (int) (c.getProbability() * 10);
            int max = (int) idx + num;
            while (idx < max){
                items[idx++] = c.getId();
            }
        }

        idx = 0;
        List<Commodity> equipList = typeCommodityMaps.get(EQUIP.getCode());
        for (Commodity c : equipList) {
            int num = (int) (c.getProbability() * 10);
            int max = idx + num;
            while (idx < max){
                equips.add(c.getId());
                idx++;
            }
        }
    }


    // 随机刷新物品信息
    public static List<Items> getItemsList(){
        List<Items> list = new ArrayList<>();

        for (int i = 0; i < ITEM_NUM; i++){
            int r = new Random().nextInt(MAX_ITEMS);
            long itemId = items[r];

            Items data = new Items();
            Items tmp = LocalItemsMap.getIdItemsMap().get(itemId);
            BeanUtils.copyProperties(tmp, data);
            data.setNum(1);

            list.add(data);
        }
        return list;
    }

    // 随机刷新装备
    public static List<Equip> getEquipsList(long roleId){

        // 去除用户已经拥有的装备信息
        List<Equip> list = new ArrayList<>();
        List<Long> out = new ArrayList<>(equips);
        if (roleId != 0) {
            List<Equip> hasList = LocalEquipMap.getHasEquipMap().get(roleId);
            hasList.forEach(e -> {
                out.remove(e.getEquipId());
            });
        }

        // 随机获取
        for (int i = 0; i < EQUIP_NUM; i++){
            int r = new Random().nextInt(out.size());
            long equipId = out.get(r);

            Equip data = new Equip();
            Equip tmp = LocalEquipMap.getIdEquipMap().get(equipId);
            BeanUtils.copyProperties(tmp, data);

            list.add(data);
        }
        return list;
    }



    public static Map<Long, Commodity> getIdCommodityMaps() {
        return idCommodityMaps;
    }

    public static Map<Integer, List<Commodity>> getTypeCommodityMaps() {
        return typeCommodityMaps;
    }

    public static void main(String[] args) throws Exception {
        readExcel();

        idCommodityMaps.forEach((k, v) -> {
            System.out.println("k: " + k + " v: " + v);
        });

        typeCommodityMaps.forEach((k, v) -> {
            System.out.println("k: " + k + " v: " + v);
        });

        for (long item : items) {
            System.out.print(item + " ");
        }
        System.out.println();

        for (long equip : equips) {
            System.out.print(equip + " ");
        }

        System.out.println(LocalCommodityMap.getEquipsList(1002L));
        System.out.println(LocalCommodityMap.getItemsList());
    }
}
