package com.freeing.seckill.common.shiro.utils;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * 工具类
 */
public class CommonsUtils {
    /**
     * 手机号正则校验
     * @param phone 手机号
     * @return 校验是否成功
     */
    public static boolean phoneRegexCheck(String phone){
        if(phone.length()!=11){
            return false;
        }
        return true;
    }

    /**
     * 获取六位数验证码
     * @return 验证码
     */
    public static int getCode(){
        return (int)((Math.random()*9+1)*100000);
    }

    /**
     * 使用md5加密
     * @param password 需要加密的密码
     * @param phoneNumber 手机号
     * @return 返回加密后的密码
     */
    public static String encryptPassword(String password, String phoneNumber){ //userId作为盐值
        return String.valueOf(new SimpleHash("MD5", password, phoneNumber, 1024));
    }

    /**
     * 获取请求域中的UserId
     */
//    public static Integer getUserId(HttpServletRequest request){
//        return Integer.parseInt(request.getAttribute(BaseConstants.USER_ID).toString());
//    }
}
