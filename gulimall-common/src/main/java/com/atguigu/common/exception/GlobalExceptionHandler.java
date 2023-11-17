package com.atguigu.common.exception;

import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler{

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();

        Map<String,String> throwMsg=new HashMap<>();

        bindingResult.getFieldErrors().forEach((fieldError)->{
            throwMsg.put(fieldError.getField() , fieldError.getDefaultMessage());
        });
        log.error("数据校验异常：{}",throwMsg);

        return R.error(ErrorEnum.VALID_EXCEPTION).put("data",throwMsg);
    }

    @ExceptionHandler(value = Exception.class)
    public R handlerUnknownException(Exception e){
        log.error("发生未知异常：{}",e.getMessage());
        e.printStackTrace();

        return R.error(ErrorEnum.UNKNOWN_ERROR);
    }

    @ExceptionHandler(value = GulimallException.class)
    public R handlerGulimallExceptio(GulimallException e){
        log.error("系统抛出异常：{}",e.getMsg());
        return R.error(e.getCode(),e.getMsg());
    }
}
