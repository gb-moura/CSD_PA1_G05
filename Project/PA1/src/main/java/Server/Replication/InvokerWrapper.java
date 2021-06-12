package Server.Replication;

import Server.Exceptions.InternalServerError;

import java.io.Serializable;

public class InvokerWrapper<E extends Serializable> implements Serializable{

    private E result;
    private RuntimeException exception;

    private InvokerWrapper(RuntimeException exception){
        this.exception = exception;
        this.result = null;
    }
    private InvokerWrapper(E result){
        this.exception = null;
        this.result = result;
    }

    public static <E extends Serializable> InvokerWrapper<E> catchInvocation(Invoker<E> invoker){
        try{
            return new InvokerWrapper<>(invoker.doStuff());
        }catch (RuntimeException e){
            e.printStackTrace();
            return new InvokerWrapper<>(e);
        } catch (Exception e){
            e.printStackTrace();
            return new InvokerWrapper<>(new InternalServerError());
        }
    }

    public E getResultOrThrow() {
        if(exception != null)
           throw exception;

        return result;
    }
}
