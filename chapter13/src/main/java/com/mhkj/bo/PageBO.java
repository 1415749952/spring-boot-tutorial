package com.mhkj.bo;

import lombok.Data;

@Data
public class PageBO {

    /**
     * 当前页数
     */
    private int pageNum = 1;
    /**
     * 每页显示记录数
     */
    private int pageSize = 20;

}
