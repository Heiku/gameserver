package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.module.user.dao.UserDao;
import com.ljh.gamedemo.util.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void selectUser() {
    }

    @Test
    public void insertUserAccount() {
        String userName = "mystic";
        String password = MD5Util.hashPwd("sise");

        int n = userDao.insertUserAccount(0, userName, password);
        System.out.println(n);
    }


    @Test
    public void queryUser(){
        /*User user = userDao.selectUser("heiku_test", MD5Util.hashPwd("sise_test"));
        System.out.println(user);*/
    }
}