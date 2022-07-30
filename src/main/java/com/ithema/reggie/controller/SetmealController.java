package com.ithema.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithema.reggie.common.R;
import com.ithema.reggie.dto.SetmealDto;
import com.ithema.reggie.entity.Category;
import com.ithema.reggie.entity.Setmeal;
import com.ithema.reggie.service.CategoryService;
import com.ithema.reggie.service.SetmealDishService;
import com.ithema.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();



        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name!=null, Setmeal::getName, name);

        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(setmealPage, setmealLambdaQueryWrapper);

        BeanUtils.copyProperties(setmealPage, setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;

        }).collect(Collectors.toList());


        setmealDtoPage.setRecords(collect);
        return R.success(setmealDtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId()!=null, Setmeal::getCategoryId, setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(setmeal.getStatus()!=null, Setmeal::getStatus, setmeal.getStatus());
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);

        return R.success(list);
    }
}
