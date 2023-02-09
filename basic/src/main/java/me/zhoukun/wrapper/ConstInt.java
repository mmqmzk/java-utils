package me.zhoukun.wrapper;

import lombok.*;

import java.util.OptionalInt;
import java.util.function.*;
import java.util.stream.IntStream;

/**
 * @author 周锟
 * @date 2023/2/9 20:55
 **/
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConstInt implements Comparable<ConstInt>,
        IntSupplier, IntConsumer, IntPredicate, IntUnaryOperator {
    protected static final int STEP = 1;
    public static final ConstInt CONST_ONE = of(STEP);

    protected static final int ZERO = 0;
    public static final ConstInt CONST_ZERO = of(ZERO);
    protected final int constValue;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected final boolean constant;

    public static ConstInt of(int constValue) {
        return new ConstInt(constValue, true);
    }

    public static ConstInt max(@NonNull ConstInt left,
                               @NonNull ConstInt right) {
        return left.compareTo(right) >= 0 ? left : right;
    }

    public static ConstInt min(@NonNull ConstInt left,
                               @NonNull ConstInt right) {
        return left.compareTo(right) <= 0 ? left : right;
    }

    public int getValue() {
        return constValue;
    }

    public int get() {
        return getValue();
    }

    @Override
    public void accept(int value) {
    }

    @Override
    public boolean test(int value) {
        return value == getValue();
    }

    @Override
    public int getAsInt() {
        return getValue();
    }

    @Override
    public int applyAsInt(int operand) {
        return getValue();
    }

    public boolean eq(int aInt) {
        return getValue() == aInt;
    }

    public boolean eq0() {
        return eq(ZERO);
    }

    public boolean gt(int aInt) {
        return getValue() > aInt;
    }

    public boolean ge(int aInt) {
        return getValue() >= aInt;
    }

    public boolean lt(int aInt) {
        return getValue() < aInt;
    }

    public boolean le(int aInt) {
        return getValue() <= aInt;
    }

    public boolean gt0() {
        return gt(ZERO);
    }

    public boolean ge0() {
        return ge(ZERO);
    }

    public boolean lt0() {
        return lt(ZERO);
    }

    public boolean le0() {
        return le(ZERO);
    }

    public IntStream asStream() {
        return IntStream.of(getValue());
    }

    public ConstInt map(IntUnaryOperator mapper) {
        return of(mapper.applyAsInt(getValue()));
    }

    public <T> Holder<T> mapToObj(@NonNull
                                  IntFunction<? extends T> mapper) {
        return Holder.of(mapper.apply(getValue()));
    }

    public OptionalInt asOptional() {
        return OptionalInt.of(getValue());
    }

    @Override
    public int compareTo(ConstInt o) {
        return Integer.compare(getValue(), o.getValue());
    }
}
