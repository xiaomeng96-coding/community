package com.nowcoder.community.entity;

/**
 * @author : zhiHao
 * @since : 2021/9/21
 */
public class Page {

    private int current = 1;
    private int limit = 10;
    private int rowCounts;
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = Math.max(1, current);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public long getRowCounts() {
        return rowCounts;
    }

    public void setRowCounts(int rowCounts) {
        this.rowCounts = Math.max(0, rowCounts);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOffset() {
        return (current - 1)*limit;
    }

    public int getTotal() {
        return (int)Math.ceil(rowCounts/(double)limit);
    }

    public int getFrom() {
        return Math.max(current - 2, 1);
    }

    public int getTo() {
        return Math.min(current + 2, getTotal());
    }
}
