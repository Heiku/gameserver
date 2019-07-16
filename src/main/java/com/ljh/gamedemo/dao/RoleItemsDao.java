package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.dto.RoleItems;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/16
 */

@Mapper
public interface RoleItemsDao {

    @Insert("insert into role_objects(id, role_id, objects_id, num) values (#{id}, #{roleId}, #{objectsId}, #{num}) ")
    int insertIntoRoleItems(long id, long roleId, long objectsId, int num);

    @Insert("insert into role_objects(role_id, objects_id, num) values (#{roleId}, #{objectsId}, #{num})")
    int insertRoleItems(long roleId, long objectsId, int num);

    @Select("select * from role_objects")
    List<RoleItems> selectAllRoleItems();
}
