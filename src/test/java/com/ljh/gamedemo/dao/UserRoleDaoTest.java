package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRoleDaoTest {

    @Autowired
    private UserRoleDao userRoleDao;

    @Test
    public void insertUserRole(){
        int m = userRoleDao.insertUserRole(1004, 10003, 2, 103, "我是小菜鸡", 20, 1);
        int n = userRoleDao.insertUserRole(1005, 10004, 3, 111, "哈哈哈哈哈哈哈", 90, 1);

        System.out.println(m + " " + n);
    }


    @Test
    public void queryUserRole(){
        List<Role> roles = userRoleDao.selectRoleBySiteId(3);
        System.out.println(roles);
    }
}
