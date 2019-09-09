package com.ljh.gamedemo.module.items.dao;

import com.ljh.gamedemo.module.items.bean.RoleItems;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 玩家物品数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/7/16
 */

@Mapper
public interface RoleItemsDao {

    /**
     * 插入玩家的物品信息
     *
     * @param roleId        玩家id
     * @param objectsId     物品id
     * @param num           物品数量
     * @return              affected rows
     */
    @Insert("insert into role_objects(role_id, objects_id, num) values (#{roleId}, #{objectsId}, #{num})")
    int insertRoleItems(long roleId, long objectsId, int num);


    /**
     * 查询玩家的所有物品信息
     *
     * @return  玩家的物品列表
     */
    @Select("select * from role_objects")
    List<RoleItems> selectAllRoleItems();


    /**
     * 更新玩家的物品信息
     *
     * @param num          物品数量
     * @param roleId       玩家id
     * @param itemId       物品id
     * @return             affected rows
     */
    @Update("update role_objects set num = #{num} where role_id = #{roleId} and objects_id = #{itemId}")
    int updateItem(int num, long roleId, long itemId);


    /**
     *  删除玩家的物品信息
     *
     * @param roleId        玩家信息
     * @param itemId        物品信息
     * @return              affected rows
     */
    @Delete("delete role_objects where role_id = #{roleId} and objects_id = #{itemId}")
    int deleteItem(long roleId, long itemId);
}
