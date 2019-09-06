package com.ljh.gamedemo.module.email.dao;

import com.ljh.gamedemo.module.email.bean.EmailGoods;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 */

@Mapper
public interface EmailGoodsDao {

    @Insert("insert into email_goods (eid, gid, num) values (#{eid}, #{gid}, #{num})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertEmailGoods(EmailGoods emailGoods);

    @Select("select * from email_goods where eid = #{eid}")
    List<EmailGoods> selectAllEmailGoods(long eid);
}
