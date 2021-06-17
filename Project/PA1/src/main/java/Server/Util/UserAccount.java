package Server.Util;


import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.security.PublicKey;


public class UserAccount implements Serializable {


    private String id;
    private PublicKey publicKey;



    public UserAccount(){

    }

    public UserAccount(String id,PublicKey publicKey){
        this.id = id;
        this.publicKey=publicKey;

    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




}