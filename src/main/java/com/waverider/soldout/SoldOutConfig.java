package com.waverider.soldout;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import com.hedera.sdk.common.HederaAccountID;
import com.hedera.sdk.common.HederaKey.KeyType;
import com.hedera.sdk.cryptography.HederaCryptoKeyPair;
import com.hedera.sdk.node.HederaNode;

public class SoldOutConfig {

	// Node/Network Details
	public String nodeAddress = null;
	public int nodePort = 0;
	public long nodeAccountShard = 0;
	public long nodeAccountRealm = 0;
	public long nodeAccountNum = 0;

	// Root Account Info
	public long rootAccountNum = 0;
	String rootPubKey = "";
	String rootPrivKey = "";

	// Other Accounts
	public long[] accountNumber = null;
	public String[] accountPublicKey = null;
	public String[] accountPrivateKey = null;

	public SoldOutConfig() {
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("config.properties");
			properties.load(inputStream);

			nodeAddress = properties.getProperty("nodeaddress");
			nodePort = Integer.parseInt(properties.getProperty("nodeport"));
			nodeAccountShard = Long.parseLong(properties.getProperty("nodeAccountShard"));
			nodeAccountRealm = Long.parseLong(properties.getProperty("nodeAccountRealm"));
			nodeAccountNum = Long.parseLong(properties.getProperty("nodeAccountNum"));

			// get my public/private keys
			rootAccountNum = Long.parseLong(properties.getProperty("rootAccountNum"));
			rootPubKey = properties.getProperty("rootPubkey");
			rootPrivKey = properties.getProperty("rootPrivkey");

			int count = 0;

			while (properties.getProperty("account" + count + "_pubkey") != null) {
				count++;
			}
			accountNumber = new long[count];
			accountPublicKey = new String[count];
			accountPrivateKey = new String[count];
			for (int i = 0; i < count; i++) {
				accountNumber[i] = Long.parseLong(properties.getProperty("account" + i + "_number"));
				accountPublicKey[i] = properties.getProperty("account" + i + "_pubkey");
				accountPrivateKey[i] = properties.getProperty("account" + i + "_privatekey");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public HederaNode getHederaNode() {
		return new HederaNode(nodeAddress, nodePort, new HederaAccountID(nodeAccountShard, nodeAccountRealm, nodeAccountNum));
	}

	public HederaAccountID getRootAccountId() {
		return new HederaAccountID(nodeAccountShard, nodeAccountRealm, rootAccountNum);
	}

	public HederaCryptoKeyPair getRootKeyPair() throws InvalidKeySpecException {
		return new HederaCryptoKeyPair(KeyType.ED25519, rootPubKey, rootPrivKey);
	}
}