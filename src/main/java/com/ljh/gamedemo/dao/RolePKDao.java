package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.PKRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

/**
 * @Author: Heiku
 * @Date: 2019/8/12
 *
 * 具体的 pk数据库记录
 */

@Mapper
public interface RolePKDao {

    @Insert("insert into pk_record (challenger, defender, winner, loser, win_honor, lose_honor, create_time, end_time) values " +
            " (#{challenger}, #{defender}, #{winner}, #{loser}, #{winHonor}, #{loseHonor}, #{createTime}, #{endTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertPKRecord(PKRecord pkRecord);

    @Update("update pk_record set winner = #{winner}, loser = #{loser}, end_time = #{endTime} where id = #{id}")
    int updateRecord(PKRecord pkRecord);
}
