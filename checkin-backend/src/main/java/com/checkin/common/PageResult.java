package com.checkin.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 */
@Data
public class PageResult<T> implements Serializable {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页数据列表
     */
    private List<T> records;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;

    public PageResult() {
    }

    public PageResult(Long total, List<T> records, Long current, Long size, Long pages) {
        this.total = total;
        this.records = records;
        this.current = current;
        this.size = size;
        this.pages = pages;
    }

    /**
     * 从 MyBatis-Plus IPage 转换
     */
    public static <T> PageResult<T> from(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setRecords(page.getRecords());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }
}
