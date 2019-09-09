package com.ljh.gamedemo.module.task.local;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.email.bean.EmailGoods;
import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.bean.Task;
import com.ljh.gamedemo.module.task.cache.TaskCache;
import com.ljh.gamedemo.module.task.dao.TaskDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ljh.gamedemo.util.ExcelUtil.*;

/**
 * 本地加载任务信息
 *
 * @Author: Heiku
 * @Date: 2019/9/6
 */

@Slf4j
public class LocalTaskMap {

    /**
     * 任务文件
     */
    private static File taskFile = null;

    /**
     * taskDao
     */
    private static TaskDao taskDao;

    static {
        try {
            taskFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/task.xlsx");

            taskDao = SpringUtil.getBean(TaskDao.class);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     *  获取csv文件信息，得到map集合
     */
    public static void readExcel(){

        // 判断文件类型，获取workBook
        Workbook workbook = formatWorkBook(taskFile);

        // 遍历获取数据
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                log.error("excel sheet is null, please recheck the task file resolve");
                break;
            }

            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);
                if (row != null) {

                    Task task = new Task();
                    task.setTaskId(getLongValue(row.getCell(0)));
                    task.setName(getValue(row.getCell(1)));
                    task.setType(getIntValue(row.getCell(2)));
                    task.setDesId(getLongValue(row.getCell(3)));
                    task.setGoal(getIntValue(row.getCell(4)));
                    task.setTimes(getIntValue(row.getCell(5)));
                    task.setGold(getIntValue(row.getCell(6)));
                    task.setDesc(getValue(row.getCell(9)));

                    // 解析奖励列表
                    String rewardStr = getValue(row.getCell(7));
                    String[] rewardArr = rewardStr.split("\\|");
                    List<EmailGoods> edList = Lists.newArrayList();
                    for (String s : rewardArr) {
                        Long goodsId = Long.valueOf(s.substring(0, s.indexOf(",")));
                        Integer num = Integer.valueOf(s.substring(s.indexOf(",") + 1));
                        edList.add(new EmailGoods(goodsId, num));
                    }
                    task.setGoods(edList);

                    // 解析子任务
                    String taskStr = getValue(row.getCell(8));
                    List<Task> tasks = Lists.newArrayList();
                    if (!StringUtils.isEmpty(taskStr)){
                        String[] taskArr = taskStr.split("\\|");
                        for (String s : taskArr) {
                            tasks.add(TaskCache.getIdTaskMap().get(Long.valueOf(s)));
                        }
                    }
                    task.setTasks(tasks);

                    // 存储
                    TaskCache.getIdTaskMap().put(task.getTaskId(), task);

                    List<Task> taskList = Optional.ofNullable(TaskCache.getTypeTaskMap().get(task.getType()))
                            .orElse(Lists.newArrayList());
                    taskList.add(task);
                    TaskCache.getTypeTaskMap().put(task.getType(), taskList);
                }
            }
        }

        // 读取数据库任务信息
        readDB();

        log.info("task 数据载入成功");
    }

    /**
     * 读取数据库表中的任务状态信息
     */
    public static void readDB(){

        // 读取所有玩家正在进行中的任务信息
        List<RoleTask> processTaskList = Optional.ofNullable(taskDao.selectUnCompleteTask())
                .orElse(Lists.newArrayList());
        processTaskList.forEach(roleTask -> {
            Task task = TaskCache.getIdTaskMap().get(roleTask.getTaskId());
            task.setId(roleTask.getId());

            List<Task> tasks = Optional.ofNullable(TaskCache.getRoleProcessTaskMap().get(roleTask.getRoleId()))
                    .orElse(Lists.newArrayList());
            tasks.add(task);
            TaskCache.getRoleProcessTaskMap().put(roleTask.getRoleId(), tasks);
        });


        // 读取所有玩家已经完成的任务信息
        List<RoleTask> finishTaskList = Optional.ofNullable(taskDao.selectFinishTask())
                .orElse(Lists.newArrayList());
        finishTaskList.forEach(roleTask -> {

            List<RoleTask> tasks = Optional.ofNullable(TaskCache.getRoleDoneTaskMap().get(roleTask.getRoleId()))
                    .orElse(Lists.newArrayList());
            tasks.add(roleTask);
            TaskCache.getRoleDoneTaskMap().put(roleTask.getRoleId(), tasks);
        });
    }

    public static void main(String[] args) {
        readExcel();

        TaskCache.getIdTaskMap().forEach((k, v) -> System.out.println(k + " " + v));
    }
}
