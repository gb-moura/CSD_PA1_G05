package Server.Replication;

import bftsmart.tom.ServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Server.Controller.WalletControllerReplicatorImp;

import java.io.*;

@Service
public class ClientReplicator {
    private final Logger logger =
            LoggerFactory.getLogger(ClientReplicator.class);

    @Autowired
    ServiceProxy serviceProxy;

    public <V> V invokeOrderedReplication(Path path) {
        return invokeOrderedReplication(null, path);
    }

    public <V,E> V invokeOrderedReplication(E object,Path path){
        logger.info("Start invoking replication");
        byte[] replicationInput = convertInput(object, path);
        byte[] replicationReply = serviceProxy.invokeOrdered(replicationInput);
        return parseReplicationReply(replicationReply);
    }

    public <V> V invokeUnorderedReplication (Path path) {
        return invokeUnorderedReplication(null, path);
    }

    public <E,V> V invokeUnorderedReplication (E object, Path path) {
        byte[] replicationInput = convertInput(object, path);
        byte[] replicationReply = serviceProxy.invokeUnordered(replicationInput);
        return parseReplicationReply(replicationReply);
    }

    private <E> byte[] convertInput (E object, Path path) {
        try {
            return tryToConvertInput(object, path);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private <E> byte[] tryToConvertInput (E object, Path path) throws IOException {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(path);
            objOut.writeObject(object);

            objOut.flush();
            byteOut.flush();
            return byteOut.toByteArray();
        }
    }

    private <V> V parseReplicationReply (byte[] replicationReply) {
        try {
            return tryToParseReplicationReply(replicationReply);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private <V> V tryToParseReplicationReply (byte[] replicationReply)
            throws IOException, ClassNotFoundException {
        if (replicationReply == null || replicationReply.length == 0)
            return null;
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(replicationReply);
             ObjectInput objIn = new ObjectInputStream(byteIn)) {
            return (V)objIn.readObject();
        }
    }

}
