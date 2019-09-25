package com.ljh.gamedemo.module.pk.asyn.run;

import com.ljh.gamedemo.module.pk.bean.PKRecord;
import com.ljh.gamedemo.module.pk.dao.RolePKDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;
import static com.ljh.gamedemo.common.CommonDBType.UPDATE;

/**
 * pk记录数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class PKRecordSaveRun implements Runnable {

    /**
     * pk记录信息
     */
    private PKRecord record;

    /**
     * 操作类型
     */
    private int type;

    /**
     * RolePKDao
     */
    private RolePKDao dao = SpringUtil.getBean(RolePKDao.class);

    public PKRecordSaveRun(PKRecord record, int type) {
        this.record = record;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertPKRecord(record);
                log.info("update pk_record, affected rows: " + n);
                break;

            case UPDATE:
                n = dao.updateRecord(record);
                log.info("update pk_record: affect rows: " + n);
                break;
        }
    }
}
