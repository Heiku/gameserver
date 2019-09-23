package com.ljh.gamedemo.module.equip.dao;

import com.ljh.gamedemo.module.equip.bean.Equip;

import com.ljh.gamedemo.module.equip.bean.RoleEquip;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 玩家装备数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/7/24
 */

@Mapper
public interface RoleEquipDao {

    /**
     * 插入玩家的装备信息
     *
     * @param roleEquip     玩家装备信息
     * @return              affected rows
     */
    @Insert("insert into role_equip(role_id, equip_id, durability, state, has_on) values (#{roleId}, #{equipId}, #{durability}, #{state}, #{on})")
    @Options(useGeneratedKeys = true, keyProperty ="id", keyColumn = "id")
    int insertRoleEquip(RoleEquip roleEquip);


    /**
     * 查询所有的背包装备物品
     *
     * @return  玩家背包中的装备列表
     */
    @Select("select * from role_equip")
    List<RoleEquip> selectAllBag();


    /**
     * 查询所有的玩家已经穿戴上的装备信息
     *
     * @return      已经穿戴的装备列表
     */
    @Select("select * from role_equip where has_on = 1")
    List<RoleEquip> selectAllOn();


    /**
     * 更新玩家的装备信息
     *
     * @param equip     装备信息
     * @return          affected rows
     */
    @Update("update role_equip set durability = #{durability} and state = #{state} and on = #{on} where id = #{id}")
    int updateRoleEquip(Equip equip);


    /**
     * 删除玩家的装备信息
     *
     * @param id        装备信息
     * @return          affected rows
     */
    @Delete("delete from role_equip where id = #{id}")
    int deleteRoleEquip(long id);
}
