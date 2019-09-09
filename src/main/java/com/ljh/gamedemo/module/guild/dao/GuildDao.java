package com.ljh.gamedemo.module.guild.dao;

import com.ljh.gamedemo.module.guild.bean.Guild;
import com.ljh.gamedemo.module.guild.bean.Member;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 公会Guild 数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/8/26
 */

@Mapper
public interface GuildDao {


    /**
     * 查询公会列表
     *
     * @return      公会列表
     */
    @Select("select * from guild")
    List<Guild> queryGuildList();


    /**
     * 查询玩家的当前公会
     *
     * @param roleId        玩家id
     * @return
     */
    @Select("select * from Guild where roleId = #{roleId}")
    Guild queryGuildByRoleId(long roleId);


    /**
     * 插入公会信息
     *
     * @param guild     公会信息
     * @return          affected rows
     */
    @Insert("insert into guild (name, bulletin, level, num, max_num, president) values " +
            " (#{name}, #{bulletin}, #{level}, #{num}, #{maxNum}, #{president})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertGuild(Guild guild);


    /**
     * 更新公会信息
     *
     * @param guild     公会信息
     * @return          affected rows
     */
    @Update("update guild set name = #{name}, bulletin = #{bulletin}, level = #{level}, num = #{num}, max_num = #{maxNum},  " +
            " president = #{president} where id = #{id}")
    int updateGuild(Guild guild);


    /**
     * 删除公会信息
     *
     * @param guild     公会信息
     * @return          affected rows
     */
    @Delete("delete from guild where id = #{id}")
    int deleteGuild(Guild guild);


    /**
     * 插入公会的成员信息
     *
     * @param member        公会成员信息
     * @return              affected rows
     */
    @Insert("insert into role_guild(role_id, gid, position, today_con, all_con) values (#{roleId}, #{gid}, #{position}, #{todayCon}, #{allCon})")
    int insertGuildMember(Member member);


    /**
     * 查询公会中的所有成员信息
     *
     * @param gid       公会id
     * @return          公会成员列表
     */
    @Select("select * from role_guild where gid = #{gid}")
    List<Member> queryAllMemberByGid(long gid);


    /**
     * 查询玩家的公会成员信息
     *
     * @param roleId        玩家id
     * @return              公会成员信息
     */
    @Select("select * from role_guild where role_id = #{roleId}")
    Member queryMemberByRoleId(long roleId);


    /**
     * 更新公会成员信息
     *
     * @param member        公会成员信息
     * @return              affected rows
     */
    @Update("update role_guild set position = #{position}, today_con = #{todayCon}, all_con = #{allCon} where role_id = #{roleId}")
    int updateMemberByRoleId(Member member);


    /**
     * 删除公会的成员信息
     *
     * @param roleId        玩家id
     * @return              affected rows
     */
    @Delete("delete from role_guild where role_id = #{roleId}")
    int deleteMemberInfo(long roleId);


    /**
     * 更新玩家的公会贡献值
     *
     * @return      affected rows
     */
    @Update("update role_guild set today_con = 0")
    int updateTodayCon();
}
