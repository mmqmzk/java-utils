package me.zhoukun.wrapper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author 周锟
 * @date 2023/2/9 20:49
 **/
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class Holder<T>
        implements Consumer<T>, Supplier<T>, Predicate<T> {

    private T data;

    public static <T> Holder<T> of(T data) {
        return new Holder<>(data);
    }

    @Override
    public void accept(T data) {
        setData(data);
    }

    @Override
    public T get() {
        return data;
    }

    public Holder<T> set(T data) {
        return setData(data);
    }

    public Holder<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public boolean test(T t) {
        return equals(t);
    }

    public <V> Holder<V> map(Function<T, V> mapper) {
        return Holder.of(mapper.apply(data));
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(data);
    }
}
