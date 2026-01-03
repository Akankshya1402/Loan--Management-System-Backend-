package com.lms.loanprocessing.util;

import java.math.*;

public final class EmiCalculator {

    private EmiCalculator() {}

    public static BigDecimal calculate(
            BigDecimal principal,
            BigDecimal annualRate,
            int months) {

        BigDecimal r =
                annualRate.divide(
                        BigDecimal.valueOf(1200),
                        10,
                        RoundingMode.HALF_UP);

        BigDecimal numerator =
                principal.multiply(r)
                        .multiply((BigDecimal.ONE.add(r)).pow(months));

        BigDecimal denominator =
                (BigDecimal.ONE.add(r)).pow(months)
                        .subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
