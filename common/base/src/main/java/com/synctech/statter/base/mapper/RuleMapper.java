package com.statter.statter.base.mapper;


import com.statter.statter.base.entity.Rule;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RuleMapper {

    @Insert("INSERT INTO rule (`createdTime`, `state`, `type`, `content`) VALUES (now(), 1, #{r.type}, #{r.content});")
    @SelectKey(statement = "select LAST_INSERT_ID()", keyProperty = "r.id", before = false, resultType = Long.class)
    void add(@Param("r") Rule r);

    @Update("UPDATE rule SET `state`=0 WHERE `id`=#{id};")
    void delete(@Param("id") String id);

    @Update("UPDATE rule SET `state`=0 WHERE `type`=#{t};")
    void deleteByType(@Param("t") byte t);

    @Select("SELECT * FROM rule WHERE id=#{id};")
    Rule findOne(@Param("id") String id);

    @Select("SELECT * FROM rule WHERE `type`=#{t} AND `state`=1;")
    Rule findByType(@Param("t") byte t);

    @Select("SELECT * FROM rule WHERE `state`=1 ORDER by `type`;")
    List<Rule> findAll();

}
