package com.ljh.gamedemo.module.task.dao;

import com.ljh.gamedemo.module.task.bean.RoleTask;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 任务的数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/6
 */

@Mapper
public interface TaskDao {

    /**
     * 插入任务信息
     *
     * @param task      玩家任务信息
     * @return          affected rows
     */
    @Insert("insert into task (role_id, task_id, progress, create_time, modify_time) values (#{roleId}, #{taskId}, " +
            " #{progress}, #{createTime}, #{modifyTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertTask(RoleTask task);


    /**
     * 更新玩家任务信息
     *
     * @param task      玩家任务信息
     * @return          affected rows
     */
    @Update("update task set progress = #{progress} and modify_time = #{modifyTime} where id = #{id}")
    int updateTask(RoleTask task);


    /**
     * 查询所有未完成的任务信息
     *
     * @return      玩家正在进行的任务列表
     */
    @Select("select * from task where progress = 2")
    List<RoleTask> selectUnCompleteTask();


    /**
     * 查询所有已经完成的任务信息
     *
     * @return      玩家已经完成的任务列表
     */
    @Select("select * from task where progress = 3")
    List<RoleTask> selectFinishTask();


    /**
     * 查询指定 id 的任务
     *
     * @param id    任务id
     * @return      玩家任务信息
     */
    @Select("select * from task where id = #{id}")
    RoleTask selectTaskById(long id);
}
