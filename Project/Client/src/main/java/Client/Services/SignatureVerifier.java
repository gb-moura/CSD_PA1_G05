package Client.Services;

import Client.Util.ReplicaSignature;
import Client.Util.SystemReply;
import bftsmart.reconfiguration.util.RSAKeyLoader;
import bftsmart.tom.util.TOMUtil;

import java.security.PublicKey;

public class SignatureVerifier {

    private static final String KEYS_DEFAULT_LOAD_PATH = "";

    public static boolean isValidReply(SystemReply systemReply) {
        byte[] reply = systemReply.getReply();
        return systemReply.getSignatures().stream()
                .filter(sign -> isValid(sign, systemReply.getReply()))
                .count() >= 2; //TODO not static quorum size;
    }

    private static boolean isValid(ReplicaSignature signature, byte[] reply) {
        try {
            PublicKey key = new RSAKeyLoader(signature.getReplicaNumber(), KEYS_DEFAULT_LOAD_PATH).
                    loadPublicKey();
            return TOMUtil.verifySignature(key, reply, signature.getSignature());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
