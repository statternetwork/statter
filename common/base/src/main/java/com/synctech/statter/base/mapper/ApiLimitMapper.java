package com.synctech.statter.base.mapper;


import com.synctech.statter.base.entity.ApiLimit;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ApiLimitMapper {

    @Insert("INSERT INTO api_limit (`uri`,`limitPerDay`,`limitPerHour`,`promotionAddress`) VALUES " +
            "(#{am.uri},#{am.limitPerDay},#{am.limitPerHour}#{am.promotionAddress});")
    @SelectKey(statement = "select LAST_INSERT_ID()", keyProperty = "am.id", before = false, resultType = Integer.class)
    void add(@Param("am") ApiLimit am);

    @Delete("DELETE FROM api_limit WHERE id=#{id};")
    void delete(@Param("id") int id);

    @Insert("UPDATE api_limit SET `uri`=#{am.uri},`limitPerDay`=#{am.limitPerDay},`limitPerHour`=#{am.limitPerHour},`promotionAddress`=#{am.promotionAddress} WHERE id=#{am.id};")
    void upt(@Param("am") ApiLimit am);

    @Select("SELECT * FROM api_limit;")
    List<ApiLimit> findAll();


}
