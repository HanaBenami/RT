package il.co.rtcohen.rt.utils;

import com.vaadin.data.ValueProvider;

public class NullPointerExceptionWrapper<T, C> {
    public static <T, C> C getWrapper(T t, ValueProvider<T, C> valueProvider, C ValueIfError) {
        try {
            C value = valueProvider.apply(t);
            if (null == value) {
                throw new NullPointerException();
            }
            return value;
        } catch (NullPointerException nullPointerException) {
            return ValueIfError;
        }
    }
}
