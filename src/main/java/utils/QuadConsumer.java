package utils;

@FunctionalInterface
public interface QuadConsumer<T, U, I, E> {
    void apply(T t, U u, I i, E e);
}
