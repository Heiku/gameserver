package com.ljh.gamedemo.util;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class MD5Util {

    /**
     * password salt
     */
    public static final String pwd_salt = "_&#809";

    /**
     * token salt
     */
    public static final String  token_salt = "&#810";


    /**
     * MD5 密码
     *
     * @param password      密码明文
     * @return              密码hash值
     */
    public static String hashPwd(String password){
        String pwdMD5 = Hashing.md5()
                .newHasher()
                .putString(password, Charsets.UTF_8)
                .putString(pwd_salt, Charsets.UTF_8)
                .hash().
                toString();

        return pwdMD5;
    }


    /**
     * 生成token信息
     * token = MD5(username + timestamp + token_salt) + timestamp[:8]
     *
     * @param username      玩家昵称
     * @return              token值
     */
    public static String hashToken(String username){
        LocalDateTime localDateTime = LocalDateTime.now();
        long timestamp = localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();

        String tokenMD5 = Hashing.md5()
                .newHasher()
                .putString(username, Charsets.UTF_8)
                .putLong(timestamp)
                .putString(token_salt, Charsets.UTF_8)
                .hash()
                .toString();

        String tsStr = String.valueOf(timestamp).substring(0, 8);
        return tokenMD5 + tsStr;
    }
}
