package com.statter.statter.base.mapper;


import com.statter.statter.base.entity.White;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WhiteMapper {

    @Insert("INSERT INTO white (`n`,`t`) VALUES (#{w.n},#{w.t});")
    void add(@Param("w") White w);

    @Select("SELECT * FROM white WHERE `t`=#{t};")
    List<White> findByType(@Param("t") byte t);

    @Select("SELECT COUNT(n) FROM white WHERE `n`=#{n} AND `t`=#{t};")
    int exist(@Param("n") String n, @Param("t") byte t);


}
