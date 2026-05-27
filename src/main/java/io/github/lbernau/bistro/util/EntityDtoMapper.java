package io.github.lbernau.bistro.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
public class EntityDtoMapper {

    /**
     * This is just a generic converter from entity to dto to copy all matching properties to the new object.
     * To make this task all POJO using the same property-name, in more realistic projects there may be differences and better mappers necessary.
     */
    @SneakyThrows
    public static <S, T> T convert(final S source, final Class<T> targetClazz) {
        final T target = targetClazz.getDeclaredConstructor()
                                    .newInstance();

        BeanUtils.copyProperties(source, target);
        return target;
    }
}
