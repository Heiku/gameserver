package com.ljh.gamedemo.module.chat.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 聊天记录实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/6
 */

@Data
@NoArgsConstructor
public class ChatRecord {

    /**
     * 记录id
     */
    private Long id;

    /**
     * 发送人
     */
    private Long fromRole;

    /**
     * 接收人
     */
    private Long toRole;

    /**
     * 文本
     */
    private String content;

    /**
     * 发送时间
     */
    private Date sendTime;
}
