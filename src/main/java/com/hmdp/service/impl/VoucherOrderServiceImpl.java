package com.hmdp.service.impl;

import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private RedisIdWorker redisIdWorker;

    @Override
    @Transactional
    public Result seckillVoucher(Long voucherId) {
        //1.查询秒杀优惠券信息
        SeckillVoucher seckillVoucher = seckillVoucherService.getById(voucherId);
        //2.判断秒杀是否开始
        LocalDateTime beginTime = seckillVoucher.getBeginTime();
        LocalDateTime endTime = seckillVoucher.getEndTime();
        if (LocalDateTime.now().isBefore(beginTime)) {
            //尚未开始
            return Result.fail("秒杀尚未开始！");
        }
        //3.判断秒杀是否结束
        if (LocalDateTime.now().isAfter(endTime)) {
            //已经结束
            return Result.fail("秒杀已经结束！");
        }
        //4.判断库存是否充足
        if (seckillVoucher.getStock() < 1) {
            //    库存不足
            return Result.fail("库存不足！");
        }
        //5.扣减库存
        //乐观锁，判断库存
        boolean success = seckillVoucherService.update()
                .setSql("stock=stock-1").eq("voucher_id", voucherId)
                .gt("stock", 0).update();
        if (!success) {
            //    扣减失败
            return Result.fail("库存不足！");
        }
        //6.创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        voucherOrder.setVoucherId(voucherId);
        //用户id
        Long userId = UserHolder.getUser().getId();
        voucherOrder.setUserId(userId);
        this.save(voucherOrder);
        //7.返回订单id
        return Result.ok(orderId);
    }
}