package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.dto.RoleItems;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/16
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleItemsDaoTest {

    @Autowired
    private RoleItemsDao roleItemsDao;

    @Test
    public void testInsert(){
        int n = roleItemsDao.insertRoleItems(10002, 10002, 1);
        System.out.println(n);
    }

    @Test
    public void testSelectAll(){
        List<RoleItems> r = roleItemsDao.selectAllRoleItems();
        System.out.println(r);
    }
}
