package me.zhoukun.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.zhoukun.math.BinaryOperatorHolder;
import me.zhoukun.wrapper.ConstInt;
import me.zhoukun.wrapper.Counter;
import me.zhoukun.wrapper.Pair;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 周锟
 */
@Slf4j
public enum Utils {
    ;

    public static String getStackTrace() {
        return getStackTrace(1, 10);
    }

    public static String getStackTrace(int start, int stop) {
        if (start > stop) {
            throw new IllegalArgumentException("start > stop");
        }
        var builder = new StringBuilder((stop - start + 1) * 50);
        var stackTrace = Thread.currentThread().getStackTrace();
        var length = 2;
        if (stackTrace.length < start + length) {
            return builder.toString();
        }
        appendElement(builder, stackTrace[start + 1]);
        for (var i = start + length; i < stop + length + 1 && i < stackTrace.length; i++) {
            builder.append("<=");
            appendElement(builder, stackTrace[i]);
        }
        return builder.toString();
    }

    private static void appendElement(StringBuilder builder, StackTraceElement element) {
        var className = element.getClassName();
        var methodName = element.getMethodName();
        var index = className.lastIndexOf('.');
        builder.append(className.substring(index + 1))
                .append('.')
                .append(methodName)
                .append(':')
                .append(element.getLineNumber());
    }

    /**
     * @param first
     * @param second
     * @param rest
     * @param <T>
     * @return
     * @throws NullPointerException
     */
    @SafeVarargs
    public static <T> T firstNonNull(T first, T second, T... rest) {
        var nonNull = firstSatisfyOrNull(Objects::nonNull, first, second, rest);
        return Objects.requireNonNull(nonNull, "All elements given are null");
    }

    public static int firstNonZero(int first, int second, int... rest) {
        if (first != 0) {
            return first;
        } else if (second != 0) {
            return second;
        } else {
            for (var i : rest) {
                if (i != 0) {
                    return i;
                }
            }
            return 0;
        }
    }

    public static long firstNonZero(long first, long second, long... rest) {
        if (first != 0L) {
            return first;
        } else if (second != 0L) {
            return second;
        } else {
            for (var l : rest) {
                if (l != 0L) {
                    return l;
                }
            }
            return 0L;
        }
    }

    public static float firstNonZero(float first, float second, float... rest) {
        if (first != 0.0F) {
            return first;
        } else if (second != 0.0F) {
            return second;
        } else {
            for (var f : rest) {
                if (f != 0.0F) {
                    return f;
                }
            }
            return 0.0F;
        }
    }

    public static double firstNonZero(double first, double second, double... rest) {
        if (first != 0.0D) {
            return first;
        } else if (second != 0.0D) {
            return second;
        } else {
            for (var d : rest) {
                if (d != 0.0D) {
                    return d;
                }
            }
            return 0.0D;
        }
    }

    @SafeVarargs
    public static <T> T firstSatisfyOrNull(Predicate<T> predicate,
                                           T first, T second, T... rest) {
        return firstSatisfy(null, predicate, first, second, rest);
    }

    @SafeVarargs
    public static <T> T firstSatisfy(T defaultValue, Predicate<T> predicate,
                                     T first, T second, T... rest) {
        if (predicate.test(first)) {
            return first;
        } else if (predicate.test(second)) {
            return second;
        } else {
            for (var t : rest) {
                if (predicate.test(t)) {
                    return t;
                }
            }
        }
        return defaultValue;
    }

    public static int nextInt(int i) {
        if (i <= 0) {
            return 0;
        }
        return ThreadLocalRandom.current().nextInt(i);
    }

    /**
     * @param min
     * @param max
     * @return
     */
    public static int random(int min, int max) {
        return nextInt(max - min + 1) + min;
    }

    public static boolean isLuck(int rate) {
        return isLuck(rate, 100);
    }

    public static Predicate<Integer> isLuckP(int base) {
        return rate -> isLuck(rate, base);
    }

    public static boolean isLuck(int rate, int base) {
        return nextInt(base) < rate;
    }

    public static boolean isLuck10000(int rate) {
        return isLuck(rate, 10000);
    }

