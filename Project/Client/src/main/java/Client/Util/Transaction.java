package Client.Util;


import java.io.Serializable;


public class Transaction implements Serializable {


    private String from;

    private String to;

    private Long amount;

    private byte[] sign;

    private boolean privacy;

    private byte[] bytes;

    public Transaction(){

    }

    public Transaction(String to, Long amount){
        this.to = to;
        this.amount = amount;
        this.sign = new byte[2048];
        this.bytes = new byte[2048];
    }


    public Transaction(String from, String to, Long amount){
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.sign = new byte[2048];
        this.bytes = new byte[2048];
    }

    public Transaction(String to, Long amount,boolean privacy){
        this.to = to;
        this.amount = amount;
        this.sign = new byte[2048];
        this.bytes = new byte[2048];
        this.privacy = privacy;
    }


    public Transaction(String from, String to, Long amount,boolean privacy){
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.sign = new byte[2048];
        this.bytes = new byte[2048];
        this.privacy = privacy;
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

    public byte[] getSign() {
        return sign;
    }

    public void setSign(byte[] sign) {
        this.sign = sign;
    }

    public byte[] getBytes() {
        return bytes;
    }


    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
      this.privacy=privacy;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }



}