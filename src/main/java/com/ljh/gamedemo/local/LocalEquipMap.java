package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.common.CommodityType;
import com.ljh.gamedemo.dao.RoleEquipDao;
import com.ljh.gamedemo.entity.Equip;
import com.ljh.gamedemo.entity.Goods;
import com.ljh.gamedemo.entity.dto.RoleEquip;
import com.ljh.gamedemo.entity.dto.RoleEquipHas;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
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
 * @Date: 2019/7/18
 */

@Slf4j
public class LocalEquipMap {

    private static File equipFile = null;

    private static RoleEquipDao roleEquipDao;

    // equip idEquipMap: <Long, Equip>
    private static Map<Long, Equip> idEquipMap = Maps.newConcurrentMap();

    private static Map<Long, List<RoleEquip>> roleEquipMap = Maps.newConcurrentMap();

    private static Map<Long, List<Equip>> hasEquipMap = Maps.newConcurrentMap();


    // 暂时用不着
    // equip typeEquipMap: <Integer, ListM<Equip>>
    private static Map<Integer, List<Equip>> typeEquipMap = Maps.newHashMap();

    static {
        try {
            roleEquipDao = SpringUtil.getBean(RoleEquipDao.class);
            equipFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/equip.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find creepFile,please check file location");
            e.printStackTrace();
        }
    }

    public static void readExcel() throws Exception {

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(equipFile);

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
                    Equip equip = new Equip();

                    equip.setEquipId(Long.valueOf(getValue(row.getCell(0))));
                    equip.setName(getValue(row.getCell(1)));
                    equip.setType(Integer.valueOf(getValue(row.getCell(2))));
                    equip.setPart(Integer.valueOf(getValue(row.getCell(3))));
                    equip.setLevel(Integer.valueOf(getValue(row.getCell(4))));
                    equip.setAUp(Integer.valueOf(getValue(row.getCell(5))));
                    equip.setSpUp(Integer.valueOf(getValue(row.getCell(6))));
                    equip.setHpUp(Integer.valueOf(getValue(row.getCell(7))));
                    equip.setArmor(Integer.valueOf(getValue(row.getCell(8))));
                    equip.setDurability(Integer.valueOf(getValue(row.getCell(9))));
                    equip.setState(Integer.valueOf(getValue(row.getCell(10))));

                    idEquipMap.put(equip.getEquipId(), equip);

                    // init typeEquipMap
                    List<Equip> equipList;
                    Integer type = equip.getType();
                    equipList = typeEquipMap.get(type);
                    if (equipList == null){
                        equipList = new ArrayList<>();
                    }
                    equipList.add(equip);
                    typeEquipMap.put(type, equipList);

                    // 背包物品基类
                    Goods goods = new Goods(equip.getEquipId(), CommodityType.EQUIP.getCode());
                    LocalGoodsMap.getIdGoodsMap().put(goods.getGid(), goods);
                }
            }
        }

        readDB();
    }


    private static void readDB() throws Exception{
        // 读取数据库中的所有记录信息
        List<RoleEquip> roleEquipList = roleEquipDao.selectAll();
        for (RoleEquip re : roleEquipList){
            long roleId = re.getRoleId();

            List<RoleEquip> list = roleEquipMap.get(roleId);
            if (list == null){
                list = new ArrayList<>();
            }
            list.add(re);

            roleEquipMap.put(roleId, list);
        }


        // 读取用户所拥有的全部装备信息
        List<RoleEquipHas> hasList = roleEquipDao.selectAllHasEquip();
        for (RoleEquipHas reh : hasList) {
            long equipId = reh.getEquipId();
            Equip equip = new Equip();
            Equip data = idEquipMap.get(equipId);
            BeanUtils.copyProperties(data, equip);


            List<Equip> list;
            list = hasEquipMap.get(reh.getRoleId());
            if (list == null){
                list = new ArrayList<>();
            }
            list.add(equip);
            hasEquipMap.put(reh.getRoleId(), list);
        }
    }

    public static Map<Long, Equip> getIdEquipMap() {
        return idEquipMap;
    }


    public static Map<Long, List<RoleEquip>> getRoleEquipMap() {
        return roleEquipMap;
    }

    public static Map<Long, List<Equip>> getHasEquipMap() {
        return hasEquipMap;
    }

    public static void main(String[] args) {
        try {
            readExcel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        idEquipMap.forEach((k, v) -> {
            System.out.println("k: " + k + " ,value: " + v);
        });

        typeEquipMap.forEach((k, v) -> {
            System.out.println("k: " + k + " ,value: " + v);
        });

        hasEquipMap.forEach((k, v) -> {
            System.out.println("k: " + k + " ,value: " + v);
        });
    }

}
