package com.ljh.gamedemo.module.face.dao;

import com.ljh.gamedemo.module.face.bean.Transaction;
import org.apache.ibatis.annotations.*;

/**
 * db face_trans
 *
 * @Author: Heiku
 * @Date: 2019/8/22
 */

@Mapper
public interface FaceTransDao {

    @Insert("insert into face_trans(promoter, receiver, goods_id, num, amount, success, create_time, modify_time) values " +
            " (#{promoter}, #{receiver}, #{goodsId}, #{num}, #{amount}, #{success}, #{createTime}, #{modifyTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertFaceTrans(Transaction transaction);


    @Update("update face_trans set success = #{success} and modify_time = #{modifyTime}")
    int updateFaceTrans(Transaction transaction);

    @Delete("delete from face_trans where id = #{id}")
    int deleteFaceTrans(int id);
}
