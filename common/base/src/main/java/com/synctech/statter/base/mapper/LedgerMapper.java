package com.statter.statter.base.mapper;


import com.statter.statter.base.entity.Ledger;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LedgerMapper {

    @Insert("INSERT INTO ledger (`id`, `createdTime`, `blockIndex`, `state`, `sn`, `address`, `promotionAddress`, `data`) VALUES " +
            "(#{l.id}, now(), #{l.blockIndex}, #{l.state}, #{l.sn}, #{l.address}, #{l.promotionAddress}, #{l.data});")
    void add(@Param("l") Ledger l);

    @Update("DELETE from ledger WHERE `id`=#{id};")
    void delete(@Param("id") String id);

    @Delete("DELETE from ledger WHERE `blockIndex`=#{blockIndex} and `state`=1;")
    void deleteInvalid(@Param("blockIndex") long blockIndex);

    @Update("UPDATE ledger SET `state`=3,`mingProfit`=#{mingProfit} WHERE `id`=#{id};")
    void tagValid(@Param("id") String id, @Param("mingProfit") String mingProfit);

    @Update("UPDATE ledger SET `state`=2 WHERE `blockIndex`=#{blockIndex} and `state`=1;")
    void tagInvalid(@Param("blockIndex") long blockIndex);

    @Select("SELECT * FROM ledger WHERE id=#{id};")
    Ledger findOne(@Param("id") String id);

    @Select("SELECT * FROM ledger WHERE `state`=3 ORDER BY `createdTime` ASC LIMIT 10;")
    List<Ledger> findValid();


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    @Insert("INSERT INTO ledger_archive (`id`, `createdTime`, `blockIndex`, `sn`, `address`, `promotionAddress`, `data`) VALUES " +
//            "(#{l.id}, #{l.createdTime}, #{l.blockIndex}, #{l.sn}, #{l.address}, #{l.promotionAddress}, #{l.data});")
//    void process(@Param("l") Ledger l);
//
//    @Select("SELECT * FROM ledger_archive WHERE blockIndex=#{bi};")
//    Ledger findByBlockIndex(@Param("bi") long blockIndex);
//
//    @Select("SELECT * FROM ledger_archive WHERE `promotionAddress`=#{p} and `blockIndex`>#{si} and `blockIndex`<#{ei} ORDER BY `blockIndex` DESC;")
//    List<Ledger> findPromotionLedgerByBlockIndexArea(@Param("p") String p, @Param("si") long si, @Param("ei") long ei);
//
//    @Select("SELECT COUNT(`promotionAddress`) FROM ledger_archive WHERE `promotionAddress`=#{p};")
//    Long countPromotionLedger(@Param("p") String p);
//    @Select("SELECT * FROM ledger_archive WHERE `promotionAddress`=#{p} ORDER BY `blockIndex` ASC LIMIT #{startIndex},#{endIndex};")
//    List<Ledger> pagePromotionLedger(@Param("p") String p, @Param("startIndex") long startIndex, @Param("endIndex") long endIndex);
//
//    @Select("SELECT * FROM ledger_archive WHERE `promotionAddress`=#{p} ORDER BY `blockIndex` ASC limit #{lm};")
//    List<Ledger> findPromotionLedgerLimit(@Param("p") String p, @Param("lm") int lm);


}
