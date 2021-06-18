package Client.Util;


import com.google.gson.Gson;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Block implements Serializable {

    private static Logger logger = Logger.getLogger(Block.class.getName());
    private String hash;
    private String previousHash;



    private List<Transaction> data;
    // private long timestamp;
    private int nonce;

    public Block(){

    }

    public Block(List<Transaction> data, String previousHash){
        this.hash = " ";
        this.previousHash=previousHash;
        //this.timestamp=timestamp;
        this.data =data;
        nonce=0;

    }

    public String calculateBlockHash(){
        Gson parser = new Gson();
        String dataString = parser.toJson(data);
        String dataToHash = previousHash+Integer.toString(nonce)+dataString;
        byte[] bytes = null;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();

    }

    public String mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!hash.substring(0, prefix)
                .equals(prefixString)) {
            nonce++;
            hash = calculateBlockHash();
        }
        return hash;
    }




    public String getHash() {
        return this.hash;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    //   public long getTimestamp(){return this.timestamp;}


    public List<Transaction> getData() {
        return data;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
