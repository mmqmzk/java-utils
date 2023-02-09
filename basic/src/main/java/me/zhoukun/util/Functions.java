package me.zhoukun.util;

import com.google.common.primitives.Floats;
import lombok.NonNull;
import me.zhoukun.math.BinaryMathOperation;
import me.zhoukun.wrapper.Holder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.*;


/**
 * Created by 周锟 on 2016/1/29 9:53.
 */
public enum Functions {
    ;

    public static final int INDEX_NOT_FOUND = -1;

    public static <T> Consumer<T> empty() {
        return ignored -> {
        };
    }

    public static <T, V> BiConsumer<T, V> bEmpty() {
        return (ignoredFirst, ignoredSecond) -> {
        };
    }

    public static <T, V, R> Function<T, R> join(@NonNull
                                                Function<T, V> before,
                                                @NonNull
                                                Function<V, R> after) {
        return before.andThen(after);
    }

    public static <T, U, V, R> Function<T, R> join(@NonNull
                                                   Function<T, U> func1,
                                                   @NonNull
                                                   Function<U, V> func2,
                                                   @NonNull
                                                   Function<V, R> func3) {
        return func1.andThen(func2).andThen(func3);
    }

    public static <T, U, V, W, R> Function<T, R> join(@NonNull
                                                      Function<T, U> func1,
                                                      @NonNull
                                                      Function<U, V> func2,
                                                      @NonNull
                                                      Function<V, W> func3,
                                                      @NonNull
                                                      Function<W, R> func4) {
        return func1.andThen(func2).andThen(func3).andThen(func4);
    }

    public static <T, V> Predicate<T> joinP(@NonNull
                                            Function<T, V> func,
                                            @NonNull
                                            Predicate<V> predicate) {
        return argument -> predicate.test(func.apply(argument));
    }

    public static <T, U, V> Predicate<T> joinP(@NonNull
                                               Function<T, U> func1,
                                               @NonNull
                                               Function<U, V> func2,
                                               @NonNull
                                               Predicate<V> predicate) {
        return argument ->
                predicate.test(func2.apply(func1.apply(argument)));
    }

    public static <T, U, V, W> Predicate<T> joinP(@NonNull
                                                  Function<T, U> func1,
                                                  @NonNull
                                                  Function<U, V> func2,
                                                  @NonNull
                                                  Function<V, W> func3,
                                                  @NonNull
                                                  Predicate<W> predicate) {
        return argument ->
                predicate.test(func3.apply(func2.apply(func1.apply(argument))));
    }

    public static <T, U, V, W, X> Predicate<T> joinP(@NonNull
                                                     Function<T, U> func1,
                                                     @NonNull
                                                     Function<U, V> func2,
                                                     @NonNull
                                                     Function<V, W> func3,
                                                     @NonNull
                                                     Function<W, X> func4,
                                                     @NonNull
                                                     Predicate<X> predicate) {
        return argument ->
                predicate.test(
                        func4.apply(
                                func3.apply(
                                        func2.apply(
                                                func1.apply(argument)))));
    }

    public static <T, R> Consumer<T> joinC(@NonNull Function<T, R> func,
                                           @NonNull Consumer<R> consumer) {
        return argumet ->
                consumer.accept(func.apply(argumet));
    }

    public static <T, U, V> Consumer<T> joinC(@NonNull
                                              Function<T, U> func1,
                                              @NonNull
                                              Function<U, V> func2,
                                              @NonNull
                                              Consumer<V> consumer) {
        return argument ->
                consumer.accept(func2.apply(func1.apply(argument)));
    }

    public static <T, U, V, W> Consumer<T> joinC(@NonNull
                                                 Function<T, U> func1,
                                                 @NonNull
                                                 Function<U, V> func2,
                                                 @NonNull
                                                 Function<V, W> func3,
                                                 @NonNull
                                                 Consumer<W> consumer) {
        return argument ->
                consumer.accept(func3.apply(func2.apply(func1.apply(argument))));
    }

