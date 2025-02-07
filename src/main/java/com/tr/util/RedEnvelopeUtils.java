package com.tr.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedEnvelopeUtils {

    public static List<BigDecimal> allocateRedEnvelopes(BigDecimal totalAmount, int totalPeople) {
        // 结果列表
        List<BigDecimal> allocations = new ArrayList<>();
        Random random = new Random();

        // 剩余金额和人数
        BigDecimal remainingAmount = totalAmount;
        int remainingPeople = totalPeople;

        for (int i = 0; i < totalPeople; i++) {
            // 如果是最后一个人，直接分配剩余金额
            if (remainingPeople == 1) {
                allocations.add(remainingAmount.setScale(2, RoundingMode.DOWN));
                break;
            }

            // 最小金额
            BigDecimal minAmount = new BigDecimal("0.01");

            // 最大金额动态调整，不能超过剩余平均值的两倍
            BigDecimal maxAmount = remainingAmount.divide(new BigDecimal(remainingPeople), 2, RoundingMode.DOWN).multiply(new BigDecimal("2"));

            // 随机生成金额，范围 [minAmount, maxAmount]
            BigDecimal randomAmount = minAmount.add(new BigDecimal(random.nextDouble()).multiply(maxAmount.subtract(minAmount)));

            // 保留两位小数
            randomAmount = randomAmount.setScale(2, RoundingMode.DOWN);

            // 确保随机金额不超过剩余金额
            if (randomAmount.compareTo(remainingAmount) > 0) {
                randomAmount = remainingAmount;
            }

            // 添加到分配结果
            allocations.add(randomAmount);

            // 更新剩余金额和人数
            remainingAmount = remainingAmount.subtract(randomAmount);
            remainingPeople--;
        }

        return allocations;
    }

}
