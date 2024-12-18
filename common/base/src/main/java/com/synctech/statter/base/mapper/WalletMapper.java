package com.statter.statter.base.mapper;


import com.statter.statter.base.entity.Wallet;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WalletMapper {

    @Insert("INSERT INTO wallet (`address`, `promotionAddress`, `alias`, `hasPledged`, `pledgeProcessId`) " + //
            "VALUES (#{w.address}, #{w.promotionAddress}, #{w.alias}, #{w.hasPledged}, #{w.pledgeProcessId});")
    void add(@Param("w") Wallet w);

    @Update("UPDATE wallet SET " + //
            " `promotionAddress`=#{w.promotionAddress}, " + //
            " `alias`=#{w.alias}, " + //
            " `hasPledged`=#{w.hasPledged}, " + //
            " `pledgeProcessId`=#{w.pledgeProcessId} " + //
            " WHERE `address`=#{w.address};")
    void update(@Param("w") Wallet w);

    @Update("UPDATE wallet SET `promotionAddress`=#{pa} WHERE `address`=#{wa};")
    void updateWalletPromotion(@Param("wa") String wa, @Param("pa") String pa);

    @Select("SELECT * FROM wallet ORDER BY `promotionAddress`;")
    List<Wallet> findAll();

    @Select("SELECT * FROM wallet WHERE `address`=#{a};")
    Wallet findOne(@Param("a") String address);

    @Select("SELECT count(address) FROM wallet WHERE `address`=#{a};")
    int exist(@Param("a") String address);

    @Select("SELECT * FROM wallet WHERE `promotionAddress`=#{pa};")
    List<Wallet> findByPromotionAddress(@Param("pa") String promotionAddress);

    @Update("UPDATE wallet SET `pledgeProcessId`=#{ppi},`hasPledged`=#{hasPledged} WHERE `address`=#{wa}")
    void updatePpi(@Param("wa") String wa, @Param("ppi") long ppi, @Param("hasPledged") boolean hasPledged);
}
