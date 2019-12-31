package com.mhkj.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestData<T> {

    private static final String SUCCESS_INFO = "操作成功";
    private static final String FAIL_INFO = "操作失败";

    // 接口调用状态，true表示调用成功，false表示调用失败
    private boolean status;
    // 提示信息，如果没有设置，则根据status值使用默认提示
    private String info;
    // 返回数据
    private T data;

    private RestData(boolean status, String info) {
        this.status = status;
        this.info = info;
    }

    /**
     * 操作是否成功
     */
    public boolean isSuccess() {
        return this.status;
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> success() {
        return this.success(null);
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> success(String info) {
        return success(info, null);
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> success(String info, T data) {
        if (info != null && !info.isEmpty()) {
            this.status = true;
            this.info = info;
            this.data = data;
            return this;
        } else {
            return success(SUCCESS_INFO, data);
        }
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> error() {
        return this.error(null);
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> error(String info) {
        return error(info, null);
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> error(String info, T data) {
        if (info != null && !info.isEmpty()) {
            this.status = false;
            this.info = info;
            this.data = data;
            return this;
        } else {
            return error(FAIL_INFO, data);
        }
    }

}
