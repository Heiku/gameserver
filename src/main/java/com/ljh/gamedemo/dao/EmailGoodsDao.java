package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.EmailGoods;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 */

@Mapper
public interface EmailGoodsDao {

    @Insert("insert into email_goods (eid, gid, num) values (#{eid}, #{gid}, #{num})")
    int insertEmailGoods(EmailGoods emailGoods);

    @Select("select * from email_goods where eid = #{eid}")
    List<EmailGoods> selectAllEmailGoods(long eid);
}
