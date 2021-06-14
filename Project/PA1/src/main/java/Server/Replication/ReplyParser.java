package Server.Replication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ReplyParser {
    private final Logger logger =
            LoggerFactory.getLogger(Server.class);

    private byte[] reply = new byte[0];

    private byte[] signedReply = new byte[0];

    public ReplyParser(byte[] fullReply) {
        logger.info("ReplyParser: full reply len=" + fullReply.length);

        try (ByteArrayInputStream in = new ByteArrayInputStream(fullReply);
             DataInputStream inReader = new DataInputStream(in)) {

            int replyLength = inReader.readInt();
            logger.info("ReplyParser: (read) reply len=" + replyLength);
            reply = inReader.readNBytes(replyLength);

            int signedLength = inReader.readInt();
            logger.info("ReplyParser: (read) signed reply len=" + signedLength);
            signedReply = inReader.readNBytes(signedLength);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getReply() {
        return reply;
    }

    public byte[] getSignedReply() {
        return signedReply;
    }

}
