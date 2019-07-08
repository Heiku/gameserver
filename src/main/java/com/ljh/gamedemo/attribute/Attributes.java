package com.ljh.gamedemo.attribute;

import io.netty.util.AttributeKey;

public interface Attributes {

    AttributeKey<Long> USER_ID = AttributeKey.newInstance("userId");
}
