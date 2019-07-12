package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.dao.RoleSpellDao;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.entity.dto.RoleSpell;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.Data;
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
 * 载入技能文件
 *
 * @Author: Heiku
 * @Date: 2019/7/11
 *
 */
@Data
@Slf4j
public class LocalSpellMap {

    private static File spellFile = null;

    // spell idMap: <spellId, Spell>
    private static Map<Integer, Spell> idSpellMap = Maps.newHashMap();

    // spell nameSpellMap: <spellName, Spell>
    private static Map<String, Spell> nameSpellMap = Maps.newHashMap();

    // spell typeSpellMap: <type, List<Spell>>
    // the all spell in this entity type
    private static Map<Integer, List<Spell>> typeSpellMap = Maps.newHashMap();

    // 记录用户的所有技能信息
    private static Map<Long, List<Spell>> roleSpellMap = Maps.newConcurrentMap();


    private static RoleSpellDao roleSpellDao;

    static {
        try {
            roleSpellDao = SpringUtil.getBean(RoleSpellDao.class);
            spellFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/spell.xlsx");
        }catch (FileNotFoundException e){
            log.error("can not find creepFile,please check file location");
            e.printStackTrace();
        }
    }

    public static void readExcel(){

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
                    Spell spell = new Spell();

                    // set spell entity attributes
                    spell.setSpellId(Integer.valueOf(getValue(row.getCell(0))));
                    spell.setName(getValue(row.getCell(1)));
                    spell.setLevel(Integer.valueOf(getValue(row.getCell(2))));
                    spell.setDamage(Integer.valueOf(getValue(row.getCell(3))));
                    spell.setCost(Integer.valueOf(getValue(row.getCell(4))));
                    spell.setCoolDown(Integer.valueOf(getValue(row.getCell(5))));
                    spell.setSchool(Integer.valueOf(getValue(row.getCell(6))));
                    spell.setType(Integer.valueOf(getValue(row.getCell(7))));
                    spell.setRange(Integer.valueOf(getValue(row.getCell(8))));

                    idSpellMap.put(spell.getSpellId(), spell);
                    nameSpellMap.put(spell.getName(), spell);

                    // init typeSpellMap
                    List<Spell> spellList;
                    Integer type = spell.getType();
                    spellList = typeSpellMap.get(type);
                    if (spellList == null){
                        spellList = new ArrayList<>();
                    }
                    spellList.add(spell);

                    typeSpellMap.put(type, spellList);
                }
            }
        }

        readRoleSpell();
    }


    // 将数据库中的所有roleId-spellId 映射到本地map中
    public static void readRoleSpell() {
        List<RoleSpell> roleSpellList = roleSpellDao.selectAllRoleSpell();
        if (roleSpellList == null || roleSpellList.isEmpty()) {
            return;
        }

        for (RoleSpell rs : roleSpellList) {
            // roleId, spellId
            Role role = LocalUserMap.getIdRoleMap().get(rs.getRoleId());
            if (role == null) {
                continue;
            }

            Spell spell = idSpellMap.get(rs.getSpellId());
            if (spell == null) {
                continue;
            }

            // 将数据封装到 map中
            List<Spell> spells;
            spells = roleSpellMap.get(role.getRoleId());
            if (spells == null || spells.isEmpty()){
                spells = new ArrayList<>();
            }
            spells.add(spell);

            roleSpellMap.put(role.getRoleId(), spells);
        }
    }

    public static Map<String, Spell> getNameSpellMap() {
        return nameSpellMap;
    }

    public static void setNameSpellMap(Map<String, Spell> nameSpellMap) {
        LocalSpellMap.nameSpellMap = nameSpellMap;
    }

    public static Map<Integer, Spell> getIdSpellMap() {
        return idSpellMap;
    }

    public static void setIdSpellMap(Map<Integer, Spell> idSpellMap) {
        LocalSpellMap.idSpellMap = idSpellMap;
    }

    public static Map<Integer, List<Spell>> getTypeSpellMap() {
        return typeSpellMap;
    }

    public static void setTypeSpellMap(Map<Integer, List<Spell>> typeSpellMap) {
        LocalSpellMap.typeSpellMap = typeSpellMap;
    }

    public static Map<Long, List<Spell>> getRoleSpellMap() {
        return roleSpellMap;
    }

    public static void setRoleSpellMap(Map<Long, List<Spell>> roleSpellMap) {
        LocalSpellMap.roleSpellMap = roleSpellMap;
    }

    public static void main(String[] args) {
        LocalSpellMap.readExcel();


        roleSpellMap.forEach((k, v) ->{
            System.out.println("k: " + k + " value: " + v);
        });


    }
}
