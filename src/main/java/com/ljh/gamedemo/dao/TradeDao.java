package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.Trade;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/8/29
 */

@Mapper
public interface TradeDao {

    @Insert("insert into trade (goods_id, num, seller, buyer, price, type, process, start_time, end_time) values " +
            " (#{goodsId}, #{num}, #{seller}, #{buyer}, #{price}, #{type}, #{process}, #{startTime}, #{endTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertTrade(Trade trade);

    @Select("select * from trade")
    List<Trade> selectAllTrade();

    @Update("update trade set buyer = #{buyer}, price = #{price}, process = #{process} where id = #{id}")
    int updateTrade(Trade trade);
}
