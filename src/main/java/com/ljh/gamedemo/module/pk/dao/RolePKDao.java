package com.ljh.gamedemo.module.pk.dao;

import com.ljh.gamedemo.module.pk.bean.PKRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

/**
 * pk数据库记录
 *
 * @Author: Heiku
 * @Date: 2019/8/12
 */

@Mapper
public interface RolePKDao {


    /**
     * 插入玩家的 pk 记录
     *
     * @param pkRecord      pk 记录
     * @return              affected rows
     */
    @Insert("insert into pk_record (challenger, defender, winner, loser, win_honor, lose_honor, create_time, end_time) values " +
            " (#{challenger}, #{defender}, #{winner}, #{loser}, #{winHonor}, #{loseHonor}, #{createTime}, #{endTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertPKRecord(PKRecord pkRecord);



    /**
     * 更新玩家的 pk 记录
     *
     * @param pkRecord      pk 记录
     * @return              affected rows
     */
    @Update("update pk_record set winner = #{winner}, loser = #{loser}, end_time = #{endTime} where id = #{id}")
    int updateRecord(PKRecord pkRecord);
}
