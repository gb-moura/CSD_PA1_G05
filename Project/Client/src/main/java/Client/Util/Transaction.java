package Client.Util;

import java.io.Serializable;

public class Transaction implements Serializable {

    public Transaction(){

    }
    public Transaction(String toUser,Long amount){
        this.to = toUser;
        this.amount = amount;
    }

    public Transaction(String fromUser, String toUser,Long amount){
        this(toUser,amount);
        this.from = fromUser;
    }

    private Long id;

    private String from;

    private String to;

    private Long amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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