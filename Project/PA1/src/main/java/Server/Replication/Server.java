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
import org.springframework.stereotype.Component;
import Server.Controller.WalletController;



import Server.Util.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Arrays;

@PropertySource("classpath:application.properties")
@Component
public class Server extends DefaultSingleRecoverable implements Runnable{
	private final Logger logger =
			LoggerFactory.getLogger(Server.class);


	@Value("${replica.id}")
	private int ID;

	@Qualifier("ImpWallet")
	@Autowired
	private WalletController walletController;


	@PostConstruct
	public void init(){
		new ServiceReplica(ID, this, this);
	}

	@Override
	public void run() {
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
				case OBTAIN_COINS: objOut.
						writeObject(InvokerWrapper.catchInvocation(
								() -> {
									walletController.obtainCoins((Transaction)objIn.readObject());
									logger.info("Successfully completed createMoney");
									return new VoidWrapper();
								}
						));
						break;
				case TRANSFER_MONEY: objOut.
						writeObject(InvokerWrapper.catchInvocation(
								() -> {
									walletController.transferMoney((Transaction)objIn.readObject());
									logger.info("Successfully completed transferMoneyBetweenUsers");
									return new VoidWrapper();
								}
						));
						break;

				case GET_MONEY: objOut.
						writeObject(InvokerWrapper.catchInvocation(
								() -> {
									//TODO log first?
									logger.info("Successfully completed currentAmount");
									return walletController.currentAmount((String)objIn.readObject());
								}
						));
						break;
				case GET_LEDGER: objOut.
						writeObject(InvokerWrapper.catchInvocation(
							() -> {
								logger.info("Successfully completed ledgerOfClientTransfers");
								return walletController.ledgerOfGlobalTransactions().toArray();
							}
						));
						break;
				case GET_CLIENT_LEDGER: objOut.
						writeObject(InvokerWrapper.catchInvocation(
								() -> {
									logger.info("Successfully completed ledgerOfClientTransfers");
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
	public void installSnapshot(byte[] bytes) {
		logger.error("Not implemented");
	}

	@Override
	public byte[] getSnapshot() {
		logger.error("Not implemented");
		return new byte[0];
	}

}