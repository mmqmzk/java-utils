package me.zhoukun.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;

@Getter
@AllArgsConstructor
public final class BinaryOperatorHolder {
    private final IntBinaryOperator intOp;

    private final LongBinaryOperator longOp;

    private final DoubleBinaryOperator doubleOp;

    private final BinaryOperator<BigInteger> bigIntOp;

    private final BinaryOperator<BigDecimal> decimalOp;
}
