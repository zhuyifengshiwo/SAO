package com.powernode.handel;

import com.powernode.exception.ResultException;
import com.powernode.util.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobaExceptionHandel {
    @ExceptionHandler
    @ResponseBody
    public Result exceptionHandel(HttpServletRequest request, Exception e) {
        ResultException resultException = null;
        try {
            if (e instanceof ResultException) {
                resultException = (ResultException) e;
            } else {
                resultException = new ResultException("未知错误");
            }
            return Result.build(404, resultException);
        } catch (ResultException e1) {

        }
        return null;
    }
}
