package com.synctech.statter.base.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class Page<T extends Serializable> implements Serializable {

    @ApiModelProperty(name = "page", value = "Page number. The first page is 1.")
    int page;
    @ApiModelProperty(name = "size", value = "The data size per page, max is 10.")
    int size;
    @ApiModelProperty(name = "total", value = "The number of the data matched the conditions.")
    int total;
    @ApiModelProperty(name = "data")
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
