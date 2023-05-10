package com.synctech.statter.base.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * the mining machine
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CpuModel implements Serializable {

    @ApiModelProperty(name = "cpuModelName", value = "The model of the mining machine's cpu")
    @JsonProperty("cpuModelName")
    String cpuModelName;

    @ApiModelProperty(name = "standardHash", value = "The standard hash of this cpu")
    @JsonProperty("standardHash")
    long standardHash;

}
