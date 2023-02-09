package me.zhoukun.wrapper;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

/**
 * @author 周锟
 * @date 2023/2/9 20:55
 **/
@Getter
@Setter
public final class Counter extends ConstInt
        implements IntSupplier, IntConsumer, IntUnaryOperator, IntPredicate {
    private int value;

    Counter() {
        this(ZERO);
    }

    Counter(int value) {
        super(value, false);
        this.value = value;
    }

    public static Counter of(int value) {
        return new Counter(value);
    }

    public static Counter of() {
        return of(ZERO);
    }


    public Counter set(int value) {
        this.value = value;
        return this;
    }

    public int getAndInc() {
        return getAndInc(STEP);
    }

    public int getAndInc(int aInt) {
        var count = this.value;
        this.value += aInt;
        return count;
    }

    public int getAndDec() {
        return getAndDec(STEP);
    }

    public int getAndDec(int aInt) {
        var count = this.value;
        this.value -= aInt;
        return count;
    }

    public int incAndGet() {
        return incAndGet(STEP);
    }

    public int incAndGet(int aInt) {
        return value += aInt;
    }

    public int decAndGet() {
        return decAndGet(STEP);
    }

    public int decAndGet(int aInt) {
        return value -= aInt;
    }

    public Counter inc() {
        return inc(STEP);
    }

    public Counter inc(int step) {
        value += step;
        return this;
    }

    public Counter dec() {
        return dec(STEP);
    }

    public Counter dec(int step) {
        value -= step;
        return this;
    }

    @Override
    public void accept(int value) {
        set(value);
    }

    @Override
    public int getAsInt() {
        return value;
    }

    @Override
    public int applyAsInt(int operand) {
        return incAndGet(operand);
    }

    public IntStream rangeFromInit() {
        return IntStream.rangeClosed(getConstValue(), value);
    }

    @Override
    public Counter map(@NonNull IntUnaryOperator mapper) {
        return of(mapper.applyAsInt(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Counter)) return false;
        if (!super.equals(o)) return false;
        return value == ((Counter) o).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public String toString() {
        return "Counter{" +
                "value=" + value +
                '}';
    }
}
