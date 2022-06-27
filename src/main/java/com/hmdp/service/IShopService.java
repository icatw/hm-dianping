package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IShopService extends IService<Shop> {

    /**
     * 通过id查询商店
     *
     * @param id id
     * @return {@link Result}
     */
    Result queryShopById(Long id);

    /**
     * 更新
     *
     * @param shop 商店
     * @return {@link Result}
     */
    Result update(Shop shop);
}
