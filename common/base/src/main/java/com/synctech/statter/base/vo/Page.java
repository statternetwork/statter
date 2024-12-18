package com.statter.statter.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class Page<T extends Serializable> implements Serializable {

    @Schema(name = "page", description = "Page number. The first page is 1.")
    int page;
    @Schema(name = "size", description = "The data size per page, max is 10.")
    int size;
    @Schema(name = "total", description = "The number of the data matched the conditions.")
    int total;
    @Schema(name = "data")
    List<T> data;

    public Page(int page, int size) {
        this.page = page;
        this.size = size;
        this.total = 0;
        this.data = new ArrayList<>();
    }

    public int startIndex() {
        return (page - 1) * size;
    }

    public int endIndex() {
        return page * size;
    }

}
