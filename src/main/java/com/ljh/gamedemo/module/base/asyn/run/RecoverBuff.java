package com.ljh.gamedemo.module.base.asyn.run;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户恢复的buff具体数值
 *
 * @Author: Heiku
 * @Date: 2019/7/22
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecoverBuff {

    /**
     * 每秒恢复血量
     */
    private Integer hpBuf;

    /**
     * 每秒恢复蓝量
     */
    private Integer mpBuf;
}
