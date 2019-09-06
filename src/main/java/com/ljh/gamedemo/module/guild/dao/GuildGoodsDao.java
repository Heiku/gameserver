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

    @Insert("insert into guild_goods_record (role_id, guild_id, goods_id, num, type, create_time) values " +
            " (#{roleId}, #{guildId}, #{goodsId}, #{num}, #{type}, #{createTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertGuildGoodsRecord(GuildGoodsRecord record);



    @Insert("insert into guild_goods_store (guild_id, goods_id, num) values (#{guildId}, #{goodsId}, #{num})")
    int insertGuildStore(GuildGoodsStore guildGoodsStore);

    @Update("update guild_goods_store set num = #{num} where id = #{id}")
    int updateGuildStore(GuildGoodsStore guildGoodsStore);

    @Delete("delete from guild_goods_store where id = #{id}")
    int deleteGuildStore(GuildGoodsStore guildGoodsStore);

    @Select("select * from guild_goods_store where id = #{gid}")
    List<GuildGoodsStore> queryAllGuildStore(long gid);
}
