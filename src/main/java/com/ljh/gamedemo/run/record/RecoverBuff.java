package com.ljh.gamedemo.run.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Heiku
 * @Date: 2019/7/22
 *
 * 用户恢复的buff具体数值
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecoverBuff {

    // 每秒恢复血量
    private Integer hpBuf;

    // 每秒恢复蓝量
    private Integer mpBuf;
}
