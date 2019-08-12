package com.ljh.gamedemo.dao;

import com.ljh.gamedemo.entity.PKRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * @Author: Heiku
 * @Date: 2019/8/12
 *
 * 具体的 pk数据库记录
 */

@Mapper
public interface RolePKDao {

    @Insert("insert into pk_record (challenger, defender, winner, win_honor, lose_honor, create_time, modify_time) values " +
            " (#{challenger}, #{defender}, #{winner}, #{win_honor}, #{lose_honor}, #{createTime}, #{modifyTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertPKRecord(PKRecord pkRecord);
}
