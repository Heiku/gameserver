package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.UserSite;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserSiteDao {

    @Insert("insert into user_site(user_id, site_id) values(#{userId}, #{siteId})")
    int insertUserSite(long userId, int siteId);

    @Select("select * from user_site where user_id = #{userId}")
    UserSite selectUserSiteByUID(long userId);

    @Select("select * from user_site where site_id = #{siteId}")
    List<UserSite> selectAllUserBySID(int siteId);
}
