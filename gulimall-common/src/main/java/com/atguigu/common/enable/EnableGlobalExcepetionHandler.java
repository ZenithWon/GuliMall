package com.atguigu.common.enable;

import com.atguigu.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({GlobalExceptionHandler.class})
public @interface EnableGlobalExcepetionHandler {
}
