package com.statter.statter.base.mapper;


import com.statter.statter.base.entity.Ledger;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.statter.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

@Mapper
@Repository
public interface PromotionLedgerMapper {

    @Select("SELECT count(*) FROM information_schema.TABLES WHERE table_name='ledger_pro'")
    int existTable(@Param("promotion") String promotion);

    @Update("DROP TABLE IF EXISTS ledger_pro")
    int dropTable(@Param("promotion") String promotion);

    @Update("CREATE TABLE ledger_pro (blockIndex bigint(20) NOT NULL,createdTime timestamp NOT NULL," +
            "sn char(32) NOT NULL,address char(" + WALLET_ADDRESS_LENGTH + ") NOT NULL,mingProfit varchar(255),data longblob NOT NULL,PRIMARY KEY (blockIndex))")
    int createNewTable(@Param("promotion") String promotion);

    @Insert("INSERT INTO ledger_pro (blockIndex, createdTime, sn, address, mingProfit, data) " +
            "VALUES (#{ledger.blockIndex},#{ledger.createdTime},#{ledger.sn},#{ledger.address},#{ledger.mingProfit},#{ledger.data})")
    int insert(@Param("promotion") String promotion, @Param("ledger") Ledger ledger);

    // @Select("SELECT COUNT(`blockIndex`) FROM ledger_pro;")
    @Select("SELECT TABLE_ROWS FROM information_schema.TABLES WHERE table_name='ledger_pro'")
    Long count(@Param("promotion") String promotion);

    @Select("SELECT * FROM ledger_pro WHERE blockIndex=#{bi};")
    Ledger findByBlockIndex(@Param("promotion") String promotion, @Param("bi") long blockIndex);

    @Select("SELECT * FROM ledger_pro WHERE `blockIndex`>#{si} and `blockIndex`<#{ei} ORDER BY `blockIndex` DESC;")
    List<Ledger> findByBlockIndexArea(@Param("promotion") String promotion, @Param("si") long si, @Param("ei") long ei);

    @Select("SELECT * FROM ledger_pro ORDER BY `blockIndex` DESC limit #{lm};")
    List<Ledger> findLimit(@Param("promotion") String promotion, @Param("lm") int lm);

    @Select("SELECT * FROM ledger_pro ORDER BY `blockIndex` ASC LIMIT #{startIndex},#{endIndex};")
    List<Ledger> page(@Param("promotion") String promotion, @Param("startIndex") long startIndex, @Param("endIndex") long endIndex);

}
