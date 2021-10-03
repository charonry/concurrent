package maven.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Description 房贷
 * @Author charon
 * @Date 2021/2/2 16:21
 * @Version 1.0
 **/
public class HouseLoan {

    // 还款月数
    BigDecimal monthDay;
    // 借贷金额
    BigDecimal capital;
    // 月利率
    BigDecimal monthRate;

    public HouseLoan(BigDecimal yearDay, BigDecimal capital, BigDecimal yearRate) {
        this.monthDay = yearDay.multiply(new BigDecimal(12));
        this.capital = capital;
        this.monthRate = yearRate.divide(new BigDecimal(12), 9, RoundingMode.HALF_UP);
    }

    public BigDecimal getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(BigDecimal yearDay) {
        this.monthDay = yearDay.multiply(new BigDecimal(12));
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public BigDecimal getMonthRate() {
        return monthRate;
    }

    public void setMonthRate(BigDecimal yearRate) {
        this.monthRate = yearRate.divide(new BigDecimal(12), 8, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "HouseLoan{" +
                "monthDay=" + monthDay +
                ", capital=" + capital +
                ", monthRate=" + monthRate +
                '}';
    }
}
