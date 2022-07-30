package com.ithema.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithema.reggie.common.R;
import com.ithema.reggie.entity.Employee;
import com.ithema.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/login")
    public R<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee){

//        1.1	将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

//        1.2	根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper);

//        1.3	如果没有查询到则返回登录失败
        if(one == null)
            return R.error("登录失败");

//        1.4	密码比对，如果不一致则返回登录失败
        if(!one.getPassword().equals(password))
            return R.error("登录失败");

//        1.5	查看员工状态，如果为已禁用，则返回员工已禁用
        if(one.getStatus() == 0)
            return R.error("账号已禁用");

//        1.6	登录成功，将员工Id存入session并返回登录成功结果
        httpServletRequest.getSession().setAttribute("employee",one.getId());
        return R.success(one);
    }


    @RequestMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    // 新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工信息：{}", employee.toString());

        //设置初始密码123456，需要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        Long empID = (Long) request.getSession().getAttribute("employee");

//        employee.setCreateUser(empID);
//        employee.setUpdateUser(empID);

        employeeService.save(employee);

        return R.success("添加用户成功");
    }

    // 员工信息分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page();

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name), Employee::getName, name);

        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    //根据id修改状态
    @PutMapping
    public R<String> update(HttpServletRequest httpServletRequest, @RequestBody Employee employee){
        log.info(employee.toString());

//        Long employeeId = (Long) httpServletRequest.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(employeeId);

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息。。。");
        Employee employee = employeeService.getById(id);
        if(employee != null)
            return R.success(employee);
        return R.error("没有查询到员工");
    }
}
