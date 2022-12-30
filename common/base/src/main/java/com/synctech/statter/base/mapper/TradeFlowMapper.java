package com.synctech.statter.base.mapper;


import com.synctech.statter.base.entity.TradeFlow;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TradeFlowMapper {

    @Insert("INSERT INTO trade_flow (" +
            " `tradeNo`,`tradeType`,`tradeSubType`,`from`,`to`,`tradeTime`,`tradeAmount`,`gas`,`publicKey`,`sign`,`uniqueCode`,`contractNumber`,`machineCode`,`blockIndex`,`stage` " +
            " ) VALUES (" +
            " #{t.tradeNo},#{t.tradeType},#{t.tradeSubType},#{t.from},#{t.to},#{t.tradeTime},#{t.tradeAmount},#{t.gas},#{t.publicKey},#{t.sign},#{t.uniqueCode},#{t.contractNumber},#{t.machineCode},#{t.blockIndex},#{t.stage} " +
            ");")
    void add(@Param("t") TradeFlow tradeFlow);

    @Insert("INSERT INTO trade_flow_archive (" +
            " `tradeNo`,`tradeType`,`tradeSubType`,`from`,`to`,`tradeTime`,`tradeAmount`,`gas`,`publicKey`,`sign`,`uniqueCode`,`contractNumber`,`machineCode`,`blockIndex` " +
            " ) VALUES (" +
            " #{t.tradeNo},#{t.tradeType},#{t.tradeSubType},#{t.from},#{t.to},#{t.tradeTime},#{t.tradeAmount},#{t.gas},#{t.publicKey},#{t.sign},#{t.uniqueCode},#{t.contractNumber},#{t.machineCode},#{t.blockIndex} " +
            ");")
    void addArchive(@Param("t") TradeFlow tradeFlow);

    @Insert("INSERT INTO trade_flow_unknow (" +
            " `tradeNo`,`tradeType`,`tradeSubType`,`from`,`to`,`tradeTime`,`tradeAmount`,`gas`,`publicKey`,`sign`,`uniqueCode`,`contractNumber`,`machineCode`,`blockIndex` " +
            " ) VALUES (" +
            " #{t.tradeNo},#{t.tradeType},#{t.tradeSubType},#{t.from},#{t.to},#{t.tradeTime},#{t.tradeAmount},#{t.gas},#{t.publicKey},#{t.sign},#{t.uniqueCode},#{t.contractNumber},#{t.machineCode},#{t.blockIndex} " +
            ");")
    void addUnknow(@Param("t") TradeFlow tradeFlow);

    @Delete("DELETE FROM trade_flow WHERE tradeNo=#{tradeNo};")
    void delete(@Param("tradeNo") String tradeNo);

    @Update("UPDATE trade_flow SET stage=#{stage} WHERE tradeNo=#{tradeNo};")
    void updateStage(@Param("tradeNo") String tradeNo, @Param("stage") byte stage);

    @Select("SELECT trade_flow WHERE tradeNo=#{tradeNo};")
    TradeFlow findOne(@Param("tradeNo") String tradeNo);

    @Select("SELECT f.* FROM trade_flow f WHERE blockIndex=( SELECT MIN( f2.blockIndex ) FROM trade_flow f2 WHERE f2.stage = 0 );")
    List<TradeFlow> findLowestBlockIndexWithNotProcess();

    @Select("SELECT trade_flow WHERE stage=1;")
    List<TradeFlow> findSuccess();

    @Select("SELECT trade_flow WHERE blockIndex=#{blockIndex};")
    List<TradeFlow> findBlockIndex(@Param("blockIndex") long blockIndex);

}
