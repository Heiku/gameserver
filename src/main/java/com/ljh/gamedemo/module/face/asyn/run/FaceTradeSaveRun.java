package com.ljh.gamedemo.module.face.asyn.run;

import com.ljh.gamedemo.module.face.bean.Transaction;
import com.ljh.gamedemo.module.face.dao.FaceTransDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.*;

/**
 * 面对面交易数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */
@Slf4j
public class FaceTradeSaveRun implements Runnable {

    /**
     * 面对面交易信息
     */
    private Transaction tran;

    /**
     * 操作类型
     */
    private int type;

    /**
     * FaceTransDao
     */
    private FaceTransDao dao = SpringUtil.getBean(FaceTransDao.class);


    public FaceTradeSaveRun(Transaction tran, int type){
        this.tran = tran;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertFaceTrans(tran);
                log.info("insert into face_trans, affected rows: " + n);
                break;

            case UPDATE:
                n = dao.updateFaceTrans(tran);
                log.info("update face_trans, affected rows: " + n);
                break;

            case DELETE:
                n = dao.deleteFaceTrans(tran);
                log.info("delete from face_trans, affected rows: " + n);
                break;
        }
    }
}
