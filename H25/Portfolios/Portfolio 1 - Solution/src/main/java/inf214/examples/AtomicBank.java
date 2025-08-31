package inf214.examples;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bank example – third version.
 * 
 * This one uses atomic variables (<code>AtomicInteger</code>).
 * 
 * @see GenerousBank
 * @see SynchronizedBank
 */
public class AtomicBank {
	// Starting balance for the employers' accounts
	static final int STARTING_BALANCE = 1000000;
	// How much to pay in each transaction
	static final int SALARY = 1000;
	// An account for UiB
	static Account uib = new Account("uib", STARTING_BALANCE);
	// An account for HVL (for experiments)
	static Account hvl = new Account("hvl", STARTING_BALANCE);

	// Anya's bank account
	static Account anya = new Account("anya");
	// Mikhail's bank account
	static Account mikhail = new Account("mikhail");
	static List<Account> accounts = List.of(uib, hvl, anya, mikhail);

	public static void main(String[] args) throws InterruptedException {
		balanceReport();

		// Two threads, one will pay money to Anya, the other will pay Mikhail
		System.out.print("\nRunning payroll...");
		Thread payAnya = new Thread(new PayrollService(uib, anya), "payAnya");
		Thread payMikhail = new Thread(new PayrollService(uib, mikhail), "payAnyaMoar");

		// start running
		payAnya.start();
		payMikhail.start();

		// wait for threads to complete
		payAnya.join();
		payMikhail.join();

		System.out.println("done\n");

		// print results
		payrollReport();

		balanceReport();

	}

	static void balanceReport() {
		System.out.println("BALANCE REPORT");
		System.out.println("----------------------------------");
		for (Account acc : accounts) {
			System.out.printf("    %-7s %8d NOK%n", acc.name(), acc.balance());
		}
		System.out.printf("    %-7s %8d NOK%n", //
				"TOTAL", accounts.stream().mapToInt(acc -> acc.balance()).sum());

		System.out.println();
	}

	static void payrollReport() {
		System.out.println("PAYROLL REPORT");
		System.out.println("---------------------------------");
		System.out.printf("    Total paid:     %6d%n",
				(STARTING_BALANCE - uib.balance()) + (STARTING_BALANCE - hvl.balance()));
		System.out.printf("    Total recived:  %6d%n", anya.balance() + mikhail.balance());
		System.out.println();
	}

	/** 
	 * The PayrollService will transfer money from <code>fromAccount</code>
	 * to <code>toAccount</code>, until <code>fromAccount</code> is empty.
	 */
	static record PayrollService(Account fromAccount, Account toAccount) implements Runnable {

		@Override
		public void run() {
			try {
				while (fromAccount.balance() > 0) {
					try {

						fromAccount.withdraw(SALARY);
						toAccount.deposit(SALARY);
					} catch (RuntimeException e) {
						// e.printStackTrace();
					}
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}


	/**
	 * 
	 *
	 */
	static class Account {
		private AtomicInteger balance;
		private String name;

		public Account(String name) {
			this(name, 0);
		}

		public Account(String name, int balance) {
			this.name = name;
			this.balance = new AtomicInteger(balance);
		}

		public int withdraw(int amount) {
			while (true) {
				int oldBalance = balance.get();
				if (oldBalance >= amount) {
					int newBalance = oldBalance - amount;
					if (balance.compareAndSet(oldBalance, newBalance)) {
						return newBalance;
					}
				} else {
					throw new IllegalStateException("Withdrawal exceeds balance: " + balance + " < " + amount);
				}
			}
		}

		public int deposit(int amount) {
			/*
			 * if(random.nextInt(100) == 0) { throw new RuntimeException(); }
			 */
			return balance.addAndGet(amount);
		}

		public int balance() {
			return balance.get();
		}

		public String toString() {
			return "Account(\"" + name + "\", " + balance + ")";
		}

		public String name() {
			return name;
		}
	}
}
