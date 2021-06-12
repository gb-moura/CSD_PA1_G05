package Server.Replication;

import java.io.Serializable;

public class DualArgReplication<E1,E2> implements Serializable {

    private E1 arg1;
    private E2 arg2;

    public DualArgReplication(E1 arg1, E2 arg2){
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public E1 getArg1() {
        return arg1;
    }

    public E2 getArg2() {
        return arg2;
    }
}
