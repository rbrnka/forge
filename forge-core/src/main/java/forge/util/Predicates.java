package forge.util;

import java.util.function.Predicate;

public class Predicates {
    private Predicates(){}

    //TODO: Migrate everything below.
    public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> components) {
        //TODO: Should be able to clean up the casting here.
        return x -> Iterables.all(components, (Predicate<Predicate<? super T>>) i -> i.test(x));
    }
    public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> components) {
        //TODO: Should be able to clean up the casting here.
        return x -> Iterables.any(components, (Predicate<Predicate<? super T>>) i -> i.test(x));
    }
}
