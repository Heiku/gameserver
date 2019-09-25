package com.ljh.gamedemo.module.chat.asyn.run;

import com.ljh.gamedemo.module.chat.bean.ChatRecord;
import com.ljh.gamedemo.module.chat.dao.ChatRecordDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.*;

/**
 * 聊天记录数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */
@Slf4j
public class ChatRecordSaveRun implements Runnable {

    /**
     * 聊天信息
     */
    private ChatRecord chatRecord;

    /**
     * 操作类型
     */
    private int type;

    /**
     * ChatRecordDao
     */
    private ChatRecordDao dao = SpringUtil.getBean(ChatRecordDao.class);


    public ChatRecordSaveRun(ChatRecord cr, int type){
        this.chatRecord = cr;
        this.type = type;
    }


    @Override
    public void run() {
        switch (type){
            case INSERT:
                int n = dao.insertChatRecord(chatRecord);
                log.info("insert into chat_record, affected rows: " + n);
                break;
        }
    }
}
