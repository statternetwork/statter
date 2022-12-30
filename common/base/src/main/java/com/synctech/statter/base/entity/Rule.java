package com.synctech.statter.base.entity;

import com.synctech.statter.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Rule extends BaseEntity {

    byte type;

    String content;

    public enum Type {
        MinerTax((byte) 11),// miner tax // {"amount": "100.00","targetAddress":"ST60ff64f90e2400a86591f5db938ea4f3"}
        MinerPledge((byte) 21),  // miner pledge // {"amount": "100.00","targetAddress":"ST60ff64f90e2400a86591f5db938ea4f3","pledgeMinDays": "180"}
        WalletPledge((byte) 31), // wallet tax/pledge  // {"amount": "10000.00","targetAddress":"ST60ff64f90e2400a86591f5db938ea4f3"}

        WhiteList((byte) 51), // white list switch  // {"minerWhiteListSwitch": false}

        MinerMachine((byte) 91), // mining machine(miner) // {"updateScriptUrl": "http://192.168.1.164/downloads/statter.update.v1.0.1.sh","version": "v1.0.1"}
        ;
        byte value;
        String desc;

        Type(byte v) {
            this.value = v;
        }

        public static boolean isValid(byte t) {
            return MinerTax.compare(t) || MinerPledge.compare(t) || WalletPledge.compare(t) || MinerMachine.compare(t);
        }

        public byte getValue() {
            return value;
        }

        public boolean compare(byte s) {
            return this.value == s;
        }

    }


}
