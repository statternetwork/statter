package com.synctech.statter.base.mapper;


import com.synctech.statter.base.entity.Promotion;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PromotionMapper {

    @Insert("INSERT INTO promotion (`createdTime`, `updatedTime`, `status`, `address`, `minerCount`,`alias`, `introduction`, `managementKey`) " +
            "VALUES (now(), now(), 1, #{p.address}, #{p.minerCount}, #{p.alias}, #{p.introduction}, #{p.managementKey});")
    void add(@Param("p") Promotion p);

    @Update("UPDATE promotion SET `minerCount`=#{mc},`hash`=#{h} WHERE `address`=#{a};")
    void updateMinerCount(@Param("a") String address, @Param("mc") int minerCount, @Param("h") long hash);

    @Update("UPDATE promotion SET `alias`=#{p.alias}, `introduction`=#{p.introduction} WHERE `address`=#{p.address};")
    void updateInfo(@Param("p") Promotion p);

    @Update("UPDATE promotion SET `secretKey`=#{sk},`secretKeyUptTime`=now() WHERE `address`=#{a};")
    void updateSecretKey(@Param("a") String a, @Param("sk") String sk);

    @Select("SELECT * FROM promotion;")
    List<Promotion> findAll();

    @Select("SELECT * FROM promotion " +
            "WHERE `code` LIKE concat('%',#{kw},'%') OR `alias` LIKE concat('%',#{kw},'%') " +
            "ORDER BY minerCount DESC LIMIT ${si},${size};")
    List<Promotion> page(@Param("si") int si, @Param("size") int size, @Param("kw") String kw);
    @Select("SELECT COUNT(address) FROM promotion " +
            "WHERE `code` LIKE concat('%',#{kw},'%') OR `alias` LIKE concat('%',#{kw},'%') " +
            ";")
    int count(@Param("kw") String kw);

    @Select("SELECT * FROM promotion WHERE `address`=#{a};")
    Promotion findOne(@Param("a") String address);

    @Select("SELECT MAX(`code`) FROM `promotion`")
    String maxCode();

    @Select("SELECT count(address) FROM promotion WHERE `address`=#{a};")
    int exist(@Param("a") String address);

    /**
     * Statistics the number of mining machines in each mining pool
     *
     * @return
     */
    @Select("SELECT promotionAddress AS address, count(*) AS minerCount FROM miner WHERE promotionAddress != '' GROUP BY promotionAddress;")
    List<Promotion> countMiners();

    // promotion hash only count the miners which is hasPledged.
    @Select("SELECT m.promotionAddress AS address, sum( cm.standardHash ) AS `hash` FROM miner m LEFT JOIN cpu_model cm ON m.cpuModelName = cm.cpuModelName WHERE m.hasPledged=1 AND m.promotionAddress != '' GROUP BY m.promotionAddress;")
    List<Promotion> countHash();

//    @Select("SELECT count(*) FROM miner WHERE `promotionAddress`=#{a}")
//    int count(@Param("a") String address);
}
