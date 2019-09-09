package com.ljh.gamedemo.module.guild.dao;

import com.ljh.gamedemo.module.guild.bean.GuildGoodsRecord;
import com.ljh.gamedemo.module.guild.bean.GuildGoodsStore;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 公会物品数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/8/27
 */

@Mapper
public interface GuildGoodsDao {


    /**
     * 插入公会物品记录信息
     *
     * @param record        公会物品记录信息
     * @return              affected rows
     */
    @Insert("insert into guild_goods_record (role_id, guild_id, goods_id, num, type, create_time) values " +
            " (#{roleId}, #{guildId}, #{goodsId}, #{num}, #{type}, #{createTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertGuildGoodsRecord(GuildGoodsRecord record);


    /**
     * 插入公会物品信息
     *
     * @param guildGoodsStore   公会物品信息
     * @return                  affected rows
     */
    @Insert("insert into guild_goods_store (guild_id, goods_id, num) values (#{guildId}, #{goodsId}, #{num})")
    int insertGuildStore(GuildGoodsStore guildGoodsStore);


    /**
     * 更新公会物品信息
     *
     * @param guildGoodsStore   公会物品信息
     * @return                  affected rows
     */
    @Update("update guild_goods_store set num = #{num} where id = #{id}")
    int updateGuildStore(GuildGoodsStore guildGoodsStore);


    /**
     * 删除公会物品信息
     *
     * @param guildGoodsStore   公会物品信息
     * @return                  affected rows
     */
    @Delete("delete from guild_goods_store where id = #{id}")
    int deleteGuildStore(GuildGoodsStore guildGoodsStore);


    /**
     * 查询指定公会的所有物品信息
     *
     * @param gid       公会id
     * @return          公会物品信息列表
     */
    @Select("select * from guild_goods_store where id = #{gid}")
    List<GuildGoodsStore> queryAllGuildStore(long gid);
}
