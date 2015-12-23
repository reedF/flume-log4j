package com.reed.test.zk;

import org.apache.log4j.PropertyConfigurator;
import org.apache.zookeeper.ZooKeeper;

import com.reed.zk.ZkUtils;

public class Test {

	protected static ZooKeeper zk;
	private static String nodePath = "/Test-zk/rrr";

	// static String data = "a very long string about data to set to zookeeper";
	static int threads = 10; // 线程数
	static int runs = 1000; // 迭代次数
	static int start = 0; // none

	static int size = 1024; // 写入数据的大小,单位：字节
	static byte[] testdata; // 测试数据

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("target/test-classes/log4j.properties");
		// 生成写入的数据，大小size字节
		testdata = new byte[size];
		for (int i = 0; i < size; i++) {
			testdata[i] = 'A';
		}

		ZkUtils test = new ZkUtils();
		// 连接
		zk = test.connect();
		if (zk.exists(nodePath, null) == null) {
			test.create(nodePath, null);
		}

		WorkerStat[] statArray = new WorkerStat[threads];
		Thread[] threadArray = new Thread[threads];

		WorkerStat mainStat = new WorkerStat();
		mainStat.runs = runs * threads;

		long begin = System.currentTimeMillis();
		for (int i = 0; i < threads; i++) {
			statArray[i] = new WorkerStat();
			statArray[i].start = start + i * runs;
			statArray[i].runs = runs;
			threadArray[i] = new SetterThread(statArray[i]);
			threadArray[i].start();
		}
		for (int i = 0; i < threads; i++) {
			threadArray[i].join();
		}
		mainStat.setterTime = System.currentTimeMillis() - begin;

		begin = System.currentTimeMillis();
		for (int i = 0; i < threads; i++) {
			threadArray[i] = new GetterThread(statArray[i]);
			threadArray[i].start();
		}
		for (int i = 0; i < threads; i++) {
			threadArray[i].join();
		}
		mainStat.getterTime = System.currentTimeMillis() - begin;

		WorkerStat totalStat = new WorkerStat();

		System.out.println("Test over!!");
		System.out.println("Thread(" + threads
				+ ")\truns\tset time(ms)\tget time(ms)");

		for (int i = 0; i < threads; i++) {
			totalStat.runs = totalStat.runs + statArray[i].runs;
			totalStat.setterTime = totalStat.setterTime
					+ statArray[i].setterTime;
			totalStat.getterTime = totalStat.getterTime
					+ statArray[i].getterTime;
		}
		System.out.println("Total\t\t" + totalStat.runs + "\t"
				+ totalStat.setterTime + "\t\t" + totalStat.getterTime);
		System.out.println("Avg\t\t" + runs + "\t" + totalStat.setterTime
				/ threads + "\t\t" + totalStat.getterTime / threads);
		System.out.println("TPS\t\t\t" + 1000 * totalStat.runs
				/ totalStat.setterTime + "\t\t" + 1000 * totalStat.runs
				/ totalStat.getterTime);
		System.out.println("\nMain\t\t" + mainStat.runs + "\t"
				+ mainStat.setterTime + "\t\t" + mainStat.getterTime);
		System.out.println("TPS\t\t" + 1000 * mainStat.runs
				/ mainStat.setterTime + "\t\t" + 1000 * mainStat.runs
				/ mainStat.getterTime);

		test.delete(nodePath);
		test.close();
	}

	private static class WorkerStat {
		public int start, runs;
		public long setterTime, getterTime;

		public WorkerStat() {
			start = runs = 0;
			setterTime = getterTime = 0;
		}
	}

	private static class SetterThread extends Thread {
		private WorkerStat stat;

		SetterThread(WorkerStat stat) {
			this.stat = stat;
		}

		public void run() {
			long begin = System.currentTimeMillis();
			for (int i = stat.start; i < stat.start + stat.runs; i++) {
				// 写入节点数据
				try {
					zk.setData(nodePath, testdata, -1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			long end = System.currentTimeMillis();
			stat.setterTime = end - begin;
		}
	}

	private static class GetterThread extends Thread {
		private WorkerStat stat;

		GetterThread(WorkerStat stat) {
			this.stat = stat;
		}

		public void run() {
			long begin = System.currentTimeMillis();
			for (int i = stat.start; i < stat.start + stat.runs; i++) {
				// 读取节点数据
				try {
					zk.getData(nodePath, false, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			long end = System.currentTimeMillis();
			stat.getterTime = end - begin;
		}
	}

}