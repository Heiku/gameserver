package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.dto.RoleSpell;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/11
 */

@Mapper
public interface RoleSpellDao {

    @Insert("insert into role_spell(id, role_id, spell_id) values (#{id}, #{roleId}, #{spellId})")
    int insertRoleSpell(int id, long roleId, int spellId);

    @Select("select * from role_spell")
    List<RoleSpell> selectAllRoleSpell();


}
