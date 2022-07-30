package com.ithema.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithema.reggie.dto.DishDto;
import com.ithema.reggie.entity.Category;
import com.ithema.reggie.entity.Dish;
import com.ithema.reggie.entity.DishFlavor;
import com.ithema.reggie.mapper.CategoryMapper;
import com.ithema.reggie.mapper.DishMapper;
import com.ithema.reggie.service.CategoryService;
import com.ithema.reggie.service.DishFlavorService;
import com.ithema.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();

//        for (DishFlavor flavor : dishDto.getFlavors()) {
//            flavor.setDishId(dishId);
//        }

        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> collect = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());


        dishFlavorService.saveBatch(collect);
    }

    public DishDto getByIdWithFlayor(Long id){
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;

    }

    @Override
    public void updateWithFlayor(DishDto dishDto) {
        this.updateById(dishDto);

//        清理flavor数据
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }


}
