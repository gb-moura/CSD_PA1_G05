package Server.Util;


//import javax.persistence.*;
import java.io.Serializable;

//@Entity
//@Table
public class Transaction implements Serializable {

    //@Id
   // @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

  //  @Column(name="valueFrom", nullable = false)
    private String from;

  //  @Column(name="valueTo", nullable = false)
    private String to;
    //  @Column(name="amount", nullable = false)
    private Long amount;

    public Transaction(){

    }

    public Transaction(String to, Long amount){
        this.to = to;
        this.amount = amount;
    }

    public Transaction(String from,String to, Long amount){
        this.from = from;
        this.to = to;
        this.amount = amount;
    }



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}