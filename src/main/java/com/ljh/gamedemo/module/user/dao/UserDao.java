package com.ljh.gamedemo.module.user.dao;

import com.ljh.gamedemo.module.user.bean.User;
import com.ljh.gamedemo.module.user.bean.UserAccount;
import com.ljh.gamedemo.module.user.bean.UserToken;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    @Select("select * from user_account where user_name = #{userName}")
    User selectUser(String userName);

    @Select("select * from user_account where user_id = #{userId}")
    User selectUserById(long userId);

    @Insert("insert into user_account(user_name, password) values(#{userName}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    int insertUserAccount(UserAccount userAccount);

    @Insert("insert into user_token (user_id, token) values(#{userId}, #{token})")
    int insertUserToken(long userId, String token);

    @Select("select * from user_token where token = #{token}")
    UserToken selectUserToken(String token);

    @Select("select * from user_token where user_id = #{userId}")
    UserToken selectUserTokenByID(long userId);
}
