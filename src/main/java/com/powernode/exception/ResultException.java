package com.powernode.exception;

import com.powernode.util.Result;
import com.powernode.util.ResultEnum;

public class ResultException extends RuntimeException{
    private ResultEnum resultEnum;

    public ResultException(String massage) {
        super(massage);
    }

    public ResultException(ResultEnum resultEnum){
        this.resultEnum = resultEnum;
    }

    public String getMessage(){
        //添加判断如果没有枚举属性直接返回父级对象的参数
        if (resultEnum ==null){
            return super.getMessage();
        }
     return resultEnum.getMessage();
    }

    public int getStatus(){
        return resultEnum.getStatus();
    }



}
