package me.zhoukun.math;

import lombok.NonNull;
import me.zhoukun.util.Functions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

public enum BinaryMathOperation {
    First(
            Functions.firstInt(),
            Functions.firstLong(),
            Functions.firstDouble(),
            Functions.bFirstArg(),
            Functions.bFirstArg()
    ),

    Second(
            Functions.secondInt(),
            Functions.secondLong(),
            Functions.secondDouble(),
            Functions.bSecondArg(),
            Functions.bSecondArg()
    ),

    Addition(
            Integer::sum,
            Long::sum,
            Double::sum,
            BigInteger::add,
            BigDecimal::add
    ),
    Subtraction(
            (minuend, aInt) -> minuend - aInt,
            (minuend, aLong) -> minuend - aLong,
            (minuend, aDouble) -> minuend - aDouble,
            BigInteger::subtract,
            BigDecimal::subtract
    ),

    Multiplication(
            (multiplicand, aInt) -> multiplicand * aInt,
            (multiplicand, aLong) -> multiplicand * aLong,
            (multiplicand, aDouble) -> multiplicand * aDouble,
            BigInteger::multiply,
            BigDecimal::multiply
    ),
    Division(
            (dividend, aInt) -> dividend / aInt,
            (dividend, aLong) -> dividend / aLong,
            (dividend, aDouble) -> dividend / aDouble,
            BigInteger::divide,
            BigDecimal::divide
    ),

    Modulo(
            (dividend, aInt) -> dividend % aInt,
            (dividend, aLong) -> dividend % aLong,
            (dividend, aDouble) -> dividend % aDouble,
            BigInteger::mod,
            BigDecimal::remainder
    ),

    Power(
            (base, aInt) -> (int) Math.pow(base, aInt),
            (base, aLong) -> (long) Math.pow(base, aLong),
            Math::pow,
            (base, aBigInt) -> base.pow(aBigInt.intValue()),
            (base, aDecimal) -> base.pow(aDecimal.intValue())
    ),
    Root(
            (base, aInt) -> (int) Math.pow(aInt, 1.0 / base),
            (base, aLong) -> (long) Math.pow(aLong, 1.0 / base),
            (base, aDouble) -> Math.pow(aDouble, 1.0 / base),
            (base, aBigInt) ->
                    BigInteger.valueOf(
                            (long) Math.pow(aBigInt.doubleValue(),
                                    1.0 / base.doubleValue())),
            (base, aDecimal) ->
                    BigDecimal.valueOf(
                            Math.pow(aDecimal.doubleValue(),
                                    1.0 / base.doubleValue()))
    ),
    Log(
            (base, aInt) -> (int) (Math.log(aInt) / Math.log(base)),
            (base, aLong) -> (long) (Math.log(aLong) / Math.log(base)),
            (base, aDouble) -> Math.log(aDouble) / Math.log(base),
            (base, aBigInt) ->
                    BigInteger.valueOf(
                            (long) (Math.log(aBigInt.doubleValue())
                                    / Math.log(base.doubleValue()))),
            (base, aDecimal) ->
                    BigDecimal.valueOf(
                            Math.log(aDecimal.doubleValue())
                                    / Math.log(base.doubleValue()))
    ),

    FromMath() {

        private final
        Map<Integer, BinaryOperatorHolder>
                holderFromDoubleMath = new HashMap<>();

        private final
        Map<Integer, BinaryOperatorHolder>
                holderFromBoxed = new HashMap<>();

        public BinaryOperatorHolder getHolder() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends Number, V extends Number, R extends Number>
        BinaryOperatorHolder holderFromJavaMath(
                @NonNull BiFunction<T, V, R> operator,
                @NonNull Class<T> tClass,
                @NonNull Class<V> vClass,
                @NonNull Class<R> rClass) {
            return super.holderFromJavaMath(operator, tClass, vClass, rClass);
        }
    },
    ;

    protected final BinaryOperatorHolder holder;

    BinaryMathOperation() {
        holder = null;
    }

    BinaryMathOperation(IntBinaryOperator intOp,
                        LongBinaryOperator longOp,
                        DoubleBinaryOperator doubleOp,
                        BinaryOperator<BigInteger> bigIntOp,
                        BinaryOperator<BigDecimal> decimalOp) {
        holder = new BinaryOperatorHolder(intOp,
                longOp, doubleOp,
                bigIntOp, decimalOp);
    }

    public BinaryOperatorHolder getHolder() {
        return holder;
    }

    protected Function<Number, Number> determineNumberType(
            @NonNull Number first,
            @NonNull Number second) {
        if (isFloatPoint(first) || isFloatPoint(second)) {
            return Number::doubleValue;
        }
        return Number::longValue;
    }


    protected boolean isFloatPoint(Number number) {
        return number instanceof Float
                || number instanceof Double
                || number instanceof BigDecimal;
    }

    public <T extends Number, V extends Number, R extends Number>
    BinaryOperatorHolder holderFromJavaMath(
            @NonNull
            BiFunction<T, V, R> operator,
            @NonNull Class<T> tClass,
            @NonNull Class<V> vClass,
            @NonNull Class<R> rClass) {
        throw new UnsupportedOperationException();
    }

    public BinaryOperatorHolder holderFromJavaMath(
            @NonNull
            DoubleBinaryOperator operator) {
        return holderFromJavaMath(
                operator::applyAsDouble,
                Double.class,
                Double.class,
                Double.class);
    }

}
