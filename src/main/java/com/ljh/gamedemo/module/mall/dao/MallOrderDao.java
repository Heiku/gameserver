package com.ljh.gamedemo.module.mall.dao;

import com.ljh.gamedemo.module.mall.bean.MallOrder;
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
public interface MallOrderDao {

    @Insert("insert into mall_order (role_id, cid, num, cost, create_time) values (#{roleId}, #{cid}, #{num}, #{cost}, #{createTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertMallOrder(MallOrder mallOrder);

    @Select("select * from mall_order where role_id = #{roleId}")
    List<MallOrder> selectAllOrder(long roleId);
}
