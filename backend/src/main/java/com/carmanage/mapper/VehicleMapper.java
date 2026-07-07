package com.carmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carmanage.entity.Vehicle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {
    IPage<Vehicle> selectPageWithCondition(Page<Vehicle> page,
                                           @Param("customerId") Long customerId,
                                           @Param("plateNumber") String plateNumber,
                                           @Param("brand") String brand);
    List<Vehicle> selectByCustomerId(@Param("customerId") Long customerId);
}
