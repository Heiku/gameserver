package com.ljh.gamedemo.module.role.dao;

import com.ljh.gamedemo.module.role.bean.RoleState;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 玩家状态操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Mapper
public interface RoleStateDao {


    /**
     * 记录玩家的离线时间
     *
     * @param roleState     玩家在线状态
     * @return              affected rows
     */
    @Insert("insert into role_state(role_id, online_time, offline_time) values (#{roleId}, #{onlineTime}, #{offlineTime})")
    int insertUserState(RoleState roleState);


    /**
     * 更新玩家的上线离线状态
     *
     * @param roleState     玩家在线状态
     * @return              affected rows
     */
    @Update("update role_state set online_time = #{onlineTime}, offline_time = #{offlineTime} where role_id = #{roleId}")
    int updateUserState(RoleState roleState);


    /**
     * 查询玩家的在线状态
     *
     * @param roleId        玩家id
     * @return              玩家在线状态
     */
    @Select("select * from role_state where role_id = #{roleId}")
    RoleState selectUserOffline(long roleId);


    /**
     * 查询所有玩家的在线状态
     *
     * @return      玩家在线状态列表
     */
    @Select(("select * from role_state"))
    List<RoleState> selectAllRoleState();
}
