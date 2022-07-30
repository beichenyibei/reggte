package com.ithema.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithema.reggie.entity.Setmeal;
import com.ithema.reggie.entity.SetmealDish;
import com.ithema.reggie.mapper.SetmealDishMapper;
import com.ithema.reggie.mapper.SetmealMapper;
import com.ithema.reggie.service.SetmealDishService;
import com.ithema.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
