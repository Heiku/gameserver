package com.ljh.gamedemo.module.task.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.*;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.email.service.EmailService;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.module.task.asyn.TaskSaveManager;
import com.ljh.gamedemo.module.task.asyn.run.TaskSaveRun;
import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.bean.Task;
import com.ljh.gamedemo.module.task.cache.TaskCache;
import com.ljh.gamedemo.module.task.dao.TaskDao;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgTaskProto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    /**
     * 邮件服务
     */
    @Autowired
    private EmailService emailService;

    /**
     * 玩家服务
     */
    @Autowired
    private RoleService roleService;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;


    /**
     * 任务协议返回
     */
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
        List<RoleTask> tasks = Lists.newArrayList();
        allTask.forEach(t ->{
            RoleTask task = new RoleTask();
            task.setTaskId(t.getTaskId());
            task.setProgress(TaskState.UN_RECEIVE_TASK);
            tasks.add(task);
                }
            );

        // 查询所有已经完成的任务
        List<RoleTask> finishTasks = Optional.ofNullable(TaskCache.getRoleDoneTaskMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());
        log.info(finishTasks.toString());
        finishTasks.forEach(t ->
                tasks.removeIf(task -> task.getTaskId().longValue() == t.getTaskId())
        );

        // 消息返回
        sendMsg(MsgTaskProto.RequestType.TASK_ALL, tasks, ContentType.TASK_ALL, channel);
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
        List<RoleTask> tasks = Optional.ofNullable(TaskCache.getRoleProcessTaskMap().get(role.getRoleId()))
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
        Optional<RoleTask> result = Optional.ofNullable(TaskCache.getRoleProcessTaskMap().get(role.getRoleId()))
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
     * 任务提交
     *
     * @param req       请求
     * @param channel   channel
     */
    public void taskSubmit(MsgTaskProto.RequestTask req, Channel channel) {
        // 获取玩家信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());
        long taskId = req.getTaskId();

        // 任务id 判断
        respTask = taskInterceptor(req.getTaskId());
        if (!Objects.isNull(respTask)){
            channel.writeAndFlush(respTask);
            return;
        }

        // 判断玩家是否已经接受了该任务
        Optional<RoleTask> result = Optional.ofNullable(TaskCache.getRoleProcessTaskMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList())
                .stream()
                .filter(t -> t.getTaskId() == taskId)
                .findFirst();
        if (!result.isPresent()){
            sendFailedMsg(channel, ContentType.TASK_RECEIVE_FAILED);
            return;
        }

        RoleTask task = result.get();
        if (task.getProgress() != TaskState.TASK_FINISH){
            sendFailedMsg(channel, ContentType.TASK_SUBMIT_FAILED);
        }

        // 具体的任务提交操作
        doTaskSubmit(role, task);

        // 消息返回
        sendMsg(MsgTaskProto.RequestType.TASK_COMMON, null, ContentType.TASK_SUBMIT_SUCCESS, channel);
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
        Optional<RoleTask> result = Optional.ofNullable(TaskCache.getRoleProcessTaskMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList())
                .stream()
                .filter(t -> t.getTaskId() == req.getTaskId())
                .findFirst();
        if (!result.isPresent()){
            sendFailedMsg(channel, ContentType.TASK_GIVE_UP_FAILED);
            return;
        }

        // 取消任务关联
        doGiveUpTask(role, result.get());

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
        RoleTask roleTask = new RoleTask();
        roleTask.setRoleId(role.getRoleId());
        roleTask.setTaskId(taskId);
        roleTask.setProgress(TaskState.RECEIVE_TASK);
        roleTask.setCreateTime(new Date());
        roleTask.setModifyTime(new Date());

        // 持久DB
        TaskSaveManager.getExecutorService().submit(new TaskSaveRun(roleTask, CommonDBType.INSERT));

        // 缓存本地
        List<RoleTask> tasks = Optional.ofNullable(TaskCache.getRoleProcessTaskMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());
        tasks.add(roleTask);
        TaskCache.getRoleProcessTaskMap().put(role.getRoleId(), tasks);
    }


    /**
     * 实际放弃任务操作
     *
     * @param role      玩家信息
     * @param task      任务信息
     */
    private void doGiveUpTask(Role role, RoleTask task) {
        // 更新任务状态
        updateStoreTask(role, task, TaskState.TASK_DISCARD);
    }


    /**
     * 任务提交
     *
     * @param role      玩家信息
     * @param task      任务信息
     */
    private void doTaskSubmit(Role role, RoleTask task) {
        // 更新任务状态
        updateStoreTask(role, task, TaskState.TASK_ALL_FINISH);

        // 获取具体的任务信息
        Task t = TaskCache.getIdTaskMap().get(task.getTaskId());

        // 更新玩家金币值
        role.setGold(t.getGold());
        roleService.updateRoleInfo(role);

        // 任务奖励发放
        emailService.sendEmail(role, t.getGoods(), EmailType.TASK_REWARD);
    }


    /**
     * 更新任务的状态信息
     *
     * @param role      玩家信息
     * @param task      任务信息
     * @param state     任务状态
     */
    private void updateStoreTask(Role role, RoleTask task, int state){
        // 更新db
        task.setProgress(state);
        task.setModifyTime(new Date());

        TaskSaveManager.getExecutorService().submit(new TaskSaveRun(task, CommonDBType.UPDATE));

        // 移除本地缓存中的信息
        if (state == TaskState.TASK_ALL_FINISH || state == TaskState.TASK_DISCARD){
            TaskCache.getRoleProcessTaskMap().get(role.getRoleId()).removeIf(t -> t.getTaskId().longValue() == task.getTaskId());
        }

        // 如果任务状态已经全部完成，那么就将任务记录缓存到本地
        if (state == TaskState.TASK_ALL_FINISH){
            List<RoleTask> doneTaskList = Optional.ofNullable(TaskCache.getRoleDoneTaskMap().get(role.getRoleId()))
                    .orElse(Lists.newArrayList());
            doneTaskList.add(task);
            TaskCache.getRoleDoneTaskMap().put(role.getRoleId(), doneTaskList);
        }
    }


    /**
     * 任务完成
     *
     * @param role      玩家信息
     * @param roleTask  任务信息
     */
    public void completeTask(Role role, RoleTask roleTask){

        // 更新玩家的任务信息
        updateStoreTask(role, roleTask, TaskState.TASK_FINISH);

        Task t = TaskCache.getIdTaskMap().get(roleTask.getTaskId());
        // 发送任务完成通知
        sendMsg(MsgTaskProto.RequestType.TASK_COMMON, null,
                String.format(ContentType.TASK_COMPLETE_ANN, t.getName()),
                ChannelCache.getUserIdChannelMap().get(role.getUserId()));
    }



    /**
     * 获取玩家的任务信息
     *
     * @param role      玩家信息
     * @param taskId    任务类型
     * @return          任务信息
     */
    public RoleTask getRoleTaskByType(Role role, long taskId){
        List<RoleTask> tasks = TaskCache.getRoleProcessTaskMap().get(role.getRoleId());
        if (CollectionUtils.isEmpty(tasks)){
            return null;
        }

        // 是否存在相关的任务
        Optional<RoleTask> result = TaskCache.getRoleProcessTaskMap().get(role.getRoleId())
                .stream()
                .filter(t -> t.getTaskId() == taskId)
                .findFirst();
        return result.orElse(null);
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
    private void sendMsg(MsgTaskProto.RequestType reqType, List<RoleTask> allTask, String msg, Channel channel) {
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
