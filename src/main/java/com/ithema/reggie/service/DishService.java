package com.ithema.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ithema.reggie.dto.DishDto;
import com.ithema.reggie.entity.Category;
import com.ithema.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlayor(Long id);
    public void updateWithFlayor(DishDto dishDto);
}
