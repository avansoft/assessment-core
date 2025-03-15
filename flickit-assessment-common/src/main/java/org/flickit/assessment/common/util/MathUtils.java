package org.flickit.assessment.common.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MathUtils {

    public static double round(double value, int scale) {
        return BigDecimal.valueOf(value)
            .setScale(scale, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
