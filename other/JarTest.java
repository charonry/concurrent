package maven;


import maven.entity.HouseLoan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author charon
 * @Date 2020/7/7 17:03
 * @Version 1.0
 **/
public class JarTest {
    // 房贷的基本信息集合
    static final HashMap<String, HouseLoan> mortgageMap = new HashMap<String, HouseLoan>() {
        {
            put("fund20", new HouseLoan(new BigDecimal(20), new BigDecimal(1000000),
                    new BigDecimal(0.058)));
            put("fund30", new HouseLoan(new BigDecimal(30), new BigDecimal(1000000),
                    new BigDecimal(0.058)));
            put("20", new HouseLoan(new BigDecimal(20), new BigDecimal(700000),
                    new BigDecimal(0.058)));
            put("30", new HouseLoan(new BigDecimal(30), new BigDecimal(700000),
                    new BigDecimal(0.058)));
        }
    };

    // 基金收益率的模拟定投
    static final Map<Integer, Double> mouthProfitMap = new HashMap<Integer, Double>() {
        {
            for (int i = 1; i <= 12; i++) {
                put(i, new BigDecimal(0.9 + Math.random() * 0.2).setScale(2, RoundingMode.HALF_UP).doubleValue());
            }
        }
    };

    /**
     * @param initvalue 每个月的定投
     * @param value     每个月末的金额
     * @param num       循环次数
     * @param totalTime 定投月份+1
     * @return
     */
    public static double recurrence(double initvalue, double value, int num, int totalTime) {
        if (num == 0) {
            return value - 100;
        }
        return recurrence(initvalue, initvalue + value * mouthProfitMap.get(totalTime - num), num - 1, totalTime);
    }


    public static void main(String[] args) {
        // 获取房贷信息
        Map<String, BigDecimal> fundHouseLoanMap = getMortgageMap("fund30");
        Map<String, BigDecimal> houseLoanMap = getMortgageMap("30");
        // 还款年数
        BigDecimal fundYearDay = fundHouseLoanMap.get("monthDay").divide(new BigDecimal(12), 1, RoundingMode.HALF_UP);
        BigDecimal yearDay = houseLoanMap.get("monthDay").divide(new BigDecimal(12), 1, RoundingMode.HALF_UP);
        // 总贷款
        BigDecimal fundTotalPrice = fundHouseLoanMap.get("totalPrice");
        BigDecimal totalPrice = houseLoanMap.get("totalPrice");
        // 借贷金额
        BigDecimal fundCapital = fundHouseLoanMap.get("capital");
        BigDecimal capital = houseLoanMap.get("capital");
        // 月还款
        BigDecimal fundMonthInterest = fundHouseLoanMap.get("monthInterest");
        BigDecimal monthInterest = houseLoanMap.get("monthInterest");
        System.out.println("年份：" + fundYearDay + "\t" + "借贷金额：" + fundCapital + "\t" + "每月还款：" + fundMonthInterest + "\t" + "总还款：" + fundTotalPrice);
        System.out.println("年份：" + yearDay + "\t" + "借贷金额：" + capital + "\t" + "每月还款：" + monthInterest + "\t" + "总还款：" + totalPrice);

        // 基金投入额
        double totalFundPrice = fundCapital.subtract(capital).doubleValue();
        // 还款差额
        double subPrice = fundTotalPrice.subtract(totalPrice).doubleValue();
        // 基金年率
        BigDecimal fundRate = totalFund(totalFundPrice, yearDay.intValue(), subPrice);
        System.out.println("临界点的基金收益率：" + fundRate);
        System.out.println("反验证基金收益额：" + totalFund(new BigDecimal(totalFundPrice), yearDay.intValue(), fundRate));

        BigDecimal fundAllInRate = totalFund(fundCapital.doubleValue(), yearDay.intValue(), fundTotalPrice.doubleValue());
        System.out.println("借贷金额用于投资收益的临界点的基金收益率：" + fundAllInRate);
        System.out.println("反验证AllIn基金收益额：" + totalFund(fundCapital, yearDay.intValue(), fundAllInRate));

        BigDecimal yearlyTotalFundRete = getYearlyTotalFundRete(fundCapital, fundMonthInterest);
        System.out.println("每年提出收益用于还贷的收益率临界点:" + yearlyTotalFundRete);

    }


    /**
     * 获取房贷的数据
     *
     * @param key 年份方式
     * @return 房贷具体的数据
     */
    public static Map<String, BigDecimal> getMortgageMap(String key) {
        Map<String, BigDecimal> houseLoanPro = new HashMap();
        if (mortgageMap.containsKey(key)) {
            HouseLoan houseLoan = mortgageMap.get(key);
            // 月数
            BigDecimal monthDay = houseLoan.getMonthDay();
            // 本金
            BigDecimal capital = houseLoan.getCapital();
            // 月利率
            BigDecimal monthRate = houseLoan.getMonthRate();
            // (1+月利率)^月数
            Double midNum = Math.pow(1 + monthRate.doubleValue(), monthDay.doubleValue());
            // 月利息
            BigDecimal monthInterest = new BigDecimal(capital.doubleValue() * (monthRate.doubleValue() * midNum / (midNum - 1)));
            // 总金额
            BigDecimal totalPrice = monthInterest.multiply(monthDay);
            houseLoanPro.put("monthDay", monthDay);
            houseLoanPro.put("capital", capital);
            houseLoanPro.put("monthRate", monthRate);
            houseLoanPro.put("monthInterest", monthInterest.divide(new BigDecimal(1), 2, RoundingMode.HALF_UP));
            houseLoanPro.put("totalPrice", totalPrice.divide(new BigDecimal(1), 2, RoundingMode.HALF_UP));
        }
        return houseLoanPro;
    }

    /**
     * 获取基金的在房贷年限内取得的金额
     *
     * @param totalPrice 投入金额
     * @param amount     年限
     * @param fundRate   基金收益
     * @return totalPrice 基金收益总额（包含成本）
     */
    public static BigDecimal totalFund(BigDecimal totalPrice, int amount, BigDecimal fundRate) {
        if (amount == 0) {
            return totalPrice.divide(new BigDecimal(1), 2, RoundingMode.HALF_UP);
        }
        return totalFund(totalPrice.multiply(fundRate.add(new BigDecimal(1))), amount - 1, fundRate);
    }


    /**
     * 借贷和收益达到平衡基金收益率的临界点
     *
     * @param totalFundPrice 投入金钱
     * @param amount         月数
     * @param subPrice       差额
     * @return 基金利率
     */
    public static BigDecimal totalFund(Double totalFundPrice, int amount, Double subPrice) {
        double fundRate = Math.pow(subPrice / totalFundPrice, (double) 1 / amount) - 1;
        return new BigDecimal(fundRate).divide(new BigDecimal(1), 4, RoundingMode.HALF_UP);
    }

    /**
     * 每年提出收益用于还贷的收益临界点
     *
     * @param fundCapital       借贷金额
     * @param fundMonthInterest 每月还款额
     * @return 临界点收益率
     */
    public static BigDecimal getYearlyTotalFundRete(BigDecimal fundCapital, BigDecimal fundMonthInterest) {
        return fundMonthInterest.multiply(new BigDecimal(12)).divide(fundCapital, 4, RoundingMode.HALF_UP);
    }
}
