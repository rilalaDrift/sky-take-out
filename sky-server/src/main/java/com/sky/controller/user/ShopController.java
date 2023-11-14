package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

//bean 默认值是名字的小写 会报错
@RestController("userShopController")
@Slf4j
@Api(tags = "店铺相关接口")
@RequestMapping("/user/shop")

public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    public static final String KEY="SHOP_STATUS";


    @GetMapping("/status")
    @ApiOperation("获得店铺营业状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获得店铺营业状态为：{}",status==1?"营业中":"打烊中");
        return Result.success(status);
    }
}
