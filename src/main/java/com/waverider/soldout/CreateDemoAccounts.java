// (C) Copyright Waverider LLC, 2018
package com.waverider.soldout;

import java.security.spec.InvalidKeySpecException;

import com.hedera.sdk.account.HederaAccount;
import com.hedera.sdk.account.HederaAccountCreateDefaults;
import com.hedera.sdk.common.HederaDuration;
import com.hedera.sdk.common.HederaPrecheckResult;
import com.hedera.sdk.common.HederaTransactionAndQueryDefaults;
import com.hedera.sdk.common.HederaTransactionReceipt;
import com.hedera.sdk.common.HederaTransactionStatus;
import com.hedera.sdk.common.Utilities;
import com.hedera.sdk.common.HederaKey.KeyType;
import com.hedera.sdk.cryptography.HederaCryptoKeyPair;
import com.hedera.sdk.transaction.HederaTransactionResult;

public class CreateDemoAccounts {

	public static void main(String[] args) throws Exception {
		System.out.println("Creating Demo Account IDs");
		SoldOutConfig config = new SoldOutConfig();
		for (int i = 0; i < config.accountNumber.length; i++) {
			if(config.accountNumber[i]> 0){
				continue;
			}
			HederaTransactionAndQueryDefaults txQueryDefaults = new HederaTransactionAndQueryDefaults();
			txQueryDefaults.memo = "Creating Demo Account";
			txQueryDefaults.node = config.getHederaNode();
			txQueryDefaults.payingAccountID = config.getRootAccountId();
			txQueryDefaults.payingKeyPair = config.getRootKeyPair();
			txQueryDefaults.transactionValidDuration = new HederaDuration(120, 0);
			HederaAccount myNewAccount = new HederaAccount();
			myNewAccount.txQueryDefaults = txQueryDefaults;
			HederaCryptoKeyPair newAccountKey = new HederaCryptoKeyPair(KeyType.ED25519, config.accountPublicKey[i], config.accountPrivateKey[i]);
			HederaAccountCreateDefaults accountCreateDefaults = new HederaAccountCreateDefaults();
			accountCreateDefaults.autoRenewPeriodSeconds = 86400;
			accountCreateDefaults.autoRenewPeriodNanos = 0;
			HederaTransactionResult createResult = myNewAccount.create(config.nodeAccountShard, config.nodeAccountRealm,
					newAccountKey.getPublicKey(), newAccountKey.getKeyType(), config.initialBalance,
					accountCreateDefaults);
			HederaPrecheckResult precheckResult = createResult.getPrecheckResult();
			if (precheckResult == HederaPrecheckResult.OK) {
				HederaTransactionReceipt receipt = Utilities.getReceipt(createResult.hederaTransactionID,
						myNewAccount.txQueryDefaults.node);
				if (receipt.transactionStatus == HederaTransactionStatus.SUCCESS) {

					System.out
							.println(String.format("===>Your new account for ID=%d number is %d", i, receipt.accountID.accountNum));
					Thread.sleep(1000);
				} else {
					throw new Exception("Failed to create account: " + receipt);
				}
			} else {
				throw new Exception("Failed to create account: " + precheckResult);
			}
		}
		System.out.print("Done.");
	}
}
