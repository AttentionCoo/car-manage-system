package com.carmanage.service;

import com.carmanage.common.PageResult;
import com.carmanage.entity.BeautyItem;
import java.util.List;

public interface BeautyItemService {
    PageResult<BeautyItem> getPage(Integer page, Integer pageSize, String keyword, Integer status);
    BeautyItem getById(Long id);
    void add(BeautyItem beautyItem);
    void update(BeautyItem beautyItem);
    void delete(Long id);
    void batchUpdateStatus(List<Long> ids, Integer status);
    List<BeautyItem> listAll();
}
