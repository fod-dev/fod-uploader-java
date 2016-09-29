package com.fortify.fod.fodapi.models;

public class GenericListResponse<T> {
    private T[] items;
    private int totalCount;

    public int getTotalCount() { return totalCount; }
    public T[] getItems() { return items;}
}
