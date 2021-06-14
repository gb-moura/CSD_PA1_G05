package Client.Util;

import java.util.Collection;

public class SystemReply {
    private byte[] reply;

    private Collection<ReplicaSignature> signatures;

    public SystemReply() {}

    public byte[] getReply() {
        return reply;
    }

    public void setReply(byte[] reply) {
        this.reply = reply;
    }

    public Collection<ReplicaSignature> getSignatures() {
        return signatures;
    }

    public void setSignatures(Collection<ReplicaSignature> signatures) {
        this.signatures = signatures;
    }
}
