package me.zhoukun.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

/**
 * @author 周锟
 * @date 2023/2/10 0:32
 **/
@Getter
@AllArgsConstructor
public class UnaryOperatorHolder {
    private final IntUnaryOperator intOp;

    private final LongUnaryOperator longOp;

    private final DoubleUnaryOperator doubleOp;

    private final UnaryOperator<BigInteger> bigIntOp;

    private final UnaryOperator<BigDecimal> decimalOp;
}
