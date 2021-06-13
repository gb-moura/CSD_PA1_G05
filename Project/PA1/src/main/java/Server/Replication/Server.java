package Server.Replication;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import Server.Controller.*;


import Server.Util.*;


import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Arrays;
import java.util.List;

@PropertySource("classpath:application.properties")
@Component
public class Server extends DefaultSingleRecoverable implements Runnable{
	private final Logger logger =
			LoggerFactory.getLogger(Server.class);

	private int counter = 0;

	@Value("${replica.id}")
	private int ID;

	@Qualifier("ImpWallet")
	@Autowired
	private WalletController walletController;

	@Autowired
	MongoTemplate mongoTemplate;

	@PostConstruct
	public void init(){
		mongoTemplate.getDb().drop();
		new ServiceReplica(ID, this, this);
	}

	@Override
	public void run() {
		mongoTemplate.getDb().drop();
		new ServiceReplica(ID, this, this);
	}

	@Override
	public byte[] appExecuteOrdered(byte[] command, MessageContext messageContext) {

		return invokeCommand(command);
	}

	@Override
	public byte[] appExecuteUnordered(byte[] command, MessageContext messageContext) {
		return invokeCommand(command);
	}

	private byte[] invokeCommand(byte[] command) {
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
			 ObjectInput objIn = new ObjectInputStream(byteIn);
			 ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut);) {

			Path path = (Path)objIn.readObject();
			logger.info(String.format("Searching for %s to invoke.", path));
			switch(path){
				case OBTAIN_COINS:
					walletController.obtainCoins((Transaction)objIn.readObject());
					logger.info("Successfully completed createMoney");
					objOut.writeObject(new VoidWrapper());
					counter++;
					break;
				case TRANSFER_MONEY:
					walletController.transferMoney((Transaction)objIn.readObject());
					logger.info("Successfully completed transferMoneyBetweenUsers");
					objOut.writeObject(new VoidWrapper());
					counter++;
					/*objOut.
						writeObject(InvokerWrapper.catchInvocation(
								() -> {
									walletController.transferMoney((Transaction)objIn.readObject());
									logger.info("Successfully completed transferMoneyBetweenUsers");
									return new VoidWrapper();
								}
						));*/
					break;

				case GET_MONEY:
					Long result = walletController.currentAmount((String)objIn.readObject());
					objOut.writeObject(result);
					logger.info("Successfully completed currentAmount");
					/*objOut.
						writeObject(InvokerWrapper.catchInvocation(
								() -> {
									//TODO log first?
									logger.info("Successfully completed currentAmount");
									return walletController.currentAmount((String)objIn.readObject());
								}
						));*/
					break;
				case GET_LEDGER:
					//objOut.writeObject( walletController.ledgerOfGlobalTransactions().toArray());
					//logger.info("Successfully completed ledgerOfClientTransfers");
					 objOut.
						writeObject(InvokerWrapper.catchInvocation(
							() -> {
								logger.info("Successfully completed ledgerOfGlobalTransfers");
								return walletController.ledgerOfGlobalTransactions().toArray();
							}
						));
					break;
				case GET_CLIENT_LEDGER:
					objOut.
							writeObject(InvokerWrapper.catchInvocation(
									() -> {
										logger.info("Successfully completed ledgerOfClientTransfers " + ID);
										return walletController.
												ledgerOfClientTransfers((String)objIn.readObject()).toArray();
									}
							));
					break;


				default:
					logger.error("Not implemented");
					break;
			}

			objOut.flush();
			byteOut.flush();

			return byteOut.toByteArray();
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public void installSnapshot(byte[] state) {
		try {
			System.out.println("setState called");
			ByteArrayInputStream bis = new ByteArrayInputStream(state);
			ObjectInput in = new ObjectInputStream(bis);
			counter =  in.readInt();
			in.close();
			bis.close();
		} catch (Exception e) {
			System.err.println("[ERROR] Error deserializing state: "
					+ e.getMessage());
		}
	}

	@Override
	public byte[] getSnapshot() {
		try {
			System.out.println("getState called");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeInt(counter);
			out.flush();
			bos.flush();
			out.close();
			bos.close();
			return bos.toByteArray();
		} catch (IOException ioe) {
			System.err.println("[ERROR] Error serializing state: "
					+ ioe.getMessage());
			return "ERROR".getBytes();
		}
	}

}