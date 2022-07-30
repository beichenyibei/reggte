package com.ithema.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithema.reggie.common.CustomException;
import com.ithema.reggie.entity.Category;
import com.ithema.reggie.entity.Dish;
import com.ithema.reggie.entity.Setmeal;
import com.ithema.reggie.mapper.CategoryMapper;
import com.ithema.reggie.service.CategoryService;
import com.ithema.reggie.service.DishService;
import com.ithema.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    //根据id删除分类，删除之前需要进行判断是否关联
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询当前分类是否关联菜品
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count > 0){
            //已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类项关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1 > 0){
            //已经关联套餐
            throw new CustomException("当前套餐项关联了菜品，不能删除");
        }

        super.removeById(id);
    }
}
