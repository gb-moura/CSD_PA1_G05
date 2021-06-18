package Server.Util;

import java.io.Serializable;

public interface ITransaction extends Serializable {
    String getFrom();

    String getTo();

    Long getAmount();

    byte[] getSign();

    byte[] getBytes();
}
