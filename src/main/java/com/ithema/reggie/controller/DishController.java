package com.ithema.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithema.reggie.common.R;
import com.ithema.reggie.dto.DishDto;
import com.ithema.reggie.entity.Category;
import com.ithema.reggie.entity.Dish;
import com.ithema.reggie.entity.DishFlavor;
import com.ithema.reggie.service.CategoryService;
import com.ithema.reggie.service.DishFlavorService;
import com.ithema.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    public R<String>  save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        String keys = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> pageR(int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);

        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,dishLambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"recoders");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();

            Category byId = categoryService.getById(categoryId);
            String categoryName = byId.getName();
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlayor(id);

        return R.success(dishDto);
    }

    @PutMapping
    public R<String>  update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlayor(dishDto);

        String keys = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);
        return R.success("更新菜品成功");
    }


//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
//
//        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//
//        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
//
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;
        // 从redis中获取缓存数据
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果存在，直接返回，无需查询数据库
        if(dishDtoList != null)
            return R.success(dishDtoList);
        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到redis


        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);

        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);


        List<Dish> list = dishService.list(dishLambdaQueryWrapper);

        dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);


        return R.success(dishDtoList);
    }

}
