package com.statter.statter.base.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ApiLimit implements Serializable {

    int id;

    @Schema(name = "api uri", description = "The target api uri, if one uri can't be match any uri, means this is uri is thoroughly open")
    String uri;

    @Schema(name = "api limit per day", description = "this api can be access such times in each day, if it's zero, means that this api is thoroughly open in each day")
    long limitPerDay;

    @Schema(name = "api limit per day", description = "this api can be access such times in each hour, if it's zero, means that this api is thoroughly open in each hour")
    long limitPerHour;

    @Schema(name = "address", description = "if specify this value, means that this limit record is belong to the specific promotion, otherwise, this rule is effect to other promotion who has no specific setting")
    String promotionAddress;

    public boolean validate() {
        return StringUtils.isNotBlank(this.uri);
    }

}
