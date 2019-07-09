package com.ljh.gamedemo.entity;

import lombok.Data;

@Data
public class TalkText {

    private String entityName;

    /**
     * 对话等级，相同的npc可对话多次，根据你的等级而定
     */
    private int level;

    /**
     * 判断是谁先发起对话
     */
    private int first;

    /**
     * 对话文本
     */
    private String content;
}
