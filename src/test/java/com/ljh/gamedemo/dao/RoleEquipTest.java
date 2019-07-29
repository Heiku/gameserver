package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.Equip;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.dto.RoleEquip;
import com.ljh.gamedemo.entity.dto.RoleEquipHas;
import com.ljh.gamedemo.local.LocalEquipMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/7/29
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleEquipTest {

    @Autowired
    private RoleEquipDao roleEquipDao;

    @Test
    public void insertIntoRoleEquipHas() throws Exception{
        LocalEquipMap.readExcel();
        Map<Long, Equip> idMaps = LocalEquipMap.getIdEquipMap();
        idMaps.forEach((k, v) -> {
            RoleEquipHas r = new RoleEquipHas();
            r.setRoleId(10002L);
            r.setEquipId(k);

            int n = roleEquipDao.addHasEquips(r);
            System.out.println("插入条数：" + n);
            System.out.println("插入后的id为：" + r.getId());
        });
    }
}
