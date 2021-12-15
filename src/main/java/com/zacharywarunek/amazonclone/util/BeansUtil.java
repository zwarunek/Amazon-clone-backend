package com.zacharywarunek.amazonclone.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.Arrays;

public class BeansUtil<T> {
    public void copyNonNullProperties(T target, T source) {
        if(source == null || target == null || target.getClass() != source.getClass()) return;

        final BeanWrapper src = new BeanWrapperImpl(source);
        final BeanWrapper trg = new BeanWrapperImpl(target);
        System.out.println(Arrays.toString(target.getClass().getDeclaredFields()));
        for(final Field property : target.getClass().getDeclaredFields()) {
            Object providedObject =
                    !property.getName().equals("__$lineHits$__") ? src.getPropertyValue(property.getName()) : null;
            if(providedObject != null) {
                trg.setPropertyValue(property.getName(), providedObject);
            }
        }
    }
}
