package com.ljh.gamedemo.module.equip.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.goods.local.LocalGoodsMap;
import com.ljh.gamedemo.module.equip.bean.RoleEquip;
import com.ljh.gamedemo.common.CommodityType;
import com.ljh.gamedemo.module.equip.dao.RoleEquipDao;
import com.ljh.gamedemo.module.equip.bean.Equip;
import com.ljh.gamedemo.module.goods.bean.Goods;
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
 * 读取文件, DB中的玩家装备数据
 *
 * @Author: Heiku
 * @Date: 2019/7/18
 */

@Slf4j
public class LocalEquipMap {

    /**
     * 装备文件
     */
    private static File equipFile = null;

    /**
     * db role_equip
     */
    private static RoleEquipDao roleEquipDao;

    /**
     * 所有装备的基本信息 (equipId, Equip)
     */
    private static Map<Long, Equip> idEquipMap = Maps.newConcurrentMap();

    /**
     * 玩家现在穿上的装备 (roleId, List<Equip>)
     */
    private static Map<Long, List<Equip>> roleEquipMap = Maps.newConcurrentMap();

    /**
     * 玩啊拥有的装备 (roleId, List<Equip>)
     */
    private static Map<Long, List<Equip>> hasEquipMap = Maps.newConcurrentMap();

    /**
     * equip typeEquipMap: <Integer, ListM<Equip>>
     */
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


    /**
     * 读取数据文件
     */
    public static void readExcel() throws Exception{
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
                    equip.setMinTrans(Integer.valueOf(getValue(row.getCell(11))));

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
        workbook.close();
        log.info("Equip 数据成功载入");
    }


    /**
     * 读取数据库中的玩家装备关联
     */
    private static void readDB(){
        // 读取数据库中的所有记录信息
        List<RoleEquip> roleEquipList = roleEquipDao.selectAllOn();
        for (RoleEquip re : roleEquipList){
            Equip equip = roleEquipToEquip(re);

            long roleId = re.getRoleId();
            List<Equip> list = roleEquipMap.get(roleId);
            if (list == null){
                list = new ArrayList<>();
            }
            list.add(equip);

            roleEquipMap.put(roleId, list);
        }


        // 读取用户所拥有的全部装备信息
        List<RoleEquip> hasList = roleEquipDao.selectAllBag();
        for (RoleEquip re : hasList) {
            Equip equip = roleEquipToEquip(re);

            List<Equip> list;
            list = hasEquipMap.get(re.getRoleId());
            if (list == null){
                list = new ArrayList<>();
            }
            list.add(equip);
            hasEquipMap.put(re.getRoleId(), list);
        }
    }

    public static Map<Long, Equip> getIdEquipMap() {
        return idEquipMap;
    }

    public static Map<Long, List<Equip>> getRoleEquipMap() {
        return roleEquipMap;
    }

    public static Map<Long, List<Equip>> getHasEquipMap() {
        return hasEquipMap;
    }

    /**
     * 将数据库roleEquip -> equip
     *
     * @param re    玩家装备关联
     * @return      装备实体信息
     */
    private static Equip roleEquipToEquip(RoleEquip re){
        Equip equip = new Equip();
        Equip data = LocalEquipMap.getIdEquipMap().get(re.getEquipId());
        BeanUtils.copyProperties(data, equip);
        equip.setId(re.getId());
        equip.setDurability(re.getDurability());
        equip.setState(re.getState());
        equip.setHasOn(re.getOn());

        return equip;
    }

}
