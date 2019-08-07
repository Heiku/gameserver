package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.ChatRecord;
import com.ljh.gamedemo.entity.RoleState;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/8/6
 */
@Mapper
public interface ChatRecordDao {

    @Insert("insert into chat_record(id, from_role, to_role, content, send_time) values (#{id}, #{fromRole}, #{toRole}, #{content}, #{sendTime})")
    int insertChatRecord(ChatRecord chatRecord);

    @Select("select * from chat_record where to_role = #{toRoleId} and send_time > #{offlineTime} order by send_time desc")
    List<ChatRecord> selectOfflineMsg(long toRoleId, Date offlineTime);

    // 记录玩家的离线时间
    @Insert("insert into role_state(role_id, online_time, offline_time) values (#{roleId}, #{onlineTime}, #{offlineTime})")
    int insertUserState(RoleState roleState);

    @Update("update role_state set online_time = #{onlineTime}, offline_time = #{offlineTime} where role_id = #{roleId}")
    int updateUserState(RoleState roleState);

    @Select("select * from role_state where role_id = #{roleId}")
    RoleState selectUserOffline(long roleId);

    @Select(("select * from role_state"))
    List<RoleState> selectAllRoleState();
}
