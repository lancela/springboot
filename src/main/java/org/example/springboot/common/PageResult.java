package org.example.springboot.common;

import java.util.List;

public class PageResult<T> {

    // 当前页码
    private Integer pageNum;

    // 每页数量
    private Integer pageSize;

    // 总记录数
    private Long total;

    // 总页数
    private Integer pages;

    // 当前页数据
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Integer pageNum, Integer pageSize, Long total, List<T> records) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;

        // 计算总页数
        this.pages = (int) ((total + pageSize - 1) / pageSize);
    }


    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }


    // 分页信息保持不变，只转换里面的 records。
    public <R> PageResult<R> convert(java.util.function.Function<T, R> converter) {

        List<R> newRecords = records.stream().map(converter).toList();

        PageResult<R> result = new PageResult<>();

        result.setPageNum(this.pageNum);
        result.setPageSize(this.pageSize);
        result.setTotal(this.total);
        result.setPages(this.pages);
        result.setRecords(newRecords);

        return result;
    }
}
