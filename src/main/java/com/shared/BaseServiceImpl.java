package com.shared;

import org.apache.coyote.Request;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class BaseServiceImpl<T, Type> implements  BaseService<T, Type> {

    private BaseRepositoryImpl<T, Type> repository;

   public BaseServiceImpl(BaseRepositoryImpl<T, Type> repository ) {
       this.repository = repository;
   }


    public T save(T request){
       return this.repository.save(request);
    }

    public List<T> findAll(Pageable pageable){
        return this.repository.findAll();
    }


    public T findById(Type id){
        return (T) this.repository.findById(id);
    }

    public void delete(Type id){
       this.repository.deleteById(id);
    }

    @Override
    public T Update(Type id, T request) {
       T entity = (T) this.repository.findById(id);

       return this.repository.save(request);
    }

}
