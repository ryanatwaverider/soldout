// (C) Copyright Waverider LLC, 2018
package com.waverider.soldout;

import java.util.HashMap;
import java.util.Map.Entry;

import com.hedera.sdk.account.HederaAccount;
import com.hedera.sdk.common.HederaAccountID;
import com.hedera.sdk.common.HederaPrecheckResult;
import com.hedera.sdk.common.HederaTransactionReceipt;
import com.hedera.sdk.common.HederaTransactionStatus;
import com.hedera.sdk.common.Utilities;
import com.hedera.sdk.transaction.HederaTransactionResult;

public class ResetBalances {

	public static void main(String[] args) throws Exception {
		HashMap<Long, Long> balances = new HashMap<Long, Long>();
		System.out.println("Resetting Demo Account Balances");
		SoldOutConfig config = new SoldOutConfig();
		for (int i = 0; i < config.accountNumber.length; i++) {
			long balance = config.getAccount(i).getBalance();
			System.out.println(String.format("Account %d (%d) has Balance %d", i,config.getAccountId(i).accountNum, balance));
			Thread.sleep(1001);
			long diff = balance - config.initialBalance;
			if (diff != 0) {
				long amount;
				HederaAccount fromAccount;
				HederaAccountID toAccount;
				if(diff > 0){
					amount = diff;
					fromAccount = config.getAccount(i);
					toAccount = config.getRootAccountId();
				} else {
					amount = -diff;
					fromAccount = config.getRootAccount();
					toAccount = config.getAccountId(i);
				}
				HederaTransactionResult transferResult = fromAccount.send(toAccount, amount);  				
				if (transferResult.getPrecheckResult() != HederaPrecheckResult.OK) {
					throw new Exception("Failed with getPrecheckResult:" + transferResult.getPrecheckResult().toString());
				}
				HederaTransactionReceipt receipt = Utilities.getReceipt(transferResult.hederaTransactionID,fromAccount.txQueryDefaults.node);
				if (receipt.transactionStatus != HederaTransactionStatus.SUCCESS) {
					throw new Exception("Failed with transaction result: " + receipt.transactionStatus);
				}
				Thread.sleep(1001);
				balance = config.getAccount(i).getBalance();
			}
			balances.put((long) i, balance);
			System.out.println(String.format("Account %d now has Balance %d", i,balance));
			Thread.sleep(1001);
		}
		for (Entry<Long, Long> entry : balances.entrySet()) {
			System.out.println(String.format("Account %d now has Balance %d", entry.getKey(), entry.getValue()));
		}
		System.out.print("Done.");
	}
}
