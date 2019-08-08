package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.Email;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/8/7
 */

@Mapper
public interface RoleEmailDao {

    @Insert("insert into role_email(from_id, to_role_id, theme, content, state, create_time, modify_time) " +
            " values (#{fromId}, #{toRoleId}, #{theme}, #{content}, #{state}, #{createTime}, #{modifyTime})")
    @Options(useGeneratedKeys = true, keyProperty ="id", keyColumn = "id")
    int insertEmail(Email email);

    @Update("update role_email set state = #{state}, modify_time = #{modifyTime} where to_role_id = #{toRoleId} and id = #{id}")
    int updateEmail(Email email);

    @Select("select * from role_email where to_role_id = #{roleId} and state = 0")
    List<Email> selectUnReceiveEmail(long roleId);
}
