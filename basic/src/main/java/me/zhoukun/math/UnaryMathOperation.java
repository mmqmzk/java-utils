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
 * @date 2023/2/10 0:30
 **/
@Getter
@AllArgsConstructor
public enum UnaryMathOperation {
    Negate(
            aInt -> -aInt,
            aLong -> -aLong,
            aDouble -> -aDouble,
            BigInteger::negate,
            BigDecimal::negate
    ),
    LogN(
            aInt -> (int) Math.log(aInt),
            aLong -> (long) Math.log(aLong),
            Math::log,
            aBigInt ->
                    BigInteger.valueOf(
                            (long) Math.log(aBigInt.doubleValue())),
            aDecimal ->
                    BigDecimal.valueOf(Math.log(aDecimal.doubleValue()))
    ),
    Log2(
            aInt -> (int) (Math.log(aInt) / Math.log(2)),
            aLong -> (long) (Math.log(aLong) / Math.log(2)),
            aDouble -> Math.log(aDouble) / Math.log(2),
            aBigInt ->
                    BigInteger.valueOf(
                            (long) (Math.log(aBigInt.doubleValue())
                                    / Math.log(2))),
            aDecimal ->
                    BigDecimal.valueOf(
                            Math.log(aDecimal.doubleValue())
                                    / Math.log(2))
    ),
    Log10(
            aInt -> (int) (Math.log(aInt) / Math.log(10)),
            aLong -> (long) (Math.log(aLong) / Math.log(10)),
            aDouble -> Math.log(aDouble) / Math.log(10),
            aBigInt ->
                    BigInteger.valueOf(
                            (long) (Math.log(aBigInt.doubleValue())
                                    / Math.log(10))),
            aDecimal ->
                    BigDecimal.valueOf(
                            Math.log(aDecimal.doubleValue())
                                    / Math.log(10))
    ),
    Exp(
            aInt -> (int) Math.exp(aInt),
            aLong -> (long) Math.exp(aLong),
            Math::exp,
            aBigInt ->
                    BigInteger.valueOf(
                            (long) Math.exp(aBigInt.doubleValue())),
            aDecimal ->
                    BigDecimal.valueOf(
                            Math.exp(aDecimal.doubleValue()))
    ),
    ;

    private final UnaryOperatorHolder holder;

    UnaryMathOperation(IntUnaryOperator intOp,
                       LongUnaryOperator longOp,
                       DoubleUnaryOperator doubleOp,
                       UnaryOperator<BigInteger> bigIntOp,
                       UnaryOperator<BigDecimal> decimalOp) {
        this.holder
                = new UnaryOperatorHolder(intOp, longOp,
                doubleOp, bigIntOp, decimalOp);
    }
}
