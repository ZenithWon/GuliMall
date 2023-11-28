package com.atguigu.gulimall.product.utils;

public interface ILock {
    public Boolean tryLock(String key);
    public void delLock(String key);
}
