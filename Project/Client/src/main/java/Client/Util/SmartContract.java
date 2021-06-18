package Client.Util;



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

    public boolean validate(Long fromAmount, UserAccount from, UserAccount to){

        if(this.from==null || this.to==null)
            return false;

        if(from==null || to==null)
            return false;
        
        if(!(from.getId().equals(this.from)) || to.getId().equals(this.to))
            return false;
        
        if(this.to.equals(token))
            return false;

        if(amount<0)
            return false;

        if (fromAmount < amount)
            return false;
        
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
