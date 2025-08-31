package inf214.examples;

import java.util.List;

/**
 * Bank example – first attempt.
 * 
 * This one doesn't use any synchronization.
 * 
 * @see AtomicBank
 * @see SynchronizedBank
 */
public class GenerousBank {
	static final int STARTING_BALANCE = 1000000;
	static final int SALARY = 1000;
	static Account uib = new Account("uib", STARTING_BALANCE);
	static Account hvl = new Account("hvl", STARTING_BALANCE);

	static Account anya = new Account("anya");
	static Account mikhail = new Account("mikhail");
	static List<Account> accounts = List.of(uib, hvl, anya, mikhail);

	public static void main(String[] args) throws InterruptedException {
		balanceReport();

		System.out.print("\nRunning payroll...");
		Thread payAnya = new Thread(new PayrollService(uib, anya), "payAnya");
		Thread payMikhail = new Thread(new PayrollService(uib, mikhail), "payAnyaMoar");

		payAnya.start();
		payMikhail.start();

		payAnya.join();
		payMikhail.join();

		System.out.println("done\n");

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

	static record PayrollService(Account fromAccount, Account toAccount) implements Runnable {

		@Override
		public void run() {
			try {
				while (fromAccount.balance() > 0) {
					try {
						fromAccount.withdraw(SALARY);
						toAccount.deposit(SALARY);
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	static class Account {
		private int balance = 0;
		private String name;

		public Account(String name) {
			this.name = name;
		}

		public Account(String name, int balance) {
			this.name = name;
			this.balance = balance;
		}

		public int withdraw(int amount) {
			if (balance >= amount) {
				balance = balance - amount;
				return balance;
			} else {
				throw new IllegalStateException("Withdrawal exceeds balance: " + balance + " < " + amount);
			}
		}

		public int deposit(int amount) {
			balance = balance + amount;
			return balance;
		}

		public int balance() {
			return balance;
		}

		public String toString() {
			return "Account(\"" + name + "\", " + balance + ")";
		}

		public String name() {
			return name;
		}
	}
}
