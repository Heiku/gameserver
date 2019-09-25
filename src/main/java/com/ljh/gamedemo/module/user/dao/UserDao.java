package com.ljh.gamedemo.module.user.dao;

import com.ljh.gamedemo.module.user.bean.User;
import com.ljh.gamedemo.module.user.bean.UserAccount;
import com.ljh.gamedemo.module.user.bean.UserToken;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据库操作
 */
@Mapper
public interface UserDao {

    /**
     * 查询用户信息
     *
     * @param userName      用户名称
     * @return
     */
    @Select("select * from user_account where user_name = #{userName}")
    User selectUser(String userName);


    /**
     * 新增用户记录
     *
     * @param userAccount       用户账号记录
     * @return                  affected rows
     */
    @Insert("insert into user_account(user_name, password) values(#{userName}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    int insertUserAccount(UserAccount userAccount);


    /**
     * 查询玩家id
     *
     * @param userId        用户id
     * @return
     */
    @Select("select * from user_account where user_id = #{userId}")
    User selectUserById(long userId);


    /**
     * 插入玩家token记录
     *
     * @param userId        用户id
     * @param token         token信息
     * @return              affected rows
     */
    @Insert("insert into user_token (user_id, token) values(#{userId}, #{token})")
    int insertUserToken(long userId, String token);


    /**
     * 查询玩家token记录
     *
     * @param token     token
     * @return          玩家token信息
     */
    @Select("select * from user_token where token = #{token}")
    UserToken selectUserToken(String token);


    /**
     * 查询玩家的token记录
     *
     * @param userId        玩家id
     * @return              玩家token信息
     */
    @Select("select * from user_token where user_id = #{userId}")
    UserToken selectUserTokenByID(long userId);
}
