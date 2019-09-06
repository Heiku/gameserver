package com.ljh.gamedemo.module.equip.dao;

import com.ljh.gamedemo.module.equip.bean.Equip;

import com.ljh.gamedemo.module.equip.bean.RoleEquip;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/24
 */

@Mapper
public interface RoleEquipDao {

    @Insert("insert into role_equip(role_id, equip_id, durability, state, on) values(#{roleId}, #{equipId}, #{durability}, #{state}, #{on})")
    @Options(useGeneratedKeys = true, keyProperty ="id", keyColumn = "id")
    int insertRoleEquip(RoleEquip roleEquip);

    @Select("select * from role_equip")
    List<RoleEquip> selectAllBag();

    @Select("select * from role_equip where has_on = 1")
    List<RoleEquip> selectAllOn();

    @Update("update role_equip set durability = #{durability} and state = #{state} and on = #{on} where id = #{id}")
    int updateRoleEquip(Equip equip);

    @Delete("delete from role_equip where id = #{id}")
    int deleteRoleEquip(long id);
}
