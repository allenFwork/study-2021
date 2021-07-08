package com.study.util;

public class PageUtil {
    private int page;
    private int limit;
    private int count;
    private int start;

    public PageUtil(){

    }

    public PageUtil(int page, int limit) {
        this.page = page;
        this.limit = limit;
        this.start = (page-1) * limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
