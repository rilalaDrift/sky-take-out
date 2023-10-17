package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //插入菜品一条
        dishMapper.insert(dish);

        //前端无法传入菜品id，这里菜品id已经创建
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors!=null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dishId);});
            //插入口味多条
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(),pageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(pageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断能不能删
        for (Long id : ids) {
           Dish dish = dishMapper.getById(id);
           if (dish.getStatus() == StatusConstant.ENABLE){
               throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
           }
        }
        //判断能不能删 是否关联套餐

        List<Long> setmealIds =  setmealDishMapper.getSetmealByDishIds(ids);
        if (setmealIds !=null &&setmealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品的口味
//            dishFlavorMapper.deleteById(id);
//        }

        //批量删除
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByIds(ids);
    }

    /**
     * 根据id查找
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dishVO = new DishVO();

        Dish dish = dishMapper.getById(id);
        List<DishFlavor> dishFlavors= dishFlavorMapper.getByDishId(id);
        //
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.update(dish);
        //删除原有口味再插入
        dishFlavorMapper.deleteById(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors!=null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dishDTO.getId());});
            //插入口味多条
            dishFlavorMapper.insertBatch(flavors);
        }


    }


}
