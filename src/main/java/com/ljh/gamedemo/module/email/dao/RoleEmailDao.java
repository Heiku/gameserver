package com.ljh.gamedemo.module.email.dao;

import com.ljh.gamedemo.module.email.bean.Email;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 邮件数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/8/7
 */

@Mapper
public interface RoleEmailDao {


    /**
     * 插入发送的邮件信息
     *
     * @param email     邮件信息
     * @return          affected rows
     */
    @Insert("insert into role_email(from_id, to_role_id, theme, content, state, create_time, modify_time) " +
            " values (#{fromId}, #{toRoleId}, #{theme}, #{content}, #{state}, #{createTime}, #{modifyTime})")
    @Options(useGeneratedKeys = true, keyProperty ="id", keyColumn = "id")
    int insertEmail(Email email);


    /**
     * 更新邮件信息
     *
     * @param email     邮件信息
     * @return          affected rows
     */
    @Update("update role_email set state = #{state}, modify_time = #{modifyTime} where to_role_id = #{toRoleId} and id = #{id}")
    int updateEmail(Email email);


    /**
     * 查询玩家的未领取物品的邮件信息
     *
     * @param roleId        玩家id
     * @return              邮件信息列表
     */
    @Select("select * from role_email where to_role_id = #{roleId} and state = 0")
    List<Email> selectUnReceiveEmail(long roleId);


    /**
     * 查询玩家的所有邮件信息
     *
     * @param roleId        玩家id
     * @return              邮件信息列表
     */
    @Select("select * from role_email where to_role_id = #{roleId} order by state asc")
    List<Email> selectAllEmail(long roleId);


    /**
     * 查询指定id 的邮件信息
     *
     * @param eid       邮件id
     * @return          邮件信息
     */
    @Select("select * from role_email where id = #{eid}")
    Email selectEmailById(long eid);
}
