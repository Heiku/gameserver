package com.ljh.gamedemo.run.db;

import com.ljh.gamedemo.module.items.dao.RoleItemsDao;
import com.ljh.gamedemo.module.items.bean.Items;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 通过任务更新数据库中的 玩家物品信息，减轻玩家频繁使用物品对数据库的写压力
 *
 * @Author: Heiku
 * @Date: 2019/7/22
 */

@Slf4j
public class SaveRoleItemRun implements Runnable {

    /**
     * 物品信息
     */
    private Items items;

    /**
     * 玩家信息
     */
    private Role role;

    /**
     * RoleItemsDao
     */
    private RoleItemsDao itemsDao = SpringUtil.getBean(RoleItemsDao.class);

    public SaveRoleItemRun(Items items, Role role){
        this.items = items;
        this.role = role;
    }

    @Override
    public void run() {
        int i = itemsDao.updateItem(items.getNum(), role.getRoleId(), items.getItemsId());
        log.info("update role_items, affected rows: " + i);
    }
}
