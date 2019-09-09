package com.ljh.gamedemo.module.role.dao;

import com.ljh.gamedemo.module.role.bean.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 玩家角色数据库操作
 */

@Mapper
public interface UserRoleDao {

    /**
     * 插入玩家的角色信息
     *
     * @param role      玩家信息
     * @return          affected rows
     */
    @Insert("insert into role(user_id, role_id, site_id, type, name, level, alive, hp, max_hp, mp, gold, honor) values(#{userId}, #{roleId}, " +
            "#{siteId}, #{type}, #{name}, #{level}, #{alive}, #{hp}, #{maxHp}, #{mp}, #{gold}, #{honor})")
    @Options(useGeneratedKeys = true, keyProperty = "roleId", keyColumn = "role_id")
    int insertUserRole(Role role);


    /**
     * 查询玩家角色列表
     *
     * @param userId    用户id
     * @return          玩家的角色列表
     */
    @Select("select * from role where user_id = #{userId}")
    List<Role> selectUserRole(long userId);


    /**
     * 查询玩家的角色信息
     *
     * @param roleId    角色id
     * @return          玩家角色信息
     */
    @Select("select * from role where role_id = #{roleId}")
    Role selectRoleByRoleId(long roleId);


    /**
     * 查询指定场景下的所有角色信息
     *
     * @param siteId        场景id
     * @return              玩家角色列表
     */
    @Select("select * from role where site_id = #{siteId}")
    List<Role> selectRoleBySiteId(int siteId);


    /**
     * 查询所有的玩家列表
     *
     * @return      玩家列表
     */
    @Select("select * from role")
    List<Role> selectAllRole();


    /**
     * 更新玩家的位置信息
     *
     * @param role      玩家信息
     * @return          affected rows
     */
    @Update("update role set site_id = #{siteId},level = #{level},hp = #{hp},mp = #{mp},gold = #{gold},honor = #{honor} where user_id = #{userId} and role_id = #{roleId}")
    int updateRoleSiteInfo(Role role);
}
