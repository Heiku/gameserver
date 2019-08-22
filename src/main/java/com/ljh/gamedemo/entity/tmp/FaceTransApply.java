package com.ljh.gamedemo.entity.tmp;

import lombok.Data;

/**
 * @Author: Heiku
 * @Date: 2019/8/22
 */

@Data
public class FaceTransApply {

    /**
     * 申请id
     */
    private Long id;

    /**
     * 发起人
     */
    private Long promoter;

    /**
     * 接收人
     */
    private Long receiver;
}
