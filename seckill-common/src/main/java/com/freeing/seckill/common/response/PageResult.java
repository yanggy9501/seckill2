package com.freeing.seckill.common.response;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回结果
 *
 * @author yanggy
 */
public class PageResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页数
     */
    private long currentPage;

    /**
     * 每页记录数
     */
    private long pageSize;

    /**
     * 总页数
     */
    private long totalPage;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<?> rows;

    public PageResult() {

    }

    public PageResult(long currentPage, long pageSize, long totalPage, long total, List<?> rows) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.total = total;
        this.rows = rows;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long totalCount) {
        this.total = totalCount;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageResult{" +
            "currentPage=" + currentPage +
            ", pageSize=" + pageSize +
            ", totalPage=" + totalPage +
            ", total=" + total +
            ", rows=" + rows +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private long currentPage;

        private long pageSize;

        private long totalPage;

        private long total;

        private List<?> rows;

        public Builder currentPage(long currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Builder pageSize(long pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder totalPage(long totalPage) {
            this.totalPage = totalPage;
            return this;
        }

        public Builder total(long total) {
            this.total = total;
            return this;
        }

        public Builder rows(List<?> rows) {
            this.rows = rows;
            return this;
        }
        public PageResult build() {
            return new PageResult(currentPage, pageSize, totalPage, total, rows);
        }
    }
}
