package com.example.githubtrailblazer.connector;

import java.util.HashMap;

/**
 * PaginationData class
 */
public class PaginationData {
    private HashMap<Connector.Service, Pagination> paginations = new HashMap();
    private int hasNextPageCount = 0;

    public static class Pagination {
        public Boolean hasNextPage;
        public String endCursor;

        public Pagination() {
            this.hasNextPage = false;
            this.endCursor = null;
        }

        public Pagination(Boolean hasNextPage, String endCursor) {
            this.hasNextPage = hasNextPage;
            this.endCursor = endCursor;
        }
    }

    public PaginationData() {
        paginations.put(Connector.Service.GITHUB, new Pagination());
        paginations.put(Connector.Service.GITLAB, new Pagination());
    }

    public PaginationData setPagination(Connector.Service service, Pagination pagination) {
        Pagination oldPagination = paginations.get(service);
        if (oldPagination != null && oldPagination.hasNextPage) hasNextPageCount--;
        if (pagination.hasNextPage) hasNextPageCount++;
        paginations.put(service, pagination);
        return this;
    }

    public Pagination getPagination(Connector.Service service) {
        return paginations.get(service);
    }

    public boolean hasNextPage() {
        return hasNextPageCount > 0;
    }
}
