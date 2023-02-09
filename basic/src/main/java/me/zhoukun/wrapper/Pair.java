package me.zhoukun.wrapper;

import lombok.*;
import me.zhoukun.util.Utils;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

/**
 * @author 周锟
 * @date 2023/2/9 20:38
 **/
@SuppressWarnings("unchecked")
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public final class Pair<L, R> {
    private static final Pair<Object, Object> EMPTY
            = of(null, null);
    private final L left;
    private final R right;

    public static <L, R> Pair<L, R> empty() {
        return (Pair<L, R>) EMPTY;
    }

    public static <L, R> Pair<L, R> ofLeft(L left) {
        return of(left, null);
    }

    public static <L, R> Pair<L, R> ofRight(R right) {
        return of(null, right);
    }

    public static <L, R> Pair<L, R> max(@NonNull Comparator<Pair<L, R>> comparator,
                                        Pair<L, R> lPair, Pair<L, R> rPair) {
        return comparator.compare(lPair, rPair) >= 0 ? lPair : rPair;
    }

    public static <L, R> Pair<L, R> min(@NonNull Comparator<Pair<L, R>> comparator,
                                        Pair<L, R> lPair, Pair<L, R> rPair) {
        return comparator.compare(lPair, rPair) <= 0 ? lPair : rPair;
    }

    @NonNull
    public L requireLeft() {
        return Objects.requireNonNull(left, () ->
                String.format("%s left is null", toString()));
    }

    @NonNull
    public R requireRight() {
        return Objects.requireNonNull(right, () ->
                String.format("%s right is null", toString()));
    }

    public Pair<L, R> requireNonNull() {
        requireLeft();
        requireRight();
        return this;
    }

    public Pair<L, R> copy() {
        return of(left, right);
    }

    public <V> Pair<V, R> changeLeft(V newLeft) {
        return of(newLeft, right);
    }

    public <V> Pair<L, V> changeRight(V newRight) {
        return of(left, newRight);
    }

    public <V> Pair<V, R> mapLeft(@NonNull Function<L, V> mapper) {
        return of(mapper.apply(left), right);
    }

    public <V> Pair<L, V> mapRight(@NonNull Function<R, V> mapper) {
        return of(left, mapper.apply(right));
    }

    public <U, V> Pair<U, V> map(@NonNull Function<L, U> leftMapper,
                                 @NonNull Function<R, V> rightMapper) {
        return of(leftMapper.apply(left), rightMapper.apply(right));
    }

    public <V> V map(@NonNull BiFunction<L, R, V> mapper) {
        return mapper.apply(left, right);
    }

    public <V> V map(Function<Pair<L, R>, V> mapper) {
        return mapper.apply(this);
    }

    public void accept(@NonNull BiConsumer<L, R> consumer) {
        consumer.accept(left, right);
    }

    public void accept(@NonNull Consumer<L> lConsumer,
                       @NonNull Consumer<R> rConsumer) {
        lConsumer.accept(left);
        rConsumer.accept(right);
    }

    public void acceptLeft(@NonNull Consumer<L> consumer) {
        consumer.accept(left);
    }

    public void acceptRight(@NonNull Consumer<R> consumer) {
        consumer.accept(right);
    }

    public boolean test(@NonNull BiPredicate<L, R> predicate) {
        return predicate.test(left, right);
    }

    public boolean testLeft(@NonNull Predicate<L> predicate) {
        return predicate.test(left);
    }

    public boolean testRight(@NonNull Predicate<R> predicate) {
        return predicate.test(right);
    }

    public boolean or(@NonNull Predicate<L> lPredicate,
                      @NonNull Predicate<R> rPredicate) {
        return lPredicate.test(left) || rPredicate.test(right);
    }

    public boolean reverseOr(@NonNull Predicate<R> rPredicate,
                             @NonNull Predicate<L> lPredicate) {
        return rPredicate.test(right) || lPredicate.test(left);
    }

    public boolean and(@NonNull Predicate<L> lPredicate,
                       @NonNull Predicate<R> rPredicate) {
        return lPredicate.test(left) && rPredicate.test(right);
    }

    public boolean reverseAnd(@NonNull Predicate<R> rPredicate,
                              Predicate<L> lPredicate) {
        return rPredicate.test(right) && lPredicate.test(left);
    }

    public boolean xor(@NonNull Predicate<L> lPredicate,
                       Predicate<R> rPredicate) {
        return lPredicate.test(left) ^ rPredicate.test(right);
    }

    public Optional<L> leftToOptional() {
        return Optional.ofNullable(left);
    }

    public Optional<R> rightToOptional() {
        return Optional.ofNullable(right);
    }

    public Comparator<Pair<L, R>> comparingLeft(Comparator<L> comparator) {
        return Comparator.comparing(Pair::getLeft,
                Utils.firstNonNull(comparator,
                        (Comparator<? super L>) Comparator.naturalOrder()));
    }

    public Comparator<Pair<L, R>> comparingRight(Comparator<R> comparator) {
        return Comparator.comparing(Pair::getRight,
                Utils.firstNonNull(comparator,
                        (Comparator<? super R>) Comparator.naturalOrder()));
    }

    public Comparator<Pair<L, R>> comparing(Comparator<L> lComparator,
                                            Comparator<R> rComparator) {
        return comparingLeft(lComparator)
                .thenComparing(comparingRight(rComparator));
    }

    public Comparator<Pair<L, R>> reverseComparing(Comparator<L> lComparator,
                                                   Comparator<R> rComparator) {
        return comparingRight(rComparator)
                .thenComparing(comparingLeft(lComparator));
    }

    public Pair<L, R> max(Comparator<Pair<L, R>> comparator, Pair<L, R> pair) {
        return max(comparator, this, pair);
    }

    public Pair<L, R> min(Comparator<Pair<L, R>> comparator, Pair<L, R> pair) {
        return min(comparator, this, pair);
    }
}
