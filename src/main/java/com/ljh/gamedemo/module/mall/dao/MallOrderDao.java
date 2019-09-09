package com.ljh.gamedemo.module.mall.dao;

import com.ljh.gamedemo.module.mall.bean.MallOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商城的购买记录数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/8/8
 */
@Mapper
public interface MallOrderDao {


    /**
     * 插入玩家的购买记录信息
     *
     * @param mallOrder     玩家商城购买记录
     * @return              affected rows
     */
    @Insert("insert into mall_order (role_id, cid, num, cost, create_time) values (#{roleId}, #{cid}, #{num}, #{cost}, #{createTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertMallOrder(MallOrder mallOrder);


    /**
     * 查询玩家的交易购买记录
     *
     * @param roleId        玩家id
     * @return              玩家的购买记录列表
     */
    @Select("select * from mall_order where role_id = #{roleId}")
    List<MallOrder> selectAllOrder(long roleId);
}
