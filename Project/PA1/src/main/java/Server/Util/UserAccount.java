package Server.Util;

// import javax.persistence.*;
import java.io.Serializable;

//@Entity
//@Table
public class UserAccount implements Serializable {

 //  @Id
    private String id;

    // @Column(name = "money", nullable = false)
    private Long money;

    public UserAccount(){

    }

    public UserAccount(String id, Long money){
        this.id = id;
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public void addMoney(Long money){
        this.money += money;
    }
}