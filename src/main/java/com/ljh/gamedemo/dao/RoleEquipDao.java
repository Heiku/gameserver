package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.dto.RoleEquip;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/24
 */

@Mapper
public interface RoleEquipDao {

    @Insert("insert into role_equip(role_id, equip_id, durability, state) values(#{roleId}, #{equipId}, #{durability}, #{state})")
    @Options(useGeneratedKeys = true, keyProperty ="id", keyColumn = "id")
    int insertRoleEquip(RoleEquip roleEquip);

    @Select("select * from role_equip where role_id = #{roleId}")
    List<RoleEquip> selectAllRoleEquip(long roleId);

    @Select("select * from role_equip where role_id = #{roleId} and equip_id = #{equipId}")
    RoleEquip selectRoleEquip(long roleId, long equipId);

    @Delete("delete from role_equip where equip_id = #{equipId} and role_id = #{roleId}")
    int deleteRoleEquip(long equipId, long roleId);

    @Update("update role_equip set durability = #{durability}, state = #{state} where equip_id = #{equipId} and role_id = #{roleId}")
    int updateRoleEquip(RoleEquip roleEquip);

    @Select("select * from role_equip")
    List<RoleEquip> selectAll();
}
