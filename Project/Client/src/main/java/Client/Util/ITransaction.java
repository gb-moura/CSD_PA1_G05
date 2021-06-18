package Client.Util;

import java.io.Serializable;

public interface ITransaction extends Serializable {
    String getFrom();

    String getTo();

    Long getAmount();

    byte[] getSign();

    byte[] getBytes();

    void setSign(byte[] sign);

    void setBytes(byte[] byteTransaction);
}
