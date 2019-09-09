package com.ljh.gamedemo.module.role.dao;

import com.ljh.gamedemo.module.role.bean.RoleAttr;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 玩家的属性数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/7/24
 */

@Mapper
public interface RoleAttrDao {


    /**
     * 插入玩家的属性记录
     *
     * @param roleAttr      玩家的属性信息
     * @return              affected rows
     */
    @Insert("insert into role_attr(role_id, damage, sp, hp, armor) values (#{roleId}, #{damage}, #{sp}, #{hp}, #{armor})")
    int insertRoleAttr(RoleAttr roleAttr);



    /**
     * 查询所有玩家的属性列表
     *
     * @return      玩家的属性列表
     */
    @Select("select * from role_attr")
    List<RoleAttr> selectAllAttr();


    /**
     * 查询玩家的属性信息
     *
     * @param roleId        玩家id
     * @return              玩家属性信息
     */
    @Select("select * from role_attr where role_id = #{roleId}")
    RoleAttr selectAttrById(long roleId);


    /**
     * 更新玩家的属性信息
     *
     * @param attr      玩家属性信息
     * @return          affected rows
     */
    @Update("update role_attr set damage = #{damage}, sp = #{sp}, hp = #{hp}, armor = #{armor} where role_id = #{roleId}")
    int updateRoleAttr(RoleAttr attr);
}
