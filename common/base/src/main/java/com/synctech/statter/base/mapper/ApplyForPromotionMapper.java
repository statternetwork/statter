package com.synctech.statter.base.mapper;


import com.synctech.statter.base.entity.ApplyForPromotion;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApplyForPromotionMapper {

    @Insert("INSERT INTO apply_for_promotion (`createdTime`, `updatedTime`, `status`, `address`, `alias`, `introduction`) " +
            "VALUES (now(), now(), 1, #{p.address}, #{p.alias}, #{p.introduction});")
    void add(@Param("p") ApplyForPromotion p);

    @Update("UPDATE apply_for_promotion SET `alias`=#{p.alias}, `introduction`=#{p.introduction} WHERE `address`=#{p.address};")
    void updateInfo(@Param("p") ApplyForPromotion p);

    @Update("UPDATE apply_for_promotion SET `status`=#{s} WHERE `address`=#{a};")
    void updateStatus(@Param("a") String address, @Param("s") byte s);

    @Select("SELECT * FROM apply_for_promotion WHERE `address`=#{a};")
    ApplyForPromotion find(@Param("a") String address);

}
