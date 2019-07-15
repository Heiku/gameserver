package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.Role;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserRoleDao {

    @Insert("insert into role(user_id, role_id, site_id, type, name, level, alive) values(#{userId}, #{roleId}, " +
            "#{siteId}, #{type}, #{name}, #{level}, #{alive})")
    int insertUserRole(long userId, long roleId, int siteId, int type, String name, int level, int alive);

    @Select("select * from role where user_id = #{userId}")
    List<Role> selectUserRole(long userId);

    @Select("select * from role where role_id = #{roleId}")
    Role selectRoleByRoleId(long roleId);

    @Select("select * from role where site_id = #{siteId}")
    List<Role> selectRoleBySiteId(int siteId);

    @Select("select * from role")
    List<Role> selectAllRole();

    @Update("update role set site_id = #{siteId},level = #{level},hp = #{hp},mp = #{mp} where user_id = #{userId} and role_id = #{roleId}")
    int updateRoleSiteInfo(long userId, long roleId, int siteId, int level, int hp, int mp);
}
