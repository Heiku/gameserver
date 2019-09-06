package com.ljh.gamedemo.module.items.dao;

import com.ljh.gamedemo.module.items.bean.RoleItems;
import org.apache.ibatis.annotations.*;

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

    @Update("update role_objects set num = #{num} where role_id = #{roleId} and objects_id = #{itemId}")
    int updateItem(int num, long roleId, long itemId);

    @Delete("delete role_objects where role_id = #{roleId} and objects_id = #{itemId}")
    int deleteItem(long roleId, long itemId);
}
