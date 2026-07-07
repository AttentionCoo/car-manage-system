package com.carmanage.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carmanage.common.PageResult;
import com.carmanage.entity.BeautyItem;
import com.carmanage.mapper.BeautyItemMapper;
import com.carmanage.service.BeautyItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BeautyItemServiceImpl implements BeautyItemService {

    @Autowired
    private BeautyItemMapper beautyItemMapper;

    @Override
    public PageResult<BeautyItem> getPage(Integer page, Integer pageSize, String keyword, Integer status) {
        Page<BeautyItem> pageParam = new Page<>(page, pageSize);
        IPage<BeautyItem> result = beautyItemMapper.selectPageWithCondition(pageParam, keyword, status);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, pageSize);
    }

    @Override
    public BeautyItem getById(Long id) {
        return beautyItemMapper.selectById(id);
    }

    @Override
    public void add(BeautyItem beautyItem) {
        beautyItemMapper.insert(beautyItem);
    }

    @Override
    public void update(BeautyItem beautyItem) {
        beautyItemMapper.updateById(beautyItem);
    }

    @Override
    public void delete(Long id) {
        beautyItemMapper.deleteById(id);
    }

    @Override
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        for (Long id : ids) {
            BeautyItem item = new BeautyItem();
            item.setId(id);
            item.setStatus(status);
            beautyItemMapper.updateById(item);
        }
    }

    @Override
    public List<BeautyItem> listAll() {
        return beautyItemMapper.selectList(null);
    }
}
