package com.statter.statter.base.mapper;


import com.statter.statter.base.entity.CpuModel;
import com.statter.statter.base.entity.Miner;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CpuModuleMapper {

    @Insert("INSERT INTO cpu_model (`cpuModuleName`,`standardHash`) VALUES (#{cm.cpuModuleName},#{cm.standardHash});")
    void add(@Param("cm") CpuModel cm);

    @Select("SELECT * FROM cpu_model;")
    List<CpuModel> findAll();


}
