package Server.Util;

public class ReplicaSignature {
    private int replicaNumber;

    private byte[] signature;

    public ReplicaSignature(int replicaNumber, byte[] signature) {
        this.replicaNumber = replicaNumber;
        this.signature = signature;
    }

    public ReplicaSignature() {}

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public int getReplicaNumber() {
        return replicaNumber;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
