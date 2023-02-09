package me.zhoukun.wrapper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * @author 周锟
 * @date 2023/2/9 20:49
 **/
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public final class Holder<T>
        implements Consumer<T>, Supplier<T>,
        Predicate<T>, UnaryOperator<T> {

    private T value;

    @Override
    public void accept(T data) {
        setValue(data);
    }

    @Override
    public T get() {
        return getValue();
    }

    public Holder<T> set(T data) {
        return setValue(data);
    }

    public Holder<T> setValue(T value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean test(T value) {
        return Objects.equals(value, this.value);
    }

    @Override
    public T apply(T value) {
        return exchange(value);
    }

    public T exchange(T value) {
        var thisValue = getValue();
        setValue(value);
        return thisValue;
    }

    public void exchangeValue(Holder<T> that) {
        that.exchange(exchange(that.getValue()));
    }

    public <V> Holder<V> map(Function<T, V> mapper) {
        return Holder.of(mapper.apply(value));
    }

    public Optional<T> asOptional() {
        return Optional.ofNullable(value);
    }

    public Stream<T> asStream() {
        return Stream.of(value);
    }

    public Stream<T> asNullableStream() {
        return Stream.ofNullable(value);
    }
}
