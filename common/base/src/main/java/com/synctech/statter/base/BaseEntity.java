package com.synctech.statter.base;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class BaseEntity implements Serializable {

    long id;

    Timestamp createdTime;

    Timestamp updatedTime;

    byte status;

}
