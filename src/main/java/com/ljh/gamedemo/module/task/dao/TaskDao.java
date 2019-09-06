package com.ljh.gamedemo.module.task.dao;

import com.ljh.gamedemo.module.task.bean.RoleTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 任务的数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/6
 */

@Mapper
public interface TaskDao {

    @Insert("insert into task (role_id, task_id, progress, create_time, modify_time) values (#{roleId}, #{taskId}, " +
            " #{progress}, #{createTime}, #{modifyTime})")
    int insertTask(RoleTask task);

    @Update("update task set progress = #{progress} and modify_time = #{modifyTime} where id = #{id}")
    int updateTask(RoleTask task);

    @Select("select * from task where progress <= 3")
    List<RoleTask> selectUnCompleteTask();
}
