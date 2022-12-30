package com.synctech.statter.base.mapper;


import com.synctech.statter.base.entity.Miner;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MinerMapper {

    @Insert("INSERT INTO miner (`sn`, `walletAddress`, `promotionAddress`, `hasPledged`, `pledgeProcessId`, `hasTaxed`, `taxProcessId`) " + //
            "VALUES (#{m.sn}, #{m.walletAddress}, #{m.promotionAddress}, #{m.hasPledged}, #{m.pledgeProcessId}, #{m.hasTaxed}, #{m.taxProcessId} );")
    void add(@Param("m") Miner m);

    @Update("<script>" + " UPDATE miner SET " + //
            " `walletAddress`=#{m.walletAddress}, " + //
            " `promotionAddress`=#{m.promotionAddress}, " + //
            " `hasPledged`=#{m.hasPledged}, " + //
            " `pledgeProcessId`=#{m.pledgeProcessId}, " + //
            " `hasTaxed`=#{m.hasTaxed}, " + //
            " `taxProcessId`=#{m.taxProcessId} " + //
            " WHERE `sn`=#{m.sn};" + //
            " </script>")
    void update(@Param("m") Miner m);

    @Update("UPDATE miner SET `walletAddress`=#{a},`bindDate`=NOW() WHERE `sn`=#{sn};")
    void updateWalletAddress(@Param("sn") String sn, @Param("a") String a);

    @Update("UPDATE miner SET `machineId`=#{mi},`cpuModelName`=#{cm} WHERE `sn`=#{sn};")
    void updateMachineId(@Param("sn") String sn, @Param("mi") String mi, @Param("cm") String cm);

    @Update("UPDATE miner SET `maxHistoryHash`=#{mhh} WHERE `sn`=#{sn};")
    void updateMaxHash(@Param("sn") String sn, @Param("mhh") long maxHistoryHash);

    @Select("SELECT * FROM miner ORDER BY `promotionAddress`, `walletAddress`;")
    List<Miner> findAll();

    @Select("SELECT `sn` FROM miner;")
    List<String> findAllSn();

    @Select("SELECT * FROM miner WHERE `sn`=#{sn};")
    Miner findOne(@Param("sn") String sn);

    @Select("SELECT count(sn) FROM miner WHERE `sn`=#{sn};")
    int exist(@Param("sn") String sn);

    @Select("SELECT * FROM miner WHERE `walletAddress`=#{walletAddress} ORDER BY `bindDate` DESC;")
    List<Miner> findByWalletAddress(@Param("walletAddress") String walletAddress);

    @Select("SELECT * FROM miner WHERE `promotionAddress`=#{promotionAddress} ORDER BY `walletAddress`, `bindDate` DESC;")
    List<Miner> findByPromotionAddress(@Param("promotionAddress") String promotionAddress);

    @Update("UPDATE miner SET `walletAddress`=#{m.walletAddress} WHERE `sn`=#{m.sn}")
    void updateWa(@Param("m") Miner miner);

    @Update("UPDATE miner SET `promotionAddress`=#{m.promotionAddress} WHERE `sn`=#{m.sn}")
    void updatePa(@Param("m") Miner miner);

    @Update("UPDATE miner SET `hasPledged`=#{m.hasPledged} WHERE `sn`=#{m.sn}")
    void updateHp(@Param("m") Miner miner);

    @Update("UPDATE miner SET `pledgeProcessId`=#{m.pledgeProcessId} WHERE `sn`=#{m.sn}")
    void updatePpi(@Param("m") Miner miner);
}
