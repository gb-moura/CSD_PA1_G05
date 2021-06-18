package Client.Util;


import java.io.Serializable;


public class UserAccount implements Serializable {


    private String id;




    public UserAccount(){

    }

    public UserAccount(String id){
        this.id = id;


    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




}