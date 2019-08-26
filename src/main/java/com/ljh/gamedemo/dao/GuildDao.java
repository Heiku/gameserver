package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.Guild;
import com.ljh.gamedemo.entity.Member;
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

    @Select("select * from guild")
    List<Guild> queryGuildList();

    @Select("select * from Guild where roleId = #{roleId}")
    Guild queryGuildByRoleId(long roleId);

    @Insert("insert into guild (name, bulletin, level, num, max_num, president) values " +
            " (#{name}, #{bulletin}, #{level}, #{num}, #{maxNum}, #{president})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertGuild(Guild guild);

    @Update("update guild set name = #{name}, bulletin = #{bulletin}, level = #{level}, num = #{num}, max_num = #{maxNum}. " +
            " president = #{president} where id = #{id}")
    int updateGuild(Guild guild);



    @Insert("insert into role_guild(role_id, gid, position, today, all) values (#{roleId. #{gid}, #{position}, #{today}. #{all})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertGuildMember(Member member);

    @Select("select * from role_guild where gid = #{gid}")
    int queryAllMemberByGid(long gid);

    @Select("select * from role_guild where role_id = #{roleId}")
    Member queryMemberByRoleId(long roleId);

    @Update("update role_guild set position = #{position}, today = #{today}, all = #{all} where role_id = #{roleId}}")
    int updateMemberByRoleId(long roleId);

    @Delete("delete from role_guild where role_id = #{roleId}")
    int deleteMemberInfo(long roleId);
}
