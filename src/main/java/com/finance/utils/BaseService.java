package com.finance.utils;

public  abstract class BaseService<T>   {

    protected String getCurrentClassName() {
        return this.getClass().getName();
    }

    protected String getCurrentMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }


    protected boolean isNullValue(T element) {
        return element == null;
    }

}
