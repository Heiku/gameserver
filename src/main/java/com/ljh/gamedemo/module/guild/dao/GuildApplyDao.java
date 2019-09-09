package com.ljh.gamedemo.module.guild.dao;

import com.ljh.gamedemo.module.guild.bean.GuildApply;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 公会申请记录操作
 *
 * @Author: Heiku
 * @Date: 2019/8/27
 */

@Mapper
public interface GuildApplyDao {


    /**
     * 插入公会的申请记录
     *
     * @param guildApply        公会申请记录
     * @return                  affected rows
     */
    @Insert("insert into guild_apply(guild_id, role_id, approver, process, create_time, modify_time) values " +
            " (#{guildId}, #{roleId}, #{approver}, #{process}, #{createTime}, #{modifyTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertGuildApply(GuildApply guildApply);


    /**
     * 查询未审批的公会申请
     *
     * @param guildId   公会申请id
     * @return          affected rows
     */
    @Select("select * from guild_apply where guild_id = #{guildId} and process = 0")
    List<GuildApply> queryAllUnCheckGuildApply(long guildId);


    /**
     * 更新公会的申请信息
     *
     * @param guildApply        公会申请信息
     * @return                  affected rows
     */
    @Update("update guild_apply set approver = #{approver}, process = #{process}, modify_time = #{modifyTime} where id = #{id}")
    int updateGuildApply(GuildApply guildApply);
}
