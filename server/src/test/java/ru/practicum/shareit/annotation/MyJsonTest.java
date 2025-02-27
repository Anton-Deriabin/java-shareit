package ru.practicum.shareit.annotation;

import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JsonTest
public @interface MyJsonTest {
}
