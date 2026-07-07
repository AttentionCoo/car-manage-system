package com.carmanage.controller;

import com.carmanage.common.PageResult;
import com.carmanage.common.Result;
import com.carmanage.entity.BeautyItem;
import com.carmanage.service.BeautyItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beauty-items")
public class BeautyItemController {

    @Autowired
    private BeautyItemService beautyItemService;

    @GetMapping
    public Result<PageResult<BeautyItem>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(beautyItemService.getPage(page, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<BeautyItem> getById(@PathVariable Long id) {
        return Result.success(beautyItemService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody BeautyItem beautyItem) {
        beautyItemService.add(beautyItem);
        return Result.success("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody BeautyItem beautyItem) {
        beautyItem.setId(id);
        beautyItemService.update(beautyItem);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        beautyItemService.delete(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/status/batch")
    public Result<Void> batchUpdateStatus(@RequestBody java.util.Map<String, Object> params) {
        List<Long> ids = (List<Long>) params.get("ids");
        Integer status = (Integer) params.get("status");
        beautyItemService.batchUpdateStatus(ids, status);
        return Result.success("操作成功", null);
    }

    @GetMapping("/all")
    public Result<List<BeautyItem>> listAll() {
        return Result.success(beautyItemService.listAll());
    }
}
