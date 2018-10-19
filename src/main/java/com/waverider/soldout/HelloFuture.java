/**
 * (C) Copyright Waverider LLC, 2018
 */
package com.waverider.soldout;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import com.hedera.sdk.account.HederaAccount;
import com.hedera.sdk.account.HederaAccountCreateDefaults;
import com.hedera.sdk.common.HederaAccountID;
import com.hedera.sdk.common.HederaDuration;
import com.hedera.sdk.common.HederaPrecheckResult;
import com.hedera.sdk.common.HederaKey.KeyType;
import com.hedera.sdk.common.HederaTransactionAndQueryDefaults;
import com.hedera.sdk.common.HederaTransactionReceipt;
import com.hedera.sdk.common.HederaTransactionStatus;
import com.hedera.sdk.common.Utilities;
import com.hedera.sdk.cryptography.HederaCryptoKeyPair;
import com.hedera.sdk.node.HederaNode;
import com.hedera.sdk.transaction.HederaTransactionResult;

import org.apache.commons.codec.net.QCodec;

public final class HelloFuture {

	public static void main (String[] args) {

		SoldOutConfig config = new SoldOutConfig();

		// setup defaults for transactions and Queries 
		HederaTransactionAndQueryDefaults txQueryDefaults = new HederaTransactionAndQueryDefaults();
		
		// default memo to attach to transactions
		txQueryDefaults.memo = "Hello Future";
		
		// setup the node we're communicating with from the properties loaded above
		txQueryDefaults.node = config.getHederaNode();
		
		// setup the paying account ID (got from the properties loaded above)
		txQueryDefaults.payingAccountID = config.getRootAccountId();

		// setup the paying key pair (got from properties loaded above)
		try {
			txQueryDefaults.payingKeyPair = config.getRootKeyPair();
		} catch (InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// define the valid duration for the transactions (seconds, nanos)
		txQueryDefaults.transactionValidDuration = new HederaDuration(120, 0);

		// instantiate a new account object
		HederaAccount myNewAccount = new HederaAccount();
		
		// set its default Transaction and Query parameters
		myNewAccount.txQueryDefaults = txQueryDefaults;
		
		// create a new key for my new account
		HederaCryptoKeyPair newAccountKey = new HederaCryptoKeyPair(KeyType.ED25519);
		
		// now, setup default for account creation 
		HederaAccountCreateDefaults defaults = new HederaAccountCreateDefaults();
		// auto renew period in seconds and nanos
		defaults.autoRenewPeriodSeconds = 86400;
		defaults.autoRenewPeriodNanos = 0;
//			defaults.maxReceiveProxyFraction = 0;
//			defaults.proxyFraction = 1;
//			defaults.receiveRecordThreshold = Long.MAX_VALUE;
//			defaults.receiverSignatureRequired = false;
//			defaults.sendRecordThreshold = Long.MAX_VALUE;
		
		try {
			// send create account transaction
			long shardToCreateIn = 0;
			long realmToCreateIn = 0;
			long startingBalance = 10000;
			// let's create the account
			HederaTransactionResult createResult = myNewAccount.create(shardToCreateIn
								, realmToCreateIn
								, newAccountKey.getPublicKey()
								, newAccountKey.getKeyType()
								, startingBalance, defaults);
			
			// was it successful ?
			HederaPrecheckResult precheckResult = createResult.getPrecheckResult();
			if ( precheckResult == HederaPrecheckResult.OK) {
				
				// yes, get a receipt for the transaction
				HederaTransactionReceipt receipt = Utilities.getReceipt(myNewAccount.hederaTransactionID, myNewAccount.txQueryDefaults.node);
				
				// was that successful ?
				if (receipt.transactionStatus == HederaTransactionStatus.SUCCESS) {
					// yes, get the new account number from the receipt
					myNewAccount.accountNum = receipt.accountID.accountNum;
					// and print it out
					System.out.println(String.format("===>Your new account number is %d", myNewAccount.accountNum));
					
					// get balance
					myNewAccount.txQueryDefaults.payingAccountID = myNewAccount.getHederaAccountID();
					myNewAccount.txQueryDefaults.payingKeyPair = newAccountKey;
					
					myNewAccount.getBalance();
					
					HederaAccountID toAccountID = config.getRootAccountId();
					myNewAccount.send(toAccountID, 20);
					
					Thread.sleep(1000);
					
					myNewAccount.getBalance();
					
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
