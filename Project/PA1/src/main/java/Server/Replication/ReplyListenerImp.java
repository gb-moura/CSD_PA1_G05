package Server.Replication;

import Server.Util.ReplicaSignature;
import Server.Util.SystemReply;
import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.AsynchServiceProxy;
import bftsmart.tom.RequestContext;
import bftsmart.tom.core.messages.TOMMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.synchronizedCollection;

public class ReplyListenerImp <E> implements ReplyListener {

    private final Logger logger =
            LoggerFactory.getLogger(ClientAsyncReplicator.class);

    private final AsynchServiceProxy asyncSP;

    private final Collection<ReplicaSignature> replies =
            synchronizedCollection(new LinkedList<>());
    private final AtomicInteger repliesCounter = new AtomicInteger(0);

    private boolean receivedFromThisReplica = false;
    private byte[] thisReplicaReply = null;

    private final BlockingQueue<SystemReply> replyChain;

    public ReplyListenerImp(BlockingQueue<SystemReply> replyChain, AsynchServiceProxy asyncSP) {
        this.replyChain = replyChain;
        this.asyncSP = asyncSP;
    }

    @Override
    public void replyReceived(RequestContext requestContext, TOMMessage msg) {
        recordReply(msg);
        if (msg.getSender() == asyncSP.getProcessId())
            receivedFromThisReplica = true;

        if(hasValidQuorum())
            deliverReply(requestContext);
    }

    private void recordReply(TOMMessage msg) {
        logger.info("ReplyListener: invoked replyReceived");
        ReplyParser parser = new ReplyParser(msg.getContent());
        replies.add(new ReplicaSignature(msg.getSender(), parser.getSignedReply()));
        if (asyncSP.getProcessId() == msg.getSender())
            thisReplicaReply = parser.getReply();
    }

    private boolean hasValidQuorum() {
        double quorum = (Math.ceil((double) (asyncSP.getViewManager().getCurrentViewN() +
                asyncSP.getViewManager().getCurrentViewF() + 1) / 2.0));
        return repliesCounter.incrementAndGet() >= quorum && receivedFromThisReplica;
    }

    private void deliverReply(RequestContext requestContext) {
        logger.info("ReplyListener: enough replies received");
        replyChain.add(new SystemReply(thisReplicaReply, new ArrayList<>(replies)));
        asyncSP.cleanAsynchRequest(requestContext.getOperationId());
    }

}
