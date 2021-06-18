package Client.Util;

import java.io.Serializable;

public class ClientInfo implements Serializable {


    private Transaction transaction;
    private byte[] sign;


    public ClientInfo(){

    }

    public ClientInfo(Transaction transaction, byte[] sign){
        this.transaction = transaction;
        this.sign = sign;
    }


}
