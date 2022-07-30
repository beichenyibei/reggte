package com.ithema.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ithema.reggie.dto.SetmealDto;
import com.ithema.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> longs);
}
