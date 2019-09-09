package com.ljh.gamedemo.module.task.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.common.TaskState;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.bean.Task;
import com.ljh.gamedemo.module.task.cache.TaskCache;
import com.ljh.gamedemo.module.task.dao.TaskDao;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgTaskProto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 任务的具体操作
 *
 * @Author: Heiku
 * @Date: 2019/9/6
 */

@Service
@Slf4j
public class TaskService {

    /**
     * TaskDao
     */
    @Autowired
    private TaskDao taskDao;


    @Autowired
    private ProtoService protoService;


    private MsgTaskProto.ResponseTask respTask;

    /**
     * 查询所有可以领取的任务信息
     *
     * 1.查询所有的任务
     * 2.查询已经完成的任务
     * 3.过滤返回可接的任务
     *
     * @param req       请求
     * @param channel   channel
     */
    public void taskAll(MsgTaskProto.RequestTask req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 查询所有的任务
        List<Task> allTask = Lists.newArrayList(TaskCache.getIdTaskMap().values());

        // 查询所有已经完成的任务
        List<RoleTask> finishTasks = Optional.ofNullable(TaskCache.getRoleDoneTaskMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());

        finishTasks.forEach(t -> allTask.removeIf(task -> task.getTaskId().longValue() == t.getTaskId()));

        // 消息返回
        sendMsg(MsgTaskProto.RequestType.TASK_ALL, allTask, ContentType.TASK_ALL, channel);
    }



    /**
     * 查询当前的任务状态
     *
     * @param req       请求
     * @param channel   channel
     */
    public void taskState(MsgTaskProto.RequestTask req, Channel channel) {
        // 玩家信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取已经领取的任务列表
        List<Task> tasks = Optional.ofNullable(TaskCache.getRoleProcessTaskMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());

        // 消息返回
        sendMsg(MsgTaskProto.RequestType.TASK_STATE, tasks, ContentType.TASK_STATE, channel);
    }


    /**
     * 接受任务操作
     *
     * @param req       请求
     * @param channel   channel
     */
    public void taskReceive(MsgTaskProto.RequestTask req, Channel channel) {
        // 任务id 判断
        respTask = taskInterceptor(req.getTaskId());
        if (!Objects.isNull(respTask)){
            channel.writeAndFlush(respTask);
            return;
        }

        // 获取玩家信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 判断玩家是否已经接受了该任务
        Optional<Task> result = Optional.ofNullable(TaskCache.getRoleProcessTaskMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList())
                .stream()
                .filter(t -> t.getTaskId() == req.getTaskId())
                .findFirst();
        if (result.isPresent()){
            sendFailedMsg(channel, ContentType.TASK_RECEIVE_FAILED);
            return;
        }


        // 构建任务关联
        doReceiveTask(role, req.getTaskId());

        // 消息返回
        sendMsg(MsgTaskProto.RequestType.TASK_COMMON, null, ContentType.TASK_RECEIVE_SUCCESS, channel);
    }


    /**
     * 取消任务
     *
     * @param req           请求
     * @param channel       channel
     */
    public void taskGiveUp(MsgTaskProto.RequestTask req, Channel channel) {
        // 玩家信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 任务id 判断
        respTask = taskInterceptor(req.getTaskId());
        if (!Objects.isNull(respTask)){
            channel.writeAndFlush(respTask);
            return;
        }

        // 判断玩家是否有接这个任务
        Optional<Task> result = Optional.ofNullable(TaskCache.getRoleProcessTaskMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList())
                .stream()
                .filter(t -> t.getTaskId() == req.getTaskId())
                .findFirst();
        if (!result.isPresent()){
            sendFailedMsg(channel, ContentType.TASK_GIVE_UP_FAILED);
            return;
        }

        // 取消任务关联
        doGiveUpTask(role, req.getTaskId());

        // 消息返回
        sendMsg(MsgTaskProto.RequestType.TASK_COMMON, null, ContentType.TASK_GIVE_UP_SUCCESS, channel);
    }




    /**
     * 实际接受任务操作
     *
     * @param role      玩家信息
     * @param taskId    任务 Id
     */
    private void doReceiveTask(Role role, long taskId) {
        // 构建玩家任务关联
        RoleTask roleTask = RoleTask.builder()
                .roleId(role.getRoleId())
                .taskId(taskId)
                .progress(TaskState.RECEIVE_TASK)
                .createTime(new Date())
                .modifyTime(new Date())
                .build();

        // 持久DB
        int n = taskDao.insertTask(roleTask);
        log.info("insert into role_task, affected rows: " + n);

        // 构建任务信息
        Task task = new Task();
        BeanUtils.copyProperties(TaskCache.getIdTaskMap().get(taskId), task);
        task.setId(roleTask.getId());

        // 缓存本地
        List<Task> tasks = Optional.ofNullable(TaskCache.getRoleProcessTaskMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());
        tasks.add(task);
        TaskCache.getRoleProcessTaskMap().put(role.getRoleId(), tasks);
    }


    /**
     * 实际放弃任务操作
     *
     * @param role      玩家信息
     * @param taskId    任务 id
     */
    private void doGiveUpTask(Role role, long taskId) {
        // 缓存找到对应的任务信息
        Optional<Task> result = TaskCache.getRoleProcessTaskMap().get(role.getRoleId())
                .stream()
                .filter(t -> t.getTaskId() == taskId)
                .findFirst();
        if (result.isPresent()){
            Task task = result.get();

            // 更新DB
            RoleTask roleTask = taskDao.selectTaskById(task.getId());
            roleTask.setProgress(TaskState.TASK_FINISH);
            roleTask.setModifyTime(new Date());

            int n = taskDao.updateTask(roleTask);
            log.info("update role_task, affected rows: " + n);

            // 移除本地缓存中的信息
            TaskCache.getRoleProcessTaskMap().get(role.getRoleId()).removeIf(t -> t.getTaskId() == taskId);
        }
    }


    /**
     * 任务id请求拦截器
     *
     * @param taskId        任务id
     * @return              任务协议
     */
    private MsgTaskProto.ResponseTask taskInterceptor(long taskId){
        if (taskId <= 0 || Objects.isNull(TaskCache.getIdTaskMap().get(taskId))){
            return MsgTaskProto.ResponseTask.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.TASK_WRONG_PARAM)
                    .build();
        }
        return null;
    }


    /**
     * 消息发送
     *
     * @param reqType       请求类型
     * @param allTask       任务列表
     * @param msg           消息
     */
    private void sendMsg(MsgTaskProto.RequestType reqType, List<Task> allTask, String msg, Channel channel) {
        respTask = MsgTaskProto.ResponseTask.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(msg)
                .setType(reqType)
                .addAllTask(protoService.transToTaskList(allTask))
                .build();
        channel.writeAndFlush(respTask);
    }



    /**
     * 失败消息发送
     *
     * @param channel       channel
     * @param msg           消息
     */
    private void sendFailedMsg(Channel channel, String msg) {
        respTask = MsgTaskProto.ResponseTask.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        channel.writeAndFlush(respTask);
    }
}
