package com.synctech.statter.base.mapper;


import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface BlockCrawlerMapper {

    @Insert("INSERT INTO block_crawler (`blockIndex`) VALUES (#{bi});")
    void add(@Param("bi") long bi);

    /**
     * @return
     */
    @Delete("DELETE FROM block_crawler WHERE blockIndex<#{bi};")
    void deleteBlow(@Param("bi") long bi);

    /**
     * @return
     */
    @Select("SELECT max(blockIndex) FROM block_crawler;")
    Long max();

}
