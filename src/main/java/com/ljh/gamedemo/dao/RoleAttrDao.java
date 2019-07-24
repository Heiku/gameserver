package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.dto.RoleAttr;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/24
 */

@Mapper
public interface RoleAttrDao {

    @Insert("insert into role_attr(role_id, damage, sp, hp, armor) values (#{roleId}, #{damage}, #{sp}, #{hp}, #{armor})")
    int insertRoleAttr(RoleAttr roleAttr);

    @Select("select * from role_attr")
    List<RoleAttr> selectAllAttr();

    @Select("select * from role_attr where role_id = #{roleId}")
    RoleAttr selectAttrById(long roleId);

    @Update("update ")
    int updateRoleAttr(RoleAttr attr);
}
