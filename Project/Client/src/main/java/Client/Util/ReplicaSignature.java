package Client.Util;

public class ReplicaSignature {

    private int replicaNumber;

    private byte[] signature;

    public ReplicaSignature() {}

    public int getReplicaNumber() {
        return replicaNumber;
    }

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
