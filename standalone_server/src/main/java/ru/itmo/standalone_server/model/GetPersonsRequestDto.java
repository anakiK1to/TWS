package ru.itmo.standalone_server.model;

public class GetPersonsRequestDto {
    private String query;
    private Integer limit;
    private Integer offset;

    public GetPersonsRequestDto() {}

    public GetPersonsRequestDto(String query, Integer limit, Integer offset) {
        this.query = query;
        this.limit = limit;
        this.offset = offset;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
