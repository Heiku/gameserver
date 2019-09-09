package com.ljh.gamedemo.module.face.dao;

import com.ljh.gamedemo.module.face.bean.Transaction;
import org.apache.ibatis.annotations.*;

/**
 * 面对面交易数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/8/22
 */

@Mapper
public interface FaceTransDao {


    /**
     * 插入面对面的交易记录
     *
     * @param transaction       交易记录
     * @return                  affected rows
     */
    @Insert("insert into face_trans(promoter, receiver, goods_id, num, amount, success, create_time, modify_time) values " +
            " (#{promoter}, #{receiver}, #{goodsId}, #{num}, #{amount}, #{success}, #{createTime}, #{modifyTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertFaceTrans(Transaction transaction);


    /**
     * 更新米娜对面的交易记录
     *
     * @param transaction       交易记录
     * @return                  affected rows
     */
    @Update("update face_trans set success = #{success} and modify_time = #{modifyTime}")
    int updateFaceTrans(Transaction transaction);


    /**
     * 删除交易记录
     *
     * @param id        交易d
     * @return          affected rows
     */
    @Delete("delete from face_trans where id = #{id}")
    int deleteFaceTrans(int id);
}
