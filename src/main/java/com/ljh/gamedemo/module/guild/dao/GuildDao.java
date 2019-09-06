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
     * 公会信息
     *
     */
    @Select("select * from guild")
    List<Guild> queryGuildList();

    @Select("select * from Guild where roleId = #{roleId}")
    Guild queryGuildByRoleId(long roleId);

    @Insert("insert into guild (name, bulletin, level, num, max_num, president) values " +
            " (#{name}, #{bulletin}, #{level}, #{num}, #{maxNum}, #{president})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertGuild(Guild guild);

    @Update("update guild set name = #{name}, bulletin = #{bulletin}, level = #{level}, num = #{num}, max_num = #{maxNum},  " +
            " president = #{president} where id = #{id}")
    int updateGuild(Guild guild);

    @Delete("delete from guild where id = #{id}")
    int deleteGuild(Guild guild);


    /**
     * 公会成员信息
     *
     * @param member
     * @return
     */

    @Insert("insert into role_guild(role_id, gid, position, today_con, all_con) values (#{roleId}, #{gid}, #{position}, #{todayCon}, #{allCon})")
    int insertGuildMember(Member member);

    @Select("select * from role_guild where gid = #{gid}")
    List<Member> queryAllMemberByGid(long gid);

    @Select("select * from role_guild where role_id = #{roleId}")
    Member queryMemberByRoleId(long roleId);

    @Update("update role_guild set position = #{position}, today_con = #{todayCon}, all_con = #{allCon} where role_id = #{roleId}")
    int updateMemberByRoleId(Member member);

    @Delete("delete from role_guild where role_id = #{roleId}")
    int deleteMemberInfo(long roleId);


    @Update("update role_guild set today_con = 0")
    int updateTodayCon();
}