    @SafeVarargs
    public static <T> Consumer<T> joinCC(@NonNull Consumer<T>... consumers) {
        return Arrays.stream(consumers)
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen)
                .orElseGet(Functions::empty);
    }

    public static <T> Consumer<T> joinCC(@Nullable
                                         Collection<Consumer<T>> consumers) {
        if (consumers == null || consumers.isEmpty()) {
            return empty();
        }
        return consumers.stream()
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen)
                .orElseGet(Functions::empty);
    }

    @SafeVarargs
    public static <T, R> Function<T, R> joinCS(@NonNull
                                               Supplier<R> supplier,
                                               @NonNull
                                               Consumer<T>... consumers) {
        return argument -> {
            joinCC(consumers).accept(argument);
            return supplier.get();
        };
    }

    public static <T, R> Function<T, R> joinCS(@NonNull
                                               Supplier<R> supplier,
                                               @NonNull
                                               Collection<Consumer<T>>
                                                       consumers) {
        return argument -> {
            joinCC(consumers).accept(argument);
            return supplier.get();
        };
    }


    @SafeVarargs
    public static <T> UnaryOperator<T> c2f(@NonNull
                                           Consumer<T>... consumers) {
        return consumersToFunction(consumers);
    }

    @SafeVarargs
    public static <T> UnaryOperator<T> consumersToFunction(
            @NonNull Consumer<T>... consumers) {
        return argument -> {
            joinCC(consumers).accept(argument);
            return argument;
        };
    }

    public static <T> UnaryOperator<T> c2f(
            @Nullable Collection<Consumer<T>> consumers) {
        return consumersToFunction(consumers);
    }

    public static <T> UnaryOperator<T> consumersToFunction(
            @Nullable Collection<Consumer<T>> consumers) {
        return argument -> {
            joinCC(consumers).accept(argument);
            return argument;
        };
    }

    @SafeVarargs
    public static <T, R> Function<T, R> joinCF(@NonNull
                                               Function<T, R> func,
                                               @NonNull
                                               Consumer<T>... consumers) {
        return argument -> {
            joinCC(consumers).accept(argument);
            return func.apply(argument);
        };
    }

    public static <T, R> Function<T, R> joinCF(@NonNull
                                               Function<T, R> func,
                                               @NonNull
                                               Collection<Consumer<T>>
                                                       consumers) {
        return argument -> {
            joinCC(consumers).accept(argument);
            return func.apply(argument);
        };
    }

    public static <T> Function<T, Boolean> p2F(@NonNull
                                               Predicate<T> predicate) {
        return predicateToFunction(predicate);
    }

    public static <T> Function<T, Boolean> predicateToFunction(
            @NonNull Predicate<T> predicate) {
        return predicate::test;
    }

    public static <T> Predicate<T> f2P(@NonNull Function<T, Boolean> func) {
        return functionToPredicate(func);
    }


    public static <T> Predicate<T> functionToPredicate(
            @NonNull Function<T, Boolean> func) {
        return argument ->
                Optional.ofNullable(func.apply(argument)).orElse(Boolean.FALSE);
    }

    public static <T> Consumer<T> ifThen(@NonNull Predicate<T> predicate,
                                         @NonNull Consumer<T> thenFunc) {
        return argument -> {
            if (predicate.test(argument)) {
                thenFunc.accept(argument);
            }
        };
    }

    public static <T> Consumer<T> ifThenElse(@NonNull Predicate<T> predicate,
                                             @NonNull Consumer<T> thenFunc,
                                             @NonNull Consumer<T> elseFunc) {
        return argument -> {
            if (predicate.test(argument)) {
                thenFunc.accept(argument);
            } else {
                elseFunc.accept(argument);
            }
        };
    }

    public static <T> Supplier<T> constant(T value) {
        return () -> value;
    }

    public static Supplier<Integer> constantZero() {
        return () -> 0;
    }

    public static Supplier<Integer> constantOne() {
        return () -> 1;
    }

    public static Supplier<Float> constantZeroF() {
        return () -> 0.0f;
    }

    public static Supplier<Float> constantOneF() {
        return () -> 1.0f;
    }

    public static IntSupplier constantInt(int value) {
        return () -> value;
    }

    public static IntSupplier constant0() {
        return () -> 0;
    }

    public static IntSupplier constant1() {
        return () -> 1;
    }

    public static LongSupplier constantLong(long value) {
        return () -> value;
    }

    public static LongSupplier constant0L() {
        return () -> 0L;
    }

    public static LongSupplier constant1L() {
        return () -> 1L;
    }

    public static DoubleSupplier constantDouble(double value) {
        return () -> value;
    }

    public static DoubleSupplier constant0D() {
        return () -> 0.0;
    }

    public static DoubleSupplier constant1D() {
        return () -> 1.0;
    }

    public static <T> Predicate<T> always(boolean value) {
        return argument -> value;
    }

    public static <T> Predicate<T> alwaysTrue() {
        return ignored -> true;
    }

    public static <T> Predicate<T> alwaysFalse() {
        return ignored -> false;
    }


    public static <T, R> Function<T, R> always(R value) {
        return ignored -> value;
    }

    public static <T> Function<T, Integer> alwaysZero() {
        return ignored -> 0;
    }

    public static <T> Function<T, Integer> alwaysOne() {
        return ignored -> 1;
    }

    public static <T> Function<T, Integer> boxedIndexNotFound() {
        return ignored -> INDEX_NOT_FOUND;
    }

    public static <T> ToIntFunction<T> alwaysInt(int value) {
        return ignored -> value;
    }

    public static <T> ToIntFunction<T> always0() {
        return ignored -> 0;
    }

    public static <T> ToIntFunction<T> always1() {
        return ignored -> 1;
    }

    public static <T> ToIntFunction<T> alwaysM1() {
        return ignored -> -1;
    }

    public static <T> ToIntFunction<T> indexNotFound() {
        return ignored -> INDEX_NOT_FOUND;
    }

    public static <T> ToLongFunction<T> alwaysLong(long value) {
        return ignored -> value;
    }

    public static <T> ToLongFunction<T> always0L() {
        return ignored -> 0L;
    }

    public static <T> ToLongFunction<T> always1L() {
        return ignored -> 1L;
    }

    public static <T> ToDoubleFunction<T> alwaysDouble(double value) {
        return ignored -> value;
    }

    public static <T> ToDoubleFunction<T> always0D() {
        return ignored -> 0.0;
    }

    public static <T> ToDoubleFunction<T> always1D() {
        return ignored -> 1.0;
    }

    public static <T> UnaryOperator<T> identity() {
        return argument -> argument;
    }

    public static <T> Predicate<T> equal(T value) {
        return Predicate.isEqual(value);
    }

    public static <T> Predicate<T> notEqual(T value) {
        return Predicate.<T>isEqual(value).negate();
    }

    public static <T, R> Function<T, R> forSupplier(@NonNull
                                                    Supplier<R> supplier) {
        return ignored -> supplier.get();
    }

    public static <T> ToIntFunction<T> forIntSupplier(@NonNull
                                                      IntSupplier supplier) {
        return ignored -> supplier.getAsInt();
    }

    public static <T> ToLongFunction<T> forLongSupplier(@NonNull
                                                        LongSupplier supplier) {
        return ignored -> supplier.getAsLong();
    }

    public static <T> ToDoubleFunction<T> forDoubleSupplier(@NonNull
                                                            DoubleSupplier
                                                                    supplier) {
        return ignored -> supplier.getAsDouble();
    }

    public static <T, R> Function<T, R> forMap(@NonNull Map<T, R> map) {
        return forMapDefault(map, null);
    }

    public static <T, R> Function<T, Optional<R>> forMapOpt(@NonNull
                                                            Map<T, R> map) {
        return key -> Optional.ofNullable(map.get(key));
    }

    public static <T, R> Function<T, R> forMapDefault(@NonNull
                                                      Map<T, R> map,
                                                      R defaultValue) {
        return key -> map.getOrDefault(key, defaultValue);
    }

    public static <T, V, R> Function<V, R> bindFirst(@NonNull
                                                     BiFunction<T, V, R> func,
                                                     T first) {
        return second -> func.apply(first, second);
    }

    public static <T, V, R> Function<T, R> bindSecond(@NonNull
                                                      BiFunction<T, V, R> func,
                                                      V second) {
        return first -> func.apply(first, second);
    }

    public static <T> UnaryOperator<T> bBindFirst(@NonNull
                                                  BinaryOperator<T> func,
                                                  T first) {
        return second -> func.apply(first, second);
    }

    public static <T> UnaryOperator<T> bBindSecond(@NonNull
                                                   BinaryOperator<T> func,
                                                   T second) {
        return first -> func.apply(first, second);
    }

    public static <T> Supplier<T> uBindFirst(@NonNull
                                             UnaryOperator<T> func,
                                             T first) {
        return () -> func.apply(first);
    }

    public static <T> Supplier<T> u2Supplier(@NonNull
                                             UnaryOperator<T> func,
                                             T first) {
        return uBindFirst(func, first);
    }

    public static <T, V> Consumer<V> cBindFirst(@NonNull
                                                BiConsumer<T, V> func,
                                                T first) {
        return second -> func.accept(first, second);
    }

    public static <T, V> Consumer<T> cBindSecond(@NonNull
                                                 BiConsumer<T, V> func,
                                                 V second) {
        return first -> func.accept(first, second);
    }

    public static <T, V> Predicate<V> pBindFirst(@NonNull
                                                 BiPredicate<T, V> func,
                                                 T first) {
        return second -> func.test(first, second);
    }

    public static <T, V> Predicate<T> pBindSecond(@NonNull
                                                  BiPredicate<T, V> func,
                                                  V second) {
        return first -> func.test(first, second);
    }

    public static <T> BooleanSupplier p2Supplier(@NonNull
                                                 Predicate<T> predicate,
                                                 T argument) {
        return () -> predicate.test(argument);
    }

    public static <T, R> Supplier<R> fBindFirst(@NonNull
                                                Function<T, R> func,
                                                T first) {
        return () -> func.apply(first);
    }

    public static <T, R> Supplier<R> f2Supplier(@NonNull
                                                Function<T, R> func,
                                                T argument) {
        return fBindFirst(func, argument);
    }

    public static <T> UnaryOperator<T> asUnary(@NonNull
                                               Function<T, T> func) {
        return func::apply;
    }

    public static <T> BinaryOperator<T> asBinary(@NonNull
                                                 BiFunction<T, T, T> func) {
        return func::apply;
    }

    public static <T> Runnable toRunnable(@NonNull
                                          Consumer<T> consumer,
                                          T argument) {
        return () -> consumer.accept(argument);
    }

    public static <T, R> Runnable toRunnable(@NonNull
                                             Function<T, R> func,
                                             T argument) {
        return () -> func.apply(argument);
    }

    public static <T> Runnable toRunnable(@NonNull
                                          Supplier<T> supplier) {
        return supplier::get;
    }

    @SafeVarargs
    public static <T> Stream<T> asStream(@Nullable T... array) {
        if (array == null || array.length == 0) {
            return Stream.empty();
        }
        return Arrays.stream(array);
    }

    public static <T> Stream<T> asStream(@Nullable
                                         Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Stream.empty();
        }
        return collection.stream();
    }

    public static IntStream asIntStream(@Nullable
                                        int... array) {
        if (array == null || array.length == 0) {
            return IntStream.empty();
        }
        return Arrays.stream(array);
    }

    public static LongStream asLongStream(@Nullable
                                          long... array) {
        if (array == null || array.length == 0) {
            return LongStream.empty();
        }
        return Arrays.stream(array);
    }

    public static DoubleStream asDoubleStream(@Nullable
                                              double... array) {
        if (array == null || array.length == 0) {
            return DoubleStream.empty();
        }
        return Arrays.stream(array);
    }

    public static Stream<Float> asStream(@Nullable
                                         float... array) {
        if (array == null || array.length == 0) {
            return Stream.empty();
        }
        return Floats.asList(array).stream();
    }

    public static <T> Stream<T> copyStream(@Nullable
                                           Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Stream.empty();
        }
        return new ArrayList<>(collection).stream();
    }

    @SafeVarargs
    public static <T> Stream<T> copyStream(@Nullable
                                           T... array) {
        if (array == null || array.length == 0) {
            return Stream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static IntStream copyIntStream(@Nullable
                                          int... array) {
        if (array == null || array.length == 0) {
            return IntStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static LongStream copyLongStream(@Nullable
                                            long... array) {
        if (array == null || array.length == 0) {
            return LongStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static DoubleStream copyDoubleStream(@Nullable
                                                double... array) {
        if (array == null || array.length == 0) {
            return DoubleStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static Stream<Float> copyStream(@Nullable
                                           float... array) {
        if (array == null || array.length == 0) {
            return Stream.empty();
        }
        return asStream(Arrays.copyOf(array, array.length));
    }

    @SafeVarargs
    public static <T> Stream<T> joinStream(@Nullable
                                           Collection<? extends T>...
                                                   collections) {
        return asStream(collections)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream);
    }

    @SafeVarargs
    public static <T, V extends T> Stream<T> joinStream(@Nullable
                                                        V[]... arrays) {
        return asStream(arrays)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream);
    }

    @SafeVarargs
    public static <T, V extends T, S extends T> Stream<T> appendStream(
            @Nullable Collection<V> collection, @Nullable S... values) {
        return Stream.concat(asStream(collection), asStream(values));
    }

    @SafeVarargs
    public static <T, V extends T, S extends T> Stream<T> appendStream(
            @Nullable V[] array, @Nullable S... values) {
        return Stream.concat(asStream(array), asStream(values));
    }

    @SafeVarargs
    public static <T, V extends T, S extends T> Stream<T> prependStream(
            @Nullable Collection<V> collection, @Nullable S... values) {
        return Stream.concat(asStream(values), asStream(collection));
    }

    @SafeVarargs
    public static <T, V extends T, S extends T> Stream<T> prependStream(
            @Nullable V[] array, @Nullable S... values) {
        return Stream.concat(asStream(values), asStream(array));
    }

    @SafeVarargs
    public static <T> Stream<T> joinStreams(@Nullable
                                            Stream<T>... streams) {
        return asStream(streams)
                .filter(Objects::nonNull)
                .reduce(Stream::concat)
                .orElseGet(Stream::empty);
    }

    public static <T> Stream<T> joinStreams(@Nullable
                                            Collection<Stream<T>> streams) {
        return asStream(streams)
                .filter(Objects::nonNull)
                .reduce(Stream::concat)
                .orElseGet(Stream::empty);
    }

    public static <T> Predicate<T> negate(@NonNull
                                          Predicate<T> predicate) {
        return predicate.negate();
    }

    @SafeVarargs
    public static <T> Predicate<T> or(@NonNull
                                      Predicate<? super T>... predicates) {
        if (predicates == null || predicates.length == 0) {
            return alwaysFalse();
        }
        return argument ->
                Arrays.stream(predicates)
                        .filter(Objects::nonNull)
                        .anyMatch(predicate ->
                                predicate.test(argument));
    }

    public static <T> Predicate<T> or(@Nullable
                                      Collection<Predicate<? super T>>
                                              predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return alwaysFalse();
        }
        return argument ->
                predicates.stream()
                        .filter(Objects::nonNull)
                        .anyMatch(predicate -> predicate.test(argument));
    }

    @SafeVarargs
    public static <T> Predicate<T> and(@NonNull
                                       Predicate<? super T>...
                                               predicates) {
        if (predicates == null || predicates.length == 0) {
            return alwaysTrue();
        }
        return argument ->
                Arrays.stream(predicates)
                        .filter(Objects::nonNull)
                        .anyMatch(predicate -> predicate.test(argument));
    }

    public static <T> Predicate<T> and(@Nullable
                                       Collection<Predicate<? super T>>
                                               predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return alwaysTrue();
        }
        return argument ->
                predicates.stream()
                        .filter(Objects::nonNull)
                        .anyMatch(predicate -> predicate.test(argument));
    }

    @SafeVarargs
    public static <T> Predicate<T> xor(@NonNull
                                       Predicate<? super T>...
                                               predicates) {
        if (predicates == null || predicates.length == 0) {
            return alwaysFalse();
        }
        return argument ->
                Arrays.stream(predicates)
                        .filter(Objects::nonNull)
                        .map(predicate -> predicate.test(argument))
                        .reduce(Boolean.FALSE, Boolean::logicalXor);
    }

    public static <T> Predicate<T> xor(@Nullable
                                       Collection<Predicate<? super T>>
                                               predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return alwaysFalse();
        }
        return argument ->
                predicates.stream()
                        .filter(Objects::nonNull)
                        .map(predicate -> predicate.test(argument))
                        .reduce(Boolean.FALSE, Boolean::logicalXor);
    }

    public static <T, R> Function<T, R> ignoreArgument(@NonNull
                                                       Supplier<R> supplier) {
        return ignored -> supplier.get();
    }

    public static <T, V, R> BiFunction<T, V, R> ignoreFirst(@NonNull
                                                            Function<V, R>
                                                                    func) {
        return (ignored, second) -> func.apply(second);
    }

    public static <T, V, R> BiFunction<T, V, R> ignoreSecond(@NonNull
                                                             Function<T, R>
                                                                     func) {
        return (first, ignored) -> func.apply(first);
    }

    public static <T> BinaryOperator<T> bIgnoreFirst(@NonNull
                                                     UnaryOperator<T> func) {
        return (ignored, second) -> func.apply(second);
    }

    public static <T> BinaryOperator<T> bIgnoreSecond(@NonNull
                                                      UnaryOperator<T> func) {
        return (first, ignored) -> func.apply(first);
    }

    public static <T> Predicate<T> pIgnoreArgument(@NonNull
                                                   BooleanSupplier supplier) {
        return ignored -> supplier.getAsBoolean();
    }

    public static <T, V> BiPredicate<T, V> pIgnoreFirst(@NonNull
                                                        Predicate<V> func) {
        return (ignored, second) -> func.test(second);
    }

    public static <T, V> BiPredicate<T, V> pIgnoreSecond(@NonNull
                                                         Predicate<T> func) {
        return (first, ignored) -> func.test(first);
    }

    public static <T> Consumer<T> cIgnoreArgument(@NonNull
                                                  Runnable runnable) {
        return ignored -> runnable.run();
    }

    public static <T, V> BiConsumer<T, V> cIgnoreFirst(@NonNull
                                                       Consumer<V> consumer) {
        return (ignored, second) -> consumer.accept(second);
    }

    public static <T, V> BiConsumer<T, V> cIgnoreSecond(@NonNull
                                                        Consumer<T> consumer) {
        return (first, ignored) -> consumer.accept(first);
    }

    public static IntUnaryOperator plus(int addend) {
        return aInt -> aInt + addend;
    }

    public static UnaryOperator<Integer> negateBoxed() {
        return aInteger -> aInteger == null ? null : -aInteger;
    }

    public static IntUnaryOperator negate() {
        return aInt -> -aInt;
    }

    public static LongUnaryOperator plus(long addend) {
        return aLong -> aLong + addend;
    }

    public static UnaryOperator<Long> negateLBoxed() {
        return aLong -> aLong == null ? null : -aLong;
    }

    public static LongUnaryOperator negateL() {
        return aLong -> -aLong;
    }

    public static DoubleUnaryOperator plus(double addend) {
        return aDouble -> aDouble + addend;
    }

    public static UnaryOperator<Double> negateDBoxed() {
        return aDouble -> aDouble == null ? null : -aDouble;
    }

    public static DoubleUnaryOperator negateD() {
        return aDouble -> -aDouble;
    }

    public static UnaryOperator<Float> plus(float addend) {
        return aFloat -> aFloat == null ? null : aFloat + addend;
    }

    public static UnaryOperator<Number> plusBoxed(@NonNull
                                                  Number addend) {
        return argument ->
                Utils.boxedArithmetic(
                        BinaryMathOperation.Addition.getHolder(),
                        argument, addend);
    }

    public static UnaryOperator<Number> arithmeticBindFirst(@NonNull
                                                            BinaryMathOperation
                                                                    operation,
                                                            @NonNull
                                                            Number first) {

        return second ->
                Utils.boxedArithmetic(
                        operation.getHolder(),
                        first, second);
    }

    public static UnaryOperator<Number> arithmeticBindSecond(@NonNull
                                                             BinaryMathOperation
                                                                     operation,
                                                             @NonNull
                                                             Number second) {
        return first ->
                Utils.boxedArithmetic(
                        operation.getHolder(),
                        first, second);
    }

    public static <N extends Number> Predicate<N> greaterBoxed(
            @NonNull N aNumber) {
        return argument ->
                argument != null
                        && Utils.boxedCompare(argument, aNumber) > 0;
    }

    public static IntPredicate greater(int aInt) {
        return argument -> argument > aInt;
    }

    public static <N extends Number> Predicate<N> greaterOrEqualBoxed(
            N aNumber) {
        return argument ->
                argument != null
                        && Utils.boxedCompare(argument, aNumber) >= 0;
    }

    public static IntPredicate greaterOrEqual(int aInt) {
        return argument -> argument >= aInt;
    }

    public static <N extends Number> Predicate<N> lessBoxed(N aNumber) {
        return argument ->
                argument != null
                        && Utils.boxedCompare(argument, aNumber) < 0;
    }

    public static IntPredicate less(int aInt) {
        return argument -> argument < aInt;
    }


    public static <N extends Number> Predicate<N> lessOrEqualBoxed(N aNumber) {
        return argument ->
                argument != null
                        && Utils.boxedCompare(argument, aNumber) <= 0;
    }


    public static IntPredicate lessOrEqual(int aInt) {
        return argument -> argument <= aInt;
    }

    public static LongPredicate greater(long aLong) {
        return argument -> argument > aLong;
    }

    public static LongPredicate greaterOrEqual(long aLong) {
        return argument -> argument >= aLong;
    }

    public static LongPredicate less(long aLong) {
        return argument -> argument < aLong;
    }

    public static LongPredicate lessOrEqual(long aLong) {
        return argument -> argument <= aLong;
    }

    public static DoublePredicate greater(double aDouble) {
        return argument -> argument > aDouble;
    }

    public static DoublePredicate greaterOrEqual(double aDouble) {
        return argument -> argument >= aDouble;
    }

    public static DoublePredicate less(double aDouble) {
        return argument -> argument < aDouble;
    }

    public static DoublePredicate lessOrEqual(double aDouble) {
        return argument -> argument <= aDouble;
    }

    public static <N extends Number> Predicate<N> inRange(@NonNull
                                                          N low,
                                                          @NonNull
                                                          N high) {
        return argument ->
                Utils.boxedCompare(low, argument) >= 0
                        && Utils.boxedCompare(high, argument) < 0;
    }

    public static <N extends Number> Predicate<N> inClosedRange(@NonNull
                                                                N floor,
                                                                @NonNull
                                                                N ceil) {
        return aNumber ->
                Utils.boxedCompare(floor, aNumber) >= 0
                        && Utils.boxedCompare(ceil, aNumber) <= 0;
    }

    @SafeVarargs
    public static <T> Predicate<T> inArray(T... array) {
        if (ArrayUtils.isEmpty(array)) {
            return alwaysFalse();
        }
        return argument -> ArrayUtils.contains(array, argument);
    }

    public static Predicate<Integer> inIntArray(int... array) {
        if (ArrayUtils.isEmpty(array)) {
            return alwaysFalse();
        }
        return aInt -> ArrayUtils.contains(array, aInt);
    }

    public static IntPredicate inInts(int... array) {
        if (ArrayUtils.isEmpty(array)) {
            return ignored -> false;
        }
        return aInt -> ArrayUtils.contains(array, aInt);
    }

    public static LongPredicate inLongs(long... array) {
        if (ArrayUtils.isEmpty(array)) {
            return ignored -> false;
        }
        return aLong -> ArrayUtils.contains(array, aLong);
    }

    public static DoublePredicate inDoubles(double... array) {
        if (ArrayUtils.isEmpty(array)) {
            return ignored -> false;
        }
        return aDouble -> ArrayUtils.contains(array, aDouble);
    }

    public static <T> Predicate<T> inCollection(@Nullable
                                                Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return alwaysFalse();
        }
        return collection::contains;
    }

    public static <T> Predicate<T> inString(@Nullable String aString) {
        if (aString == null) {
            return alwaysFalse();
        }
        return argumnet ->
                argumnet != null
                        && aString.contains(argumnet.toString());
    }

    public static <T> Predicate<T> inStringIgnoreCase(@Nullable
                                                      String aString) {
        if (aString == null) {
            return alwaysFalse();
        }
        var lowerString = aString.toLowerCase();
        return argumnet ->
                argumnet != null
                        && lowerString.contains(
                        argumnet.toString().toLowerCase());
    }

    public static <T> ToIntFunction<T[]> indexOf(@Nullable
                                                 T value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static <T> ToIntFunction<T[]> lastIndexOf(@Nullable
                                                     T value) {
        return array -> ArrayUtils.lastIndexOf(array, value);
    }

    public static <T> ToIntFunction<List<T>> listIndexOf(@Nullable
                                                         T value) {
        return list ->
                list == null || list.isEmpty()
                        ? INDEX_NOT_FOUND : list.indexOf(value);
    }

    public static <T> ToIntFunction<List<T>> listLastIndexOf(
            @Nullable T value) {
        return list ->
                list == null || list.isEmpty()
                        ? INDEX_NOT_FOUND : list.lastIndexOf(value);
    }

    public static ToIntFunction<String> stringIndexOf(Object value) {
        if (value == null) {
            return indexNotFound();
        }
        var valueString = value.toString();
        return aString -> aString.indexOf(valueString);
    }

    public static ToIntFunction<String> stringLastIndexOf(Object value) {
        if (value == null) {
            return indexNotFound();
        }
        var valueString = value.toString();
        return aString -> aString.lastIndexOf(valueString);
    }

    public static ToIntFunction<String> stringIndexOfIgnoreCase(Object value) {
        if (value == null) {
            return indexNotFound();
        }
        var valueString = value.toString().toLowerCase();
        return aString -> aString.toLowerCase().indexOf(valueString);
    }

    public static ToIntFunction<String> stringLastIndexOfIgnoreCase(
            @Nullable Object value) {
        if (value == null) {
            return indexNotFound();
        }
        var valueString = value.toString().toLowerCase();
        return aString -> aString.toLowerCase().indexOf(valueString);
    }


    public static ToIntFunction<int[]> indexOf(int value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static ToIntFunction<int[]> lastIndexOf(int value) {
        return array -> ArrayUtils.lastIndexOf(array, value);
    }

    public static ToIntFunction<long[]> indexOf(long value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static ToIntFunction<long[]> lastIndexOf(long value) {
        return array -> ArrayUtils.lastIndexOf(array, value);
    }

    public static ToIntFunction<double[]> indexOf(double value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static ToIntFunction<double[]> lastIndexOf(double value) {
        return array -> ArrayUtils.lastIndexOf(array, value);
    }

    public static <T> Predicate<T[]> arrayContains(T value) {
        return (T[] array) -> ArrayUtils.contains(array, value);
    }

    public static Predicate<int[]> arrayContains(int value) {
        return intsContains(value);
    }

    public static Predicate<long[]> arrayContains(long value) {
        return longsContains(value);
    }

    public static Predicate<double[]> arrayContains(double value) {
        return doublesContains(value);
    }

    public static Predicate<float[]> arrayContains(float value) {
        return floatsContains(value);
    }

    public static Predicate<int[]> intsContains(int value) {
        return array -> ArrayUtils.contains(array, value);
    }

    public static Predicate<long[]> longsContains(long value) {
        return array -> ArrayUtils.contains(array, value);
    }

    public static Predicate<double[]> doublesContains(double value) {
        return array -> ArrayUtils.contains(array, value);
    }

    public static Predicate<float[]> floatsContains(float value) {
        return array -> ArrayUtils.contains(array, value);
    }

    public static <T> Predicate<Collection<T>> collectionContains(@Nullable
                                                                  T value) {
        return collection ->
                collection != null
                        && !collection.isEmpty()
                        && collection.contains(value);
    }

    public static Predicate<String> stringContains(@Nullable
                                                   Object value) {
        if (value == null) {
            return alwaysFalse();
        }
        var valueString = value == null ? null : value.toString();
        return aString -> aString.contains(valueString);
    }

    public static Predicate<Collection<String>> containsIgnoreCase(
            @Nullable Object value) {
        var valueString =
                value == null
                        ? null
                        : value.toString();
        return collection ->
                collection != null
                        && !collection.isEmpty()
                        && collection.stream().anyMatch(
                        aString ->
                                aString.equalsIgnoreCase(valueString));
    }

    public static BinaryOperator<String> stringJoinBy(@Nullable
                                                      CharSequence separator) {
        var aString = separator == null ? StringUtils.EMPTY : separator;
        return (joined, current) -> joined + aString + current;
    }

    public static Function<String, String[]> splitBy(@Nullable
                                                     CharSequence separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        var pattern = Pattern.compile(separator.toString());
        return pattern::split;
    }

    public static BiFunction<String, Integer, String[]> splitWithLimit(
            @Nullable CharSequence separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        var pattern = Pattern.compile(separator.toString());
        return pattern::split;
    }

    public static Function<String, Stream<String>> splitToStream(
            @Nullable CharSequence separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        var pattern = Pattern.compile(separator.toString());
        return pattern::splitAsStream;
    }

    public static Function<CharSequence, List<String>> splitToList(
            @Nullable CharSequence separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        var pattern = Pattern.compile(separator.toString());
        return input -> {
            if (input == null) {
                return Collections.emptyList();
            }
            return pattern.splitAsStream(input).collect(Collectors.toList());
        };
    }

    public static Function<String, Map<String, String>> splitToMap(
            @Nullable CharSequence keyValueSeparator,
            @Nullable CharSequence entrySeparator) {
        if (keyValueSeparator == null) {
            keyValueSeparator = StringUtils.EMPTY;
        }
        var kvPattern = Pattern.compile(keyValueSeparator.toString());
        if (entrySeparator == null) {
            entrySeparator = StringUtils.EMPTY;
        }
        var entryPattern = Pattern.compile(entrySeparator.toString());
        return input -> {
            if (input == null || input.isEmpty()) {
                return Collections.emptyMap();
            }
            return entryPattern.splitAsStream(input)
                    .map(kvPattern::split)
                    .collect(
                            Collectors.toMap(
                                    Utils.arrayGetI(0),
                                    Utils.arrayGetI(1)));
        };
    }

    public static Function<Object[], String> arrayJoinBy(@Nullable
                                                         CharSequence
                                                                 separator) {
        return array -> StringUtils.join(array, separator);
    }

    public static <T> Function<Iterable<T>, String> iterableJoinBy(
            @Nullable CharSequence separator) {
        return iterable -> StringUtils.join(iterable, separator);
    }

    public static <T, V> Function<Map<T, V>, String> mapJoinBy(
            @Nullable CharSequence keyValueSeparator,
            @Nullable CharSequence entrySeparator) {
        var kvSeparator =
                keyValueSeparator == null
                        ? StringUtils.EMPTY
                        : keyValueSeparator.toString();
        var enSeparator =
                entrySeparator == null
                        ? StringUtils.EMPTY
                        : entrySeparator.toString();
        return map -> {
            if (map == null || map.isEmpty()) {
                return StringUtils.EMPTY;
            }
            var builder = new StringBuilder(map.size() * 10);
            map.forEach((key, value) -> {
                if (builder.length() > 0) {
                    builder.append(enSeparator);
                }
                builder.append(key).append(kvSeparator).append(value);
            });
            return builder.toString();
        };
    }

    public static UnaryOperator<String> append(Object suffix) {
        var suffixString = String.valueOf(suffix);
        return aString -> aString + suffixString;
    }

    public static UnaryOperator<String> prepend(Object prefix) {
        var prefixString = String.valueOf(prefix);
        return aString -> prefixString + aString;
    }

    public static <T> Stream<T> reverse(@Nullable Stream<T> stream) {
        if (stream == null) {
            return Stream.empty();
        }
        Deque<T> deque = new ArrayDeque<>();
        stream.sequential().forEachOrdered(deque::addFirst);
        return deque.stream();
    }

    public static IntStream reverse(@Nullable IntStream stream) {
        return reverse(stream.boxed()).mapToInt(Integer::intValue);
    }

    public static LongStream reverse(@Nullable LongStream stream) {
        return reverse(stream.boxed()).mapToLong(Long::longValue);
    }

    public static DoubleStream reverse(@Nullable DoubleStream stream) {
        return reverse(stream.boxed()).mapToDouble(Double::doubleValue);
    }

    public static <T> Stream<T> reverseStream(@Nullable
                                              Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Stream.empty();
        }
        Deque<T> deque = new ArrayDeque<>(collection.size());
        collection.forEach(deque::addFirst);
        return deque.stream();
    }

    @SafeVarargs
    public static <T> Stream<T> reverseStream(@Nullable T... array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return Stream.empty();
        }
        return IntStream.range(0, length)
                .mapToObj(index ->
                        array[length - index - 1]);
    }

    public static IntStream reverseStream(int... array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return IntStream.empty();
        }
        return IntStream.range(0, length)
                .map(index ->
                        array[length - index - 1]);
    }

    public static LongStream reverseStream(long... array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return LongStream.empty();
        }
        return IntStream.range(0, length)
                .mapToLong(index ->
                        array[length - index - 1]);
    }

    public static DoubleStream reverseStream(double... array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return DoubleStream.empty();
        }
        return IntStream.range(0, length)
                .mapToDouble(index ->
                        array[length - index - 1]);
    }

    public static <T> Stream<T> reverseStream(@Nullable List<T> list) {
        int size;
        if (list == null || (size = list.size()) == 0) {
            return Stream.empty();
        }
        var iterator = list.listIterator(size - 1);
        if (!iterator.hasPrevious()) {
            return Stream.empty();
        }
        return Stream.iterate(iterator.previous(),
                ignored ->
                        iterator.hasPrevious(),
                ignored ->
                        iterator.previous());
    }

    public static <T> Stream<T> reverseStream(@Nullable Deque<T> deque) {
        if (deque == null || deque.isEmpty()) {
            return Stream.empty();
        }
        var iterator = deque.descendingIterator();
        if (!iterator.hasNext()) {
            return Stream.empty();
        }
        return Stream.iterate(iterator.next(),
                ignored ->
                        iterator.hasNext(),
                ignored ->
                        iterator.next());
    }


    public static IntUnaryOperator unboxIU(UnaryOperator<Number> func) {
        return aInt -> func.apply(aInt).intValue();
    }

    public static LongUnaryOperator unboxLU(UnaryOperator<Number> func) {
        return aLong -> func.apply(aLong).longValue();
    }

    public static DoubleUnaryOperator unboxDU(UnaryOperator<Number> func) {
        return aDouble -> func.apply(aDouble).doubleValue();
    }

    public static ToIntFunction<Number> unboxTI(Function<Number, Number> func) {
        return aNumber -> func.apply(aNumber).intValue();
    }

    public static IntFunction<Number> unboxIF(Function<Number, Number> func) {
        return aNumber -> func.apply(aNumber).intValue();
    }

    public static ToLongFunction<Number> unboxTL(Function<Number, Number> func) {
        return aNumber -> func.apply(aNumber).longValue();
    }

    public static LongFunction<Number> unboxLF(Function<Number, Number> func) {
        return aNumber -> func.apply(aNumber).longValue();
    }

    public static ToDoubleFunction<Number> unboxTD(Function<Number, Number> func) {
        return aNumber -> func.apply(aNumber).doubleValue();
    }

    public static DoubleFunction<Number> unboxDF(Function<Number, Number> func) {
        return aNumber -> func.apply(aNumber).doubleValue();
    }

    public static IntPredicate unboxIP(Predicate<Number> predicate) {
        return predicate::test;
    }

    public static LongPredicate unboxLP(Predicate<Number> predicate) {
        return predicate::test;
    }

    public static DoublePredicate unboxDP(Predicate<Number> predicate) {
        return predicate::test;
    }

    public static IntSupplier unboxIS(Supplier<Number> supplier) {
        return () -> supplier.get().intValue();
    }

    public static LongSupplier unboxLS(Supplier<Number> supplier) {
        return () -> supplier.get().longValue();
    }

    public static DoubleSupplier unboxDS(Supplier<Number> supplier) {
        return () -> supplier.get().doubleValue();
    }


    public static <R> Function<Integer, R> box(@NonNull IntFunction<R> func) {
        return aInt -> func.apply(aInt == null ? 0 : aInt);
    }

    public static <R> Function<Long, R> box(@NonNull LongFunction<R> func) {
        return aLong -> func.apply(aLong == null ? 0L : aLong);
    }

    public static <R> Function<Double, R> box(@NonNull DoubleFunction<R> func) {
        return aDouble -> func.apply(aDouble == null ? 0.0 : aDouble);
    }

    public static <T> Function<T, Integer> box(@NonNull ToIntFunction<T> func) {
        return func::applyAsInt;
    }

    public static <T> Function<T, Long> box(@NonNull ToLongFunction<T> func) {
        return func::applyAsLong;
    }

    public static <T> Function<T, Double> box(@NonNull ToDoubleFunction<T> func) {
        return func::applyAsDouble;
    }

    public static UnaryOperator<Integer> box(@NonNull IntUnaryOperator func) {
        return aInt -> func.applyAsInt(aInt == null ? 0 : aInt);
    }

    public static UnaryOperator<Long> box(@NonNull LongUnaryOperator func) {
        return aLong -> func.applyAsLong(aLong == null ? 0L : aLong);
    }

    public static UnaryOperator<Double> box(@NonNull DoubleUnaryOperator func) {
        return aDouble -> func.applyAsDouble(aDouble == null ? 0.0 : aDouble);
    }

    public static Predicate<Integer> box(@NonNull IntPredicate predicate) {
        return aInt -> aInt != null && predicate.test(aInt);
    }

    public static Predicate<Long> box(@NonNull LongPredicate predicate) {
        return aLong -> aLong != null && predicate.test(aLong);
    }

    public static Predicate<Double> box(@NonNull DoublePredicate predicate) {
        return aDouble -> aDouble != null && predicate.test(aDouble);
    }

    public static <T, R> Optional<R> foldSequential(Stream<T> stream,
                                                    BiFunction<R, T, R>
                                                            accumulator) {
        return Optional.ofNullable(foldSequential(stream,
                null, accumulator));
    }

    public static <T, R> R foldSequential(Stream<T> stream,
                                          R identity,
                                          BiFunction<R, T, R> accumulator) {
        if (stream == null) {
            return identity;
        }
        return fold(stream.sequential(), identity, accumulator, bFirstArg());
    }

    public static <T, R> Optional<R> fold(Stream<T> stream,
                                          BiFunction<R, T, R> accumulator,
                                          BinaryOperator<R> selector) {
        return Optional.ofNullable(fold(stream,
                null, accumulator, selector));
    }

    public static <T, R> R fold(Stream<T> stream, R identity,
                                BiFunction<R, T, R> accumulator,
                                BinaryOperator<R> selector) {
        if (stream == null) {
            return identity;
        }
        var holder = Holder.of(identity);
        BiFunction<Holder<R>, T, Holder<R>> wrapAccumulator =
                (currentHolder, current) ->
                        Holder.of(
                                accumulator.apply(
                                        currentHolder.getValue(), current));
        BinaryOperator<Holder<R>> combiner =
                (lHolder, rHolder) ->
                        lHolder.setValue(
                                selector.apply(
                                        lHolder.getValue(),
                                        rHolder.getValue()));
        return stream.reduce(holder, wrapAccumulator, combiner).getValue();
    }


    public static IntBinaryOperator firstInt() {
        return (first, second) -> first;
    }

    public static IntBinaryOperator secondInt() {
        return (first, second) -> second;
    }

    public static LongBinaryOperator firstLong() {
        return (first, second) -> first;
    }

    public static LongBinaryOperator secondLong() {
        return (first, second) -> second;
    }

    public static DoubleBinaryOperator firstDouble() {
        return (first, second) -> first;
    }

    public static DoubleBinaryOperator secondDouble() {
        return (first, second) -> second;
    }

    public static <T, V> BiFunction<T, V, T> firstArg() {
        return (first, second) -> first;
    }

    public static <T> BinaryOperator<T> bFirstArg() {
        return (first, second) -> first;
    }

    public static <T, V> BiFunction<T, V, V> secondArg() {
        return (first, second) -> second;
    }

    public static <T> BinaryOperator<T> bSecondArg() {
        return (first, second) -> second;
    }

    public static <T> Optional<T> first(Stream<T> stream) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.findFirst();
    }

    public static <T> Optional<T> second(Stream<T> stream) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.skip(1).findFirst();
    }

    public static <T> Optional<T> third(Stream<T> stream) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.skip(2).findFirst();
    }

    public static <T> Optional<T> nth(Stream<T> stream, int n) {
        if (stream == null || n <= 0) {
            return Optional.empty();
        }
        return stream.skip(n - 1).findFirst();
    }

    public static <T> T first(Stream<T> stream, T defaultValue) {
        return first(stream).orElse(defaultValue);
    }

    public static <T> T second(Stream<T> stream, T defaultValue) {
        return second(stream).orElse(defaultValue);
    }

    public static <T> T third(Stream<T> stream, T defaultValue) {
        return third(stream).orElse(defaultValue);
    }

    public static <T> T nth(Stream<T> stream, int n, T defaultValue) {
        return nth(stream, n).orElse(defaultValue);
    }

}
