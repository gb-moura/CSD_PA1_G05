package Server.Replication;

import Server.Repositories.BlockRepository;
import bftsmart.reconfiguration.util.RSAKeyLoader;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.tom.util.TOMUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.glassfish.jersey.internal.guava.Maps;
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

import java.util.List;
import java.util.Map;

@PropertySource("classpath:application.properties")
@Component
public class Server extends DefaultSingleRecoverable implements Runnable{
	private final Logger logger =
			LoggerFactory.getLogger(Server.class);
	private static final String DEFAULT_KEY_CONFIG = "";
	private int counter = 0;
	private RSAKeyLoader keyLoader;
	private RSAKeyLoader keyFund;

	@Value("${replica.id}")
	private int ID;

	@Qualifier("ImpWallet")
	@Autowired
	private WalletController walletController;

	@Autowired
	MongoTemplate mongoTemplate;



	@PostConstruct
	public void init() throws Exception {
		mongoTemplate.getDb().drop();
		new ServiceReplica(ID, this, this);
		this.keyLoader = new RSAKeyLoader(ID, DEFAULT_KEY_CONFIG);
		this.keyFund = new RSAKeyLoader(4,DEFAULT_KEY_CONFIG);
		byte[] empty = keyFund.loadPublicKey().getEncoded();
		Map.Entry<byte[],String> clientEntry =  Maps.immutableEntry(empty, "FUND");
		walletController.createClient(clientEntry);
		walletController.createGenesisBlock();
	}

	@Override
	public void run() {
		mongoTemplate.getDb().drop();
		new ServiceReplica(ID, this, this);
	}

	@Override
	public byte[] appExecuteOrdered(byte[] command, MessageContext messageContext) {

		return sendFullReply(invokeCommand(command));
	}

	@Override
	public byte[] appExecuteUnordered(byte[] command, MessageContext messageContext) {
		return sendFullReply(invokeCommand(command));
	}

	private byte[] invokeCommand(byte[] command) {
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
			 ObjectInput objIn = new ObjectInputStream(byteIn);
			 ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			 ObjectOutput objOut = new ObjectOutputStream(byteOut)) {
			 Path path = (Path)objIn.readObject();
			logger.info(String.format("Searching for %s to invoke.", path));

			switch(path){
				case INIT:
					String r = String.valueOf(walletController.createClient((Map.Entry<byte[], String>) objIn.readObject()));
					logger.info("Successfully created client");
					objOut.writeObject(r);
					counter++;
					return new Gson().toJson(r).getBytes();
				case OBTAIN_COINS:
					String ans = String.valueOf(walletController.obtainCoins((Transaction) objIn.readObject()));
					logger.info("Successfully completed createMoney");
					objOut.writeObject(ans);
					counter++;
					return new Gson().toJson(ans).getBytes();
				case TRANSFER_MONEY:
					String a =	String.valueOf(walletController.transferMoney((Transaction)objIn.readObject()));
					logger.info("Successfully completed transferMoney");
					objOut.writeObject(a);
					counter++;
					return new Gson().toJson(a).getBytes();
				case GET_MONEY:
					String result = String.valueOf(walletController.currentAmount((String)objIn.readObject()));
					objOut.writeObject(result);
					logger.info("Successfully completed currentAmount");
					return new Gson().toJson(result).getBytes();
				case GET_LEDGER_GLOBAL:
					List<Transaction> res = walletController.ledgerOfGlobalTransactions((String)objIn.readObject());
					objOut.writeObject( res);
					logger.info("Successfully completed ledgerOfClientTransfers");
					return new Gson().toJson(res).getBytes();
				case GET_CLIENT_LEDGER:
					List<Transaction> res1 = walletController.ledgerOfClientTransfers((String)objIn.readObject());
					objOut.writeObject(res1);
					logger.info("Successfully completed ledgerOfClientTransfers");
					return new Gson().toJson(res1).getBytes();
				case OBTAIN_LAST_MINED_BLOCK:
					Block b = walletController.obtainLastMinedBlock();
					objOut.writeObject(b);
					logger.info("Successfully completed obtainLastMinedBlock");
					return new Gson().toJson(b).getBytes();
				case PICK_NOT_MIN_TRANS:
					Block pick = walletController.pickNotMineratedTransactions((String)objIn.readObject());
					objOut.writeObject(pick);
					logger.info("Successfully completed pickNotMineratedTransactions");
					Gson g = new Gson();
					String x = g.toJson(pick);
					return x.getBytes();
				case MINE_BLOCK:
					boolean mined = walletController.sendMinedBlock((Map.Entry<String, Block>) objIn.readObject());
					objOut.writeObject(mined);
					logger.info("Successfully completed mineBlock");
					return new Gson().toJson(mined).getBytes();
				case TRANSFER_MONEY_SMRCONTRACT:
					String answ = String.valueOf(walletController.transferMoneyWithSmr((SmartContract )objIn.readObject()));
					logger.info("Successfully completed transferMoney with Smart Contract");
					objOut.writeObject(answ);
					counter++;
					return new Gson().toJson(answ).getBytes();
				case TRANSFER_MONEY_PRIVACY:
					String answe = String.valueOf(walletController.transferMoneyWithPrivacy((Transaction)objIn.readObject()));
					logger.info("Successfully completed transferMoney with privacy");
					objOut.writeObject(answe);
					counter++;
					return new Gson().toJson(answe).getBytes();
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


	private byte[] sendFullReply(byte[] serverReply) {
		logger.info("sendFullReply: reply len=" + serverReply.length);

		try (ByteArrayOutputStream bS = new ByteArrayOutputStream();
			 DataOutputStream dS = new DataOutputStream(bS)) {

			dS.writeInt(serverReply.length);
			dS.write(serverReply);
			byte[] signedAnswer = sign(serverReply);
			logger.info("tryToSendFullReply: signed reply len="+signedAnswer.length);
			dS.writeInt(signedAnswer.length);
			dS.write(signedAnswer);
			return bS.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}



	private byte[] sign(byte[] serverReply) throws Exception {
		return TOMUtil.signMessage(keyLoader.loadPrivateKey(), serverReply);
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