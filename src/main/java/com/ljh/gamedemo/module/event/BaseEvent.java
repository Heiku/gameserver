package com.ljh.gamedemo.module.event;

import com.ljh.gamedemo.module.role.bean.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 基础的事件信息
 *
 * @Author: Heiku
 * @Date: 2019/9/9
 */

@AllArgsConstructor
@Data
public class BaseEvent implements Serializable {

    /**
     * 玩家信息
     */
    private Role role;
}
