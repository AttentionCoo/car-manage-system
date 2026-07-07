package com.carmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carmanage.entity.BeautyItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BeautyItemMapper extends BaseMapper<BeautyItem> {
    IPage<BeautyItem> selectPageWithCondition(Page<BeautyItem> page,
                                              @Param("keyword") String keyword,
                                              @Param("status") Integer status);
}
