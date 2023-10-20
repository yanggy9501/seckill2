package com.freeing.seckill.common.response;

import com.freeing.seckill.common.enums.ResultCode;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 统一返回结果
 *
 *  ps: dataM()，data()，put("data", obj) 不能混用
 *  dataM 实现如下数据结构：
 *  "data": {
 *         "k1": "v1",
 *         "k2": "v2"
 *         "k3": {"k4": "v4"}
 *     }
 *
 * @author yanggy
 * @date 2021/10/9 23:36
 */
public class R extends HashMap<String, Object> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private static final String CODE = "code";

    /**
     * 返回内容|提示用户信息(面向用户)
     */
    private static final String MSG = "msg";

    /**
     * 错误信息（面向开发者）
     */
    private static final String ERROR_MSG = "errorMsg";

    /**
     * 数据对象
     */
    private static final String DATA = "data";

    /**
     * 构造函数
     */
    private R() {}

    /**
     * 返回数据和消息
     *
     * @param msg 消息
     * @param data 数据
     * @return R
     */
    public static R success(String msg, Object data) {
        R r = new R();
        r.put(CODE, ResultCode.SUCCESS.getCode());
        if (msg == null || msg.isEmpty()) {
            r.put(MSG, ResultCode.SUCCESS.getMsg());
        } else {
            r.put(MSG, msg);
        }
        if (data != null) {
            r.put(DATA, data);
        }
        return r;
    }

    /**
     * 返回成功默认消息
     *
     * @return 成功消息
     */
    public static R success() {
        return R.success(null, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg 成功消息
     * @return R
     */
    public static R success(String msg) {
        return R.success(msg, null);
    }

    /**
     * 返回成功数据
     *
     * @param data 数据对象
     * @return 数据对象
     */
    public static R success(Object data) {
        return R.success(null, data);
    }

    /**
     * 返回失败数据
     *
     * @param code 状态码
     * @param msg 返回信息
     * @param data 数据对象
     * @return R
     */
    public static R error(Integer code, String msg, Object data) {
        R r = new R();
        r.put(CODE, code);
        if (msg == null || msg.isEmpty()) {
            r.put(MSG, ResultCode.ERROR.getCode());
        } else {
            r.put(MSG, msg);
        }
        if (data != null) {
            r.put(DATA, data);
        }
        return r;
    }

    /**
     * 返回失败数据和消息
     *
     * @param msg 返回信息
     * @param data 数据对象
     * @return R
     */
    public static R error(String msg, Object data) {
        return error(ResultCode.ERROR.getCode(), msg, data);
    }

    /**
     * 返回失败数据和消息
     *
     * @param code 状态码
     * @param msg 返回信息
     * @return R
     */
    public static R error(Integer code, String msg) {
        return error(code, msg, null);
    }

    /**
     * 返回异常数据
     *
     * @return 消息
     */
    public static R error() {
        return R.error(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg(), null);
    }

    /**
     * 返回异常数据
     *
     * @param msg 消息
     * @return R
     */
    public static R error(String msg) {
        return R.error(ResultCode.ERROR.getCode(), msg, null);
    }

    /**
     * 返回失败数据
     *
     * @param msg 返回信息
     * @param data 数据对象
     * @return R
     */
    public static R fail(String msg, Object data) {
        R r = new R();
        r.put(CODE, ResultCode.FAILURE.getCode());
        if (msg == null || msg.isEmpty()) {
            r.put(MSG, ResultCode.FAILURE.getCode());
        } else {
            r.put(MSG, msg);
        }
        if (data != null) {
            r.put(DATA, data);
        }
        return r;
    }

    /**
     * 返回失败数据和消息
     *
     * @param msg 返回信息
     * @return R
     */
    public static R fail(String msg) {
        return fail(msg, null);
    }

    /**
     * 返回成功默认消息
     *
     * @return R
     */
    public static R fail() {
        return R.fail(null, null);
    }

    /**
     * 返回失败数据
     *
     * @param data 数据对象
     * @return 数据对象
     */
    public static R fail(Object data) {
        return R.fail(null, data);
    }

    /**
     * 设置状态码
     *
     * @param code 状态码
     * @return R
     */
    public R code(String code) {
        super.put(CODE, code);
        return this;
    }

    /**
     * 设置返回消息
     *
     * @param msg 消息
     * @return R
     */
    public R msg(String msg) {
        super.put(MSG, msg);
        return this;
    }

    /**
     * 设置数据，如果 key=data 已经存在，再次调用抛出异常，所以 data方法仅能调用一次。
     *
     * @param data 数据对象
     * @return R
     */
    public R data(Object data) {
        // 由于链式调用 key = data 只有一个后续调用都会覆盖前面的。如果key=data已经存在，再次调用抛出异常
        if (super.containsKey(DATA)) {
            throw new RuntimeException("Wrong calling method R#data(Object data)");
        }
        super.put(DATA, data);
        return this;
    }

    /**
     * 设置错误消息
     *
     * @param errorMsg
     * @return
     */
    public R errorMsg(String errorMsg) {
        super.put(ERROR_MSG, errorMsg);
        return this;
    }

    /**
     * 自定义数据对象
     *
     * @param key 数据的key
     * @param value 数据的值
     * @return R
     */
    @Override
    public R put (String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     *  设置HashMap数据 key = data hk hv
     *  实现如下数据结构：
     *  "data": {
     *         "k1": "v1",
     *         "k2": "v2"
     *         "k3": {"k4": "v4"}
     *     }
     *
     * @param key hk
     * @param value hv
     * @return R
     */
    public R dataM(String key, Object value) {
        if (containsKey(DATA)) {
            // 是否为DataMap类型，否则抛出异常
            Object dataMap = super.get(DATA);
            if (dataMap instanceof DataMap) {
                ((DataMap) dataMap).put(key, value);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            // 初始化时不存在，创建hashMap对象
            DataMap dataMapInstance = DataMap.getDataMapInstance();
            dataMapInstance.put(key,  value);
            this.data(dataMapInstance);
        }
        return this;
    }

    /**
     * 获取状态码
     *
     * @return code
     */
    public String getCode() {
        return super.get(CODE).toString();
    }

    /**
     *  内部类, 封装key=data的下数据：加强data的灵活性
     */
    public static class DataMap extends HashMap<String, Object> {
        public static DataMap getDataMapInstance() {
            return new DataMap();
        }
    }
}
