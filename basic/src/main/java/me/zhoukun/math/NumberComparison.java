package me.zhoukun.math;

import lombok.Getter;
import me.zhoukun.util.Utils;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * @author 周锟
 * @date 2023/2/10 0:30
 **/
@Getter
public enum NumberComparison {
    Equal(
            Utils.constZero()
    ),
    Less(
            Utils.constZero()::lt
    ),
    LessOrEqual(
            Utils.constZero()::le
    ),
    Greater(
            Utils.constZero()::gt
    ),
    GreaterOrEqual(
            Utils.constZero()::ge
    ),
    ;
    private final Predicate<Integer> testResultBoxed;

    private final IntPredicate testResult;

    NumberComparison(IntPredicate testResult) {
        this.testResult = testResult;
        this.testResultBoxed =
                aInteger ->
                        aInteger != null
                                && testResult.test(aInteger);
    }
}
