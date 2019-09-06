package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.module.site.dao.UserSiteDao;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserSiteDaoTest {

    @Autowired
    private UserSiteDao userSiteDao;

    public void insertUserSite(){
        long userId = 1004;
    }
}
