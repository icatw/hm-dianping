package com.hmdp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    RedisTemplate<String, ShopType> redisTemplate;

    @Override
    public Result listByAsc() {
        List<ShopType> shopType = redisTemplate.opsForList().range(CACHE_SHOP_TYPE_KEY, 0, -1);
        if (shopType!=null&&!shopType.isEmpty()) {
            log.info("查询了redis");
            return Result.ok(shopType);
        }
        List<ShopType> shopTypeList = this.list(new QueryWrapper<ShopType>().orderByAsc("sort"));
        log.info("查询了数据库");
        if (shopTypeList==null){
            return Result.fail("服务器异常！");
        }
        redisTemplate.opsForList().leftPushAll(CACHE_SHOP_TYPE_KEY,shopTypeList);

        return Result.ok(shopTypeList);
    }
}
