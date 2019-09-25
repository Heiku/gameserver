package com.ljh.gamedemo.module.email.asyn.run;

import com.ljh.gamedemo.module.email.bean.Email;
import com.ljh.gamedemo.module.email.dao.RoleEmailDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;
import static com.ljh.gamedemo.common.CommonDBType.UPDATE;

/**
 * 玩家邮件数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class RoleEmailSaveRun implements Runnable {

    /**
     * 邮件信息
     */
    private Email email;

    /**
     * 操作类型
     */
    private int type;


    /**
     * RoleEmailDao
     */
    private RoleEmailDao dao = SpringUtil.getBean(RoleEmailDao.class);


    public RoleEmailSaveRun(Email email, int type){
        this.email = email;
        this.type = type;
    }


    @Override
    public void run() {
        int n;
        switch (type){
            case INSERT:
                n = dao.insertEmail(email);
                log.info("insert into email, affected rows: " + n);
                break;
            case UPDATE:
                n = dao.updateEmail(email);
                log.info("update email, affected rows: " + n);
                break;
        }
    }
}