    public static OptionalInt randomChoose(int... array) {
        int length;
        if (array == null || (length = array.length) <= 0) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(get(array, nextInt(length)));
    }

    public static OptionalLong randomChoose(long... array) {
        int length;
        if (array == null || (length = array.length) <= 0) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(get(array, nextInt(length)));
    }

    public static OptionalDouble randomChoose(double... array) {
        int length;
        if (array == null || (length = array.length) <= 0) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(get(array, nextInt(length)));
    }

    public static Optional<Float> randomChoose(float... array) {
        int length;
        if (array == null || (length = array.length) <= 0) {
            return Optional.empty();
        }
        return Optional.of(get(array, nextInt(length)));
    }

    @SafeVarargs
    public static <T> Optional<T> randomChoose(T... array) {
        return randomChoose(Functions.always1(), array);
    }

    @SafeVarargs
    public static <T> Optional<T> randomChoose(ToIntFunction<T> weigher, T... array) {
        if (array == null || array.length == 0) {
            return Optional.empty();
        }
        var total = calculateTotalWeight(weigher, array);
        return randomChoose(weigher, Arrays.stream(array), total);
    }

    public static <T> Optional<T> randomChoose(@Nullable Collection<T> collection) {
        return randomChoose(Functions.always1(), collection);
    }

    public static <T> Optional<T> randomChoose(ToIntFunction<T> weigher,
                                               @Nullable Collection<T> collection) {
        if (collection == null) {
            return Optional.empty();
        }
        var total = calculateTotalWeight(weigher, collection);
        return randomChoose(weigher, collection.stream(), total);
    }

    private static <T> Optional<T> randomChoose(ToIntFunction<T> weigher, Stream<T> stream, int total) {
        if (stream == null || total <= 0) {
            return Optional.empty();
        }
        var random = nextInt(total);
        if (weigher == null) {
            return stream.skip(random).findFirst();
        }
        var counter = Counter.of(random);
        return stream.dropWhile(t ->
                        counter.dec(getWeight(weigher, t)).ge0())
                .findFirst();
    }

    @SafeVarargs
    private static <T> int calculateTotalWeight(ToIntFunction<T> weigher, T... array) {
        if (array == null) {
            return 0;
        }
        if (weigher == null) {
            return array.length;
        }
        return calculateTotalWeight(Arrays.stream(array), weigher);
    }

    private static <T> int calculateTotalWeight(ToIntFunction<T> weigher,
                                                @Nullable Collection<T> collection) {
        if (collection == null) {
            return 0;
        }
        if (weigher == null) {
            return collection.size();
        }
        return calculateTotalWeight(collection.stream(), weigher);
    }

    private static <T> int calculateTotalWeight(Stream<T> stream, ToIntFunction<T> weigher) {
        if (stream == null) {
            return 0;
        }
        if (weigher == null) {
            return Math.toIntExact(stream.count());
        }
        return stream.mapToInt(weigher).sum();
    }

