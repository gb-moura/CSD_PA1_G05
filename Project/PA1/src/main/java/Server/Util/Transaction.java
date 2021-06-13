package Server.Util;



import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;


public class Transaction implements Serializable {


 //



    private String from;


    private String to;

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