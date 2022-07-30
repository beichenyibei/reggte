package com.ithema.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithema.reggie.common.CustomException;
import com.ithema.reggie.dto.SetmealDto;
import com.ithema.reggie.entity.Dish;
import com.ithema.reggie.entity.Setmeal;
import com.ithema.reggie.entity.SetmealDish;
import com.ithema.reggie.mapper.DishMapper;
import com.ithema.reggie.mapper.SetmealMapper;
import com.ithema.reggie.service.DishService;
import com.ithema.reggie.service.SetmealDishService;
import com.ithema.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(setmealLambdaQueryWrapper);
        if(count > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(setmealDishLambdaQueryWrapper);

    }
}
