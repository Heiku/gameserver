package com.ljh.gamedemo.module.email.dao;

import com.ljh.gamedemo.module.email.bean.EmailGoods;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 邮件物品数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/8/8
 */

@Mapper
public interface EmailGoodsDao {


    /**
     * 插入邮件物品信息
     *
     * @param emailGoods      邮件物品信息
     * @return                affected rows
     */
    @Insert("insert into email_goods (eid, gid, num) values (#{eid}, #{gid}, #{num})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertEmailGoods(EmailGoods emailGoods);


    /**
     * 查询所有的邮件物品信息
     *
     * @param eid      邮件id
     * @return          邮件物品信息列表
     */
    @Select("select * from email_goods where eid = #{eid}")
    List<EmailGoods> selectAllEmailGoods(long eid);
}
