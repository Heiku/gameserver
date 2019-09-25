package com.ljh.gamedemo.module.user.asyn.run;

import com.ljh.gamedemo.module.user.bean.UserAccount;
import com.ljh.gamedemo.module.user.dao.UserDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;

/**
 * 用户数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class UserSaveRun implements Runnable {

    /**
     * 用户信息
     */
    private UserAccount account;

    /**
     * 操作类型
     */
    private int type;

    /**
     * UserDoa
     */
    private UserDao dao = SpringUtil.getBean(UserDao.class);

    public UserSaveRun(UserAccount account, int type) {
        this.account = account;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertUserAccount(account);
                log.info("insert into user_account, affected rows: " + n);
                break;
        }
    }
}
