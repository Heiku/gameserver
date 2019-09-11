package com.ljh.gamedemo.module.site.dao;

import com.ljh.gamedemo.module.site.bean.UserSite;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 玩家移动数据库操作
 */

@Mapper
public interface UserSiteDao {


    /**
     * 插入玩家的位置信息
     *
     * @param userId        用户id
     * @param siteId        位置id
     * @return              affected rows
     */
    @Insert("insert into user_site(user_id, site_id) values(#{userId}, #{siteId})")
    int insertUserSite(long userId, int siteId);


    /**
     * 查询玩家的位置信息
     *
     * @param userId        用户id
     * @return              玩家位置信息
     */
    @Select("select * from user_site where user_id = #{userId}")
    UserSite selectUserSiteByUID(long userId);


    /**
     * 查询指定位置的玩家信息
     *
     * @param siteId        场景id
     * @return              玩家位置信息列表
     */
    @Select("select * from user_site where site_id = #{siteId}")
    List<UserSite> selectAllUserBySID(int siteId);
}
