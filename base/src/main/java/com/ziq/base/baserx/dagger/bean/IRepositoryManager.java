package com.ziq.base.baserx.dagger.bean;

public interface IRepositoryManager {
     <T> T createService(final Class<T> service);
}
