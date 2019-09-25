package com.ljh.gamedemo.module.spell.dao;

import com.ljh.gamedemo.module.spell.bean.RoleSpell;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 玩家技能数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/7/11
 */

@Mapper
public interface RoleSpellDao {


    /**
     * 插入玩家的技能信息
     *
     * @param rs            玩家技能信息
     * @return              affected rows
     */
    @Insert("insert into role_spell(role_id, spell_id) values (#{roleId}, #{spellId})")
    int insertRoleSpell(RoleSpell rs);


    /**
     * 查询所有的玩家技能信息
     *
     * @return      玩家技能列表
     */
    @Select("select * from role_spell")
    List<RoleSpell> selectAllRoleSpell();
}
