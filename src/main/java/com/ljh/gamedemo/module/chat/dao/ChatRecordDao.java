package com.ljh.gamedemo.module.chat.dao;

import com.ljh.gamedemo.module.chat.bean.ChatRecord;
import com.ljh.gamedemo.module.role.bean.RoleState;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * 聊天记录数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/8/6
 */
@Mapper
public interface ChatRecordDao {


    /**
     * 插入聊天记录
     *
     * @param chatRecord    聊天记录信息
     * @return              affected rows
     */
    @Insert("insert into chat_record(id, from_role, to_role, content, send_time) values (#{id}, #{fromRole}, #{toRole}, #{content}, #{sendTime})")
    int insertChatRecord(ChatRecord chatRecord);


    /**
     * 查询玩家的离线消息
     *
     * @param toRoleId          玩家id
     * @param offlineTime       离线时间
     * @return                  离线的聊天记录
     */
    @Select("select * from chat_record where to_role = #{toRoleId} and send_time > #{offlineTime} order by send_time desc")
    List<ChatRecord> selectOfflineMsg(long toRoleId, Date offlineTime);
}
