package com.ljh.gamedemo.module.task.local;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.email.bean.EmailGoods;
import com.ljh.gamedemo.module.task.bean.Task;
import com.ljh.gamedemo.module.task.cache.TaskCache;
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

    static {
        try {
            taskFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "csv/task.xlsx");
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
        log.info("task 数据载入成功");
    }

    public static void main(String[] args) {
        readExcel();

        TaskCache.getIdTaskMap().forEach((k, v) -> System.out.println(k + " " + v));
    }
}