    public static int randomIndex(int... array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return -1;
        }
        var total = Arrays.stream(array).sum();
        if (total <= 0) {
            return -1;
        }
        var random = nextInt(total);
        for (var i = 0; i < length; i++) {
            random -= array[i];
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(long... array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return -1;
        }
        var total = Arrays.stream(array).sum();
        if (total <= 0) {
            return -1;
        }
        var random = ThreadLocalRandom.current().nextLong(total);
        for (var i = 0; i < length; i++) {
            random -= array[i];
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    @SafeVarargs
    public static <T> int randomIndex(ToIntFunction<T> weigher, T... array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return -1;
        }
        var total = calculateTotalWeight(weigher, array);
        if (total <= 0) {
            return -1;
        }
        var random = nextInt(total);
        for (var i = 0; i < length; i++) {
            random -= getWeight(weigher, array[i]);
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(@Nullable Integer... array) {
        return randomIndex(Utils::parseToInt, array);
    }

    public static <T> int randomIndex(ToIntFunction<T> weigher, @Nullable List<T> list) {
        int size;
        if (list == null || (size = list.size()) == 0) {
            return -1;
        }
        var total = calculateTotalWeight(weigher, list);
        if (total <= 0) {
            return -1;
        }
        var random = nextInt(total);
        for (var i = 0; i < size; i++) {
            random -= getWeight(weigher, list.get(i));
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(@Nullable List<Integer> list) {
        return randomIndex(Utils::parseToInt, list);
    }

    @SafeVarargs
    public static <T> List<T> randomChooseN(int n, @Nullable T... array) {
        return randomChooseN(Functions.always1(), n, array);
    }

    @SafeVarargs
    public static <T> List<T> randomChooseN(ToIntFunction<T> weigher, int n, @Nullable T... array) {
        if (array == null || array.length == 0 || n <= 0) {
            return Collections.emptyList();
        }
        var total = calculateTotalWeight(weigher, array);
        return randomChooseN(Arrays.stream(array), n, total, weigher);
    }

    public static <T> List<T> randomChooseN(int n, @Nullable Collection<T> collection) {
        return randomChooseN(Functions.always1(), n, collection);
    }

    public static <T> List<T> randomChooseN(ToIntFunction<T> weigher,
                                            int n, @Nullable Collection<T> collection) {
        if (collection == null || collection.size() == 0 || n <= 0) {
            return Collections.emptyList();
        }
        var total = calculateTotalWeight(weigher, collection);
        return randomChooseN(collection.stream(), n, total, weigher);
    }

    private static <T> List<T> randomChooseN(Stream<T> stream, int n, int total, ToIntFunction<T> weigher) {
        if (stream == null || total <= 0 || n <= 0) {
            return Collections.emptyList();
        }
        var count = Counter.of(n);
        var totalWeight = Counter.of(total);
        return stream.filter(obj -> {
            if (count.gt0() && totalWeight.gt0()) {
                var weight = getWeight(weigher, obj);
                var random = nextInt(totalWeight.getAndDec(weight));
                if (random < weight * count.get()) {
                    count.decAndGet();
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private static <T> int getWeight(ToIntFunction<T> weigher, T obj) {
        if (obj == null) {
            return 0;
        }
        return weigher == null ? 1 : weigher.applyAsInt(obj);
    }

    public static <T> Optional<T> randomRemove(@Nullable List<T> list) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.remove(nextInt(list.size())));
    }

    public static <T> Optional<T> randomRemove(@Nullable Collection<T> collection) {
        return randomRemove(Functions.always1(), collection);
    }

    public static <T> Optional<T> randomRemove(ToIntFunction<T> weigher,
                                               @Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Optional.empty();
        }
        var list = randomRemoveN(weigher, 1, collection);
        return Optional.ofNullable(get(list, 0));
    }

    public static <T> List<T> randomRemoveN(int n, @Nullable Collection<T> collection) {
        return randomRemoveN(Functions.always1(), n, collection);
    }

    public static <T> List<T> randomRemoveN(ToIntFunction<T> weigher,
                                            int n, @Nullable Collection<T> collection) {
        if (collection == null || collection.size() == 0 || n <= 0) {
            return Collections.emptyList();
        }
        List<T> result = Lists.newArrayListWithExpectedSize(n);
        if (n >= collection.size()) {
            result.addAll(collection);
            collection.clear();
            return result;
        }
        var total = calculateTotalWeight(weigher, collection);
        if (total <= 0) {
            return Collections.emptyList();
        }
        for (; n > 0 && total > 0; n--) {
            var random = nextInt(total);
            for (var iterator = collection.iterator(); iterator.hasNext(); ) {
                var t = iterator.next();
                var weight = getWeight(weigher, t);
                random -= weight;
                if (random < 0) {
                    result.add(t);
                    iterator.remove();
                    total -= weight;
                    break;
                }
            }
        }
        return result;
    }

    public static int get0(@Nullable int... array) {
        return get(array, 0);
    }


    public static int getLast(@Nullable int... array) {
        return get(array, -1);
    }

    public static int get(@Nullable int[] array, int index) {
        return get(array, index, 0);
    }

    public static int get(@Nullable int[] array, int index, int defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        return array[index];
    }

    public static long get0(@Nullable long... array) {
        return get(array, 0);
    }

    public static long getLast(@Nullable long... array) {
        return get(array, -1);
    }

    public static long get(@Nullable long[] array, int index) {
        return get(array, index, 0L);
    }

    public static long get(@Nullable long[] array, int index, long defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        return array[index];
    }

    public static double get0(@Nullable double... array) {
        return get(array, 0);
    }

    public static double getLast(@Nullable double... array) {
        return get(array, -1);
    }

    public static double get(@Nullable double[] array, int index) {
        return get(array, index, 0.0);
    }

    public static double get(@Nullable double[] array, int index, double defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        return array[index];
    }

    public static float get0(@Nullable float... array) {
        return get(array, 0);
    }

    public static float getLast(@Nullable float... array) {
        return get(array, -1);
    }

    public static float get(@Nullable float[] array, int index) {
        return get(array, index, 0.0f);
    }

    public static float get(@Nullable float[] array, int index, float defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        return array[index];
    }

    @SafeVarargs
    public static <T> T get0(@Nullable T... array) {
        return get(array, 0);
    }

    @SafeVarargs
    public static <T> T getLast(@Nullable T... array) {
        return get(array, -1);
    }

    public static <T> T get(@Nullable T[] array, int index) {
        return get(array, index, null);
    }

    public static <T> T get(@Nullable T[] array, int index, T defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        var t = array[index];
        return t == null ? defaultValue : t;
    }

    public static <T> T get0(@Nullable Collection<T> collection) {
        return get(collection, 0);
    }

    public static <T> T getLast(@Nullable Collection<T> collection) {
        return get(collection, -1);
    }

    public static <T> T get(@Nullable Collection<T> collection, int index) {
        return get(collection, index, null);
    }

    public static <T> T get(@Nullable Collection<T> collection, int index, T defaultValue) {
        int size;
        if (collection == null || (size = collection.size()) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += size;
        }
        if (index < 0 || index >= size) {
            return defaultValue;
        }
        var t = Iterables.get(collection, index);
        return t == null ? defaultValue : t;
    }

    public static <T> T get0(Stream<T> stream) {
        return get0(stream, null);
    }

    public static <T> T get0(Stream<T> stream, T defaultValue) {
        if (stream == null) {
            return defaultValue;
        }
        return stream.findFirst().orElse(defaultValue);
    }

    public static <T> T get(Stream<T> stream, int index) {
        return get(stream, index, null);
    }

    public static <T> T get(Stream<T> stream, int index, T defaultValue) {
        if (stream == null) {
            return defaultValue;
        }
        return stream.skip(index).findFirst().orElse(defaultValue);
    }

    public static <T> Function<T[], T> arrayGetI(int index) {
        return Functions.bindSecond(Utils::get, index);
    }

    public static <T> Function<Collection<T>, T> collectionGetI(int index) {
        return Functions.bindSecond(Utils::get, index);
    }

    public static Function<int[], Integer> intsGetI(int index) {
        return Functions.bindSecond(Utils::get, index);
    }

    public static <T, R> Ordering<T> orderingFromToIntFunction(@NonNull
                                                               ToIntFunction<T> function) {
        return orderingFromToIntFunction(function, true);
    }

    public static <T, R> Ordering<T> orderingFromToIntFunction(@NonNull
                                                               ToIntFunction<T> function,
                                                               boolean nullsFirst) {
        Ordering<T> ordering = Ordering.from((a, b) ->
                a == b ? 0 : Integer.compare(function.applyAsInt(a), function.applyAsInt(b)));
        return nullsFirst ? ordering.nullsFirst() : ordering.nullsLast();
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull
                                                          Function<T,
                                                                  Comparable<R>> function) {
        return orderingFromFunction(function, Ordering.natural(), true);
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull
                                                          Function<T,
                                                                  Comparable<R>> function,
                                                          boolean nullsFirst) {
        return orderingFromFunction(function, Ordering.natural(), nullsFirst);
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull
                                                          Function<T, R> function,
                                                          Comparator<R> comparator) {
        return orderingFromFunction(function, comparator, true);
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull Function<T, R> function,
                                                          @NonNull Comparator<R> comparator,
                                                          boolean nullsFirst) {
        Ordering<T> ordering = Ordering.from((a, b) ->
                a == b ? 0 : comparator.compare(function.apply(a), function.apply(b)));
        return nullsFirst ? ordering.nullsFirst() : ordering.nullsLast();
    }


    @SafeVarargs
    public static <T> Pair<T, Integer> findFirst(@NonNull Predicate<T> predicate,
                                                 @Nullable T... array) {
        var length = 0;
        if (array == null || (length = array.length) == 0) {
            return Pair.empty();
        }
        var index = findFirstIndex(predicate, Arrays.stream(array));
        if (index < 0 || index >= length) {
            return Pair.ofRight(index);
        }
        return Pair.of(get(array, index), index);
    }

    public static <T> int findFirstIndex(@NonNull Predicate<T> predicate,
                                         @NonNull Stream<T> stream) {
        var count = stream.takeWhile(predicate.negate())
                .count();
        return (int) count;
    }

    /**
     * @param predicate
     * @param stream
     * @param n         1 开始，负数表示从后往前, -1 是最后一个
     * @param <T>
     * @return
     */
    public static <T> List<T> findFirstN(@NonNull Predicate<T> predicate,
                                         @NonNull Stream<T> stream,
                                         int n) {
        if (n == 0) {
            return Collections.emptyList();
        } else if (n < 0) {
            n = -n;
            stream = Functions.reverse(stream);
        }
        return stream.filter(predicate)
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThan(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, true, false);
    }

    /**
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThanOrEqualTo(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, true, true);
    }

    /**
     * 返回list中第一个小于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */

    public static int indexLessThan(@Nullable int[] list, int target) {
        return index(list, target, true, false);
    }

    /**
     * 返回list中第一个小于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */

    public static int indexLessThanOrEqualTo(@Nullable int[] list, int target) {
        return index(list, target, true, true);
    }

    /**
     * 返回list中第一个大于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */

    public static int indexMoreThan(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, false, false);
    }

    /**
     * 返回list中第一个大于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */

    public static int indexMoreThanOrEqualTo(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, false, true);
    }

    /**
     * 返回list中第一个大于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */

    public static int indexMoreThan(@Nullable int[] list, int target) {
        return index(list, target, false, false);
    }

    /**
     * 返回list中第一个大于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexMoreThanOrEqualTo(@Nullable int[] list, int target) {
        return index(list, target, false, true);
    }

    private static int index(List<Integer> list, Integer target, boolean less, boolean equal) {
        if (list == null) {
            return -1;
        }
        var size = list.size();
        for (var i = 0; i < size; i++) {
            var n = list.get(i);
            if (n == null) {
                if (equal && target == null) {
                    return i;
                }
            } else if ((less && target < n) || (!less && target > n) || (equal && n.equals(target))) {
                return i;
            }
        }
        return -1;
    }

    private static int index(int[] list, int target, boolean less, boolean equal) {
        if (list == null) {
            return -1;
        }
        var size = list.length;
        for (var i = 0; i < size; i++) {
            var n = list[i];
            if ((less && target < n) || (!less && target > n) || (equal && target == n)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 将字符串数组转换为int数组
     *
     * @param strings
     * @return
     */
    public static int[] parseToInts(@Nullable String[] strings) {
        if (strings == null) {
            return new int[0];
        }
        var ints = new int[strings.length];
        for (var i = 0; i < strings.length; i++) {
            ints[i] = parseToInt(strings[i]);
        }
        return ints;
    }

    public static int[] parseToInts(@Nullable String data, @NonNull String separator) {
        if (data == null || separator == null) {
            return new int[0];
        }
        return parseToInts(data.trim().split(separator));
    }

    private static Number toNumber(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return (Number) object;
        } else if (object instanceof Boolean) {
            var aBoolean = (Boolean) object;
            return aBoolean ? 1 : 0;
        } else if (object instanceof BigInteger) {
            return (Number) object;
        } else if (object instanceof BigDecimal) {
            return (Number) object;
        }
        return null;
    }

    public static int parseToInt(@Nullable Object object) {
        return Ints.saturatedCast(parseToLong(object));
    }

    public static int parseToInt(@Nullable String data) {
        return Ints.saturatedCast(parseToLong(data));
    }

    public static long parseToLong(@Nullable Object object) {
        if (object == null) {
            return 0L;
        }
        var number = toNumber(object);
        if (number != null) {
            return number.longValue();
        }
        return parseToLong(object.toString());
    }

    public static long parseToLong(@Nullable String data) {
        if (data == null) {
            return 0L;
        }
        data = data.trim();
        var length = data.length();
        if (length == 0) {
            return 0L;
        }
        var radix = 10;
        if (data.charAt(0) == '0' && length > 1) {
            var c = data.charAt(1);
            switch (c) {
                case 'x':
                case 'X':
                    if (length > 2) {
                        data = data.substring(2);
                    } else {
                        return 0L;
                    }
                    radix = 16;
                    break;
                case 'b':
                case 'B':
                    if (length > 2) {
                        data = data.substring(2);
                    } else {
                        return 0L;
                    }
                    radix = 2;
                    break;
                default:
                    data = data.substring(1);
                    radix = 8;
                    break;
            }
            if (data.isEmpty()) {
                return 0L;
            }
        }
        Long aLong = null;
        try {
            aLong = Long.parseLong(data, radix);
        } catch (Exception ignore) {
        }
        return aLong == null ? 0L : aLong;
    }

    public static double parseToDouble(@Nullable Object object) {
        if (object == null) {
            return 0.0;
        }
        var number = toNumber(object);
        if (number != null) {
            return number.doubleValue();
        }
        return parseToDouble(object.toString());
    }

    public static double parseToDouble(@Nullable String data) {
        if (data == null) {
            return 0.0;
        }
        data = data.trim();
        if (data.isEmpty()) {
            return 0.0;
        }
        Double aDouble = null;
        try {
            aDouble = Double.parseDouble(data);
        } catch (Exception ignore) {
        }
        return aDouble == null ? 0.0 : aDouble;
    }

    public static boolean parseToBoolean(@Nullable Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (Boolean) object;
        } else {
            var number = toNumber(object);
            if (number != null) {
                return number.doubleValue() != 0;
            }
        }
        return parseToBoolean(object.toString());
    }

    /**
     * true, yes, on(无视大小写),非0数返回true,其他情况返回false
     *
     * @param data
     * @return
     */
    public static boolean parseToBoolean(@Nullable String data) {
        if (data == null) {
            return false;
        }
        data = data.trim();
        if (data.isEmpty()) {
            return false;
        }
        if (data.equalsIgnoreCase("true") || data.equalsIgnoreCase("yes") ||
                data.equalsIgnoreCase("on")) {
            return true;
        }
        return parseToDouble(data) != 0;
    }

    public static long convertTwoIntToLong(int high32, int low32) {
        return ((long) high32) << 32 | (low32);
    }

    public static int[] convertLongToTwoInt(long data) {
        var result = new int[2];
        result[0] = (int) (data >> 32);
        result[1] = (int) (data);
        return result;
    }

    public static Number boxedArithmetic(@NonNull
                                         BinaryOperatorHolder
                                                 operation,
                                         @NonNull
                                         Number first,
                                         @NonNull
                                         Number second) {
        var sBigInt = second instanceof BigInteger
                ? (BigInteger) second
                : null;
        var sDecimal = second instanceof BigDecimal
                ? (BigDecimal) second
                : null;

        if (first instanceof Long
                || first instanceof Integer
                || first instanceof Short
                || first instanceof Byte
                || first instanceof AtomicLong
                || first instanceof AtomicInteger) {
            if (sBigInt != null) {
                return operation.getBigIntOp()
                        .apply(BigInteger.valueOf(first.longValue()), sBigInt);
            }
            if (sDecimal != null) {
                return operation.getDecimalOp()
                        .apply(BigDecimal.valueOf(first.longValue()), sDecimal);
            }
            return operation.getLongOp()
                    .applyAsLong(first.longValue(), second.longValue());
        } else if (first instanceof Double
                || first instanceof Float
                || first instanceof AtomicDouble) {
            if (sBigInt != null) {
                sDecimal = new BigDecimal(sBigInt);
            }
            if (sDecimal != null) {
                return operation.getDecimalOp()
                        .apply(BigDecimal.valueOf(first.doubleValue()),
                                sDecimal);
            }
            return operation.getDoubleOp()
                    .applyAsDouble(first.doubleValue(), second.doubleValue());
        } else if (first instanceof BigInteger) {
            var fBigInt = (BigInteger) first;
            if (sDecimal != null) {
                return operation.getDecimalOp()
                        .apply(new BigDecimal(fBigInt), sDecimal);
            }
            if (sBigInt == null) {
                sBigInt = BigInteger.valueOf(second.longValue());
            }
            return operation.getBigIntOp().apply(fBigInt, sBigInt);
        } else if (first instanceof BigDecimal) {
            var fDecimal = (BigDecimal) first;
            if (sBigInt != null) {
                sDecimal = new BigDecimal(sBigInt);
            }
            if (sDecimal == null) {
                sDecimal = BigDecimal.valueOf(second.doubleValue());
            }
            return operation.getDecimalOp().apply(fDecimal, sDecimal);
        }
        return operation.getDecimalOp()
                .apply(NumberUtils.createBigDecimal(first.toString()),
                        NumberUtils.createBigDecimal(second.toString()));
    }

    public static int boxedCompare(@NonNull Number first,
                                   @NonNull Number second) {
        var sBigInt = second instanceof BigInteger
                ? (BigInteger) second
                : null;
        var sDecimal = second instanceof BigDecimal
                ? (BigDecimal) second
                : null;

        if (first instanceof Long
                || first instanceof Integer
                || first instanceof Short
                || first instanceof Byte
                || first instanceof AtomicLong
                || first instanceof AtomicInteger) {
            if (sBigInt != null) {
                return BigInteger.valueOf(first.longValue())
                        .compareTo(sBigInt);
            }
            if (sDecimal != null) {
                return BigDecimal.valueOf(first
                        .longValue()).compareTo(sDecimal);
            }
            return Long.compare(first.longValue(), second.longValue());
        } else if (first instanceof Double
                || first instanceof Float
                || first instanceof AtomicDouble) {
            if (sBigInt != null) {
                sDecimal = new BigDecimal(sBigInt);
            }
            if (sDecimal != null) {
                return BigDecimal.valueOf(first.doubleValue())
                        .compareTo(sDecimal);
            }
            return Double.compare(first.doubleValue(), second.doubleValue());
        } else if (first instanceof BigInteger) {
            var fBigInt = (BigInteger) first;
            if (sDecimal != null) {
                return new BigDecimal(fBigInt).compareTo(sDecimal);
            }
            if (sBigInt == null) {
                sBigInt = BigInteger.valueOf(second.longValue());
            }
            return fBigInt.compareTo(sBigInt);
        } else if (first instanceof BigDecimal) {
            var fDecimal = (BigDecimal) first;
            if (sBigInt != null) {
                sDecimal = new BigDecimal(sBigInt);
            }
            if (sDecimal == null) {
                sDecimal = BigDecimal.valueOf(second.doubleValue());
            }
            return fDecimal.compareTo(sDecimal);
        }
        return NumberUtils.createBigDecimal(first.toString())
                .compareTo(
                        NumberUtils.createBigDecimal(second.toString()));
    }

    public static Counter counterFromZero() {
        return Counter.of();
    }

    public static Counter counter(int initValue) {
        return Counter.of(initValue);
    }

    public static ConstInt constZero() {
        return ConstInt.CONST_ZERO;
    }

    public static ConstInt constOne() {
        return ConstInt.CONST_ONE;
    }

    public static ConstInt constInt(int constValue) {
        return ConstInt.of(constValue);
    }

    public <T> Pair<T, Integer> findFirst(@NonNull Predicate<T> predicate,
                                          @Nullable List<T> list) {
        var size = 0;
        if (list == null || (size = list.size()) == 0) {
            return Pair.empty();
        }
        var index = findFirstIndex(predicate, list.stream());
        if (index < 0 || index >= size) {
            return Pair.ofRight(index);
        }
        return Pair.of(get(list, index), index);
    }
}
