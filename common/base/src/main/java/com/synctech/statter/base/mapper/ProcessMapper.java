package com.statter.statter.base.mapper;


import com.statter.statter.base.entity.Process;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProcessMapper {

    @Insert("INSERT INTO process (`createdTime`, `type`, `stage`, `address`, `sn`, `tradeNo`, `amount`) " +
            "VALUES (now(), #{p.type}, #{p.stage}, #{p.address}, #{p.sn}, #{p.tradeNo}, #{p.amount});")
    @SelectKey(statement = "select LAST_INSERT_ID()", keyProperty = "p.id", before = false, resultType = Long.class)
    void add(@Param("p") Process p);

    @Update("UPDATE process SET `status`=0 WHERE id=#{id};")
    void delete(@Param("id") String id);

    @Update("UPDATE process SET `stage`=#{s} WHERE `id`=#{id};")
    void updateStage(@Param("id") long id, @Param("s") long stage);

    @Update("UPDATE process SET`type`=#{p.type} `stage`=#{p.stage} WHERE `id`=#{p.id};")
    void updateTypeAndStage(@Param("p") Process p);

    @Select("SELECT * FROM process WHERE `STATUS`=1;")
    List<Process> findAll();

    @Select("SELECT * FROM process WHERE `id`=#{id};")
    Process findOne(@Param("id") long id);

    @Select("SELECT `stage` FROM process WHERE `id`=#{id};")
    byte getStage(@Param("id") long id);

}
