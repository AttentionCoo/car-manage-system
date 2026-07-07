package com.carmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carmanage.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
    IPage<Customer> selectPageWithCondition(Page<Customer> page,
                                            @Param("name") String name,
                                            @Param("phone") String phone,
                                            @Param("gender") String gender);
}
