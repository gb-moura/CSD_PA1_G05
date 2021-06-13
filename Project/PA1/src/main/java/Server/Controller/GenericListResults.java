package Server.Controller;

import Server.Replication.*;


import java.util.Arrays;
import java.util.List;

public class GenericListResults<E, V> {

    private ClientReplicator clientReplicator;

    public GenericListResults(ClientReplicator clientReplicator) {
        this.clientReplicator = clientReplicator;
    }

    public List<E> getListWithPath (Path path) {
        return getListWithPath(null, path);
    }

    public List<E> getListWithPath (V args, Path path) {
        System.out.println("PATH     " + path);
        InvokerWrapper<E[]> wallets = clientReplicator.
                invokeUnorderedReplication(args, path);
        return Arrays.asList(wallets.getResultOrThrow());
    }
}
