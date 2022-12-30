package com.synctech.statter.base.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class White implements Serializable {

    String n;

    byte t;

    public enum Type {
        Miner((byte) 1),
        Wallet((byte) 2),
        ;
        byte value;

        Type(byte v) {
            this.value = v;
        }

        public byte getValue() {
            return value;
        }

    }

}
