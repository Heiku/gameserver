package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserRoleDao {

    @Insert("insert into role(user_id, role_id, site_id, type, name, level, alive, hp, max_hp, mp, gold, honor) values(#{userId}, #{roleId}, " +
            "#{siteId}, #{type}, #{name}, #{level}, #{alive}, #{hp}, #{maxHp}, #{mp}, #{gold}, #{honor})")
    @Options(useGeneratedKeys = true, keyProperty = "roleId", keyColumn = "role_id")
    int insertUserRole(Role role);

    @Select("select * from role where user_id = #{userId}")
    List<Role> selectUserRole(long userId);

    @Select("select * from role where role_id = #{roleId}")
    Role selectRoleByRoleId(long roleId);

    @Select("select * from role where site_id = #{siteId}")
    List<Role> selectRoleBySiteId(int siteId);

    @Select("select * from role")
    List<Role> selectAllRole();

    @Update("update role set site_id = #{siteId},level = #{level},hp = #{hp},mp = #{mp},gold = #{gold},honor = #{honor} where user_id = #{userId} and role_id = #{roleId}")
    int updateRoleSiteInfo(Role role);
}
