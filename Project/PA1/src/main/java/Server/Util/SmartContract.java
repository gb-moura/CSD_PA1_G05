package Server.Util;

import java.io.Serializable;

public class SmartContract implements Serializable {


    String token;
    byte[] transactionBytes;
    byte[] transactionSignature;
    Long amount;
    String from;
    String to;


    public SmartContract(String token, byte[] transactionBytes, byte[] transactionSignature, Long amount, String from, String to) {
        this.token = token;
        this.transactionBytes = transactionBytes;
        this.transactionSignature = transactionSignature;
        this.amount = amount;
        this.from = from;
        this.to = to;
    }

    public boolean validate(Long fromAmount, UserAccount from, UserAccount to) {

        if (this.from == null || this.to == null){
            System.out.println("from ou to a null" );
            return false;
        }


        if (from == null || to == null){
            System.out.println("from user ou to user a null" );
            return false;
        }

        if (!(from.getId().equals(this.from)) && to.getId().equals(this.to)){
            System.out.println("3" );
            return false;
        }

        if (this.to.equals(token)){
            System.out.println("mesmo user" );
            return false;
        }

        if (amount < 0){
            System.out.println("amount<0" );
            return false;
        }

        if (fromAmount < amount){
            System.out.println("from<amount>" );
            return false;
        }

        return true;
    }

    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public byte[] getTransactionBytes() {
        return transactionBytes;
    }

    public void setTransactionBytes(byte[] transactionBytes) {
        this.transactionBytes = transactionBytes;
    }

    public byte[] getTransactionSignature() {
        return transactionSignature;
    }

    public void setTransactionSignature(byte[] transactionSignature) {
        this.transactionSignature = transactionSignature;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
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
}