package com.statter.statter.manager.api.controller.v1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ReqMinerInfo {
    String sn;
    String mi;// machine id
    String cm;// cpu model
    int v;
    String ver;
    String dv;
}
