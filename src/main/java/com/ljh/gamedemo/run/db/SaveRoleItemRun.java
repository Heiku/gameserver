package com.ljh.gamedemo.run.db;

import com.ljh.gamedemo.dao.RoleItemsDao;
import com.ljh.gamedemo.entity.Items;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Heiku
 * @Date: 2019/7/22
 */

@Slf4j
public class SaveRoleItemRun implements Runnable {

    private Items items;

    private Role role;

    private RoleItemsDao itemsDao = SpringUtil.getBean(RoleItemsDao.class);

    public SaveRoleItemRun(Items items, Role role){
        this.items = items;
        this.role = role;
    }

    @Override
    public void run() {
        int i = itemsDao.updateItem(items.getNum(), role.getRoleId(), items.getItemsId());
        log.info("定时执行用户背包数据保存，更新数量：" + i);
    }
}
