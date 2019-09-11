package com.ljh.gamedemo.module.trade.dao;

import com.ljh.gamedemo.module.trade.bean.Trade;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 交易数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/8/29
 */

@Mapper
public interface TradeDao {

    /**
     * 插入交易信息
     *
     * @param trade     交易信息
     * @return          affected rows
     */
    @Insert("insert into trade (goods_id, num, seller, buyer, price, type, process, start_time, end_time) values " +
            " (#{goodsId}, #{num}, #{seller}, #{buyer}, #{price}, #{type}, #{process}, #{startTime}, #{endTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertTrade(Trade trade);


    /**
     * 获取所有的交易信息
     *
     * @return      交易信息列表
     */
    @Select("select * from trade")
    List<Trade> selectAllTrade();


    /**
     * 更新交易记录
     *
     * @param trade     交易信息
     * @return          affected rows
     */
    @Update("update trade set buyer = #{buyer}, price = #{price}, process = #{process} where id = #{id}")
    int updateTrade(Trade trade);


    /**
     * 查询正在交易中的交易信息
     *
     * @return      交易信息列表
     */
    @Select("select * from trade where process = 1")
    List<Trade> selectAllOnTrade();
}
