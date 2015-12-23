package com.reed.zk;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZkUtils {

	private CountDownLatch connectedSignal = new CountDownLatch(1);
	/**
	 * server列表, 以逗号分割
	 */
	protected static String hosts = "172.28.5.132:3181";
	/**
	 * 连接的超时时间, 毫秒
	 */
	private final int SESSION_TIMEOUT = 5000;

	protected static ZooKeeper zk;

	/**
	 * 连接zookeeper server
	 */
	public ZooKeeper connect() throws Exception {
		zk = new ZooKeeper(hosts, SESSION_TIMEOUT, new ConnWatcher());
		// 等待连接完成
		connectedSignal.await();
		return zk;
	}

	/**
	 * 
	 * @author Kiven
	 *
	 */
	public class ConnWatcher implements Watcher {
		public void process(WatchedEvent event) {
			// 连接建立, 回调process接口时, 其event.getState()为KeeperState.SyncConnected
			if (event.getState() == KeeperState.SyncConnected) {
				// 放开闸门, wait在connect方法上的线程将被唤醒
				connectedSignal.countDown();
			}
		}
	}

	/**
	 * 以下为各个参数的详细说明: path. znode的路径. data. 与znode关联的数据. acl. 指定权限信息, 如果不想指定权限,
	 * 可以传入Ids.OPEN_ACL_UNSAFE. 指定znode类型. CreateMode是一个枚举类, 从中选择一个成员传入即可.
	 */

	/**
	 * 创建持久化节点
	 */
	public void create(String Path, byte[] data) throws Exception {
		zk.create(Path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("创建节点:" + Path);
		System.out.println("=================");
	}

	/**
	 * 
	 * 获取节点信息
	 *
	 * @param path
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void getChild(String path) throws KeeperException,
			InterruptedException {
		try {
			List<String> list = zk.getChildren(path, false);
			if (list.isEmpty()) {
				System.out.println(path + "中没有节点");
			} else {
				System.out.println(path + "中存在节点");
				for (String child : list) {
					System.out.println("节点为：" + child);
				}
			}
		} catch (KeeperException.NoNodeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置节点数据
	 * 
	 * @throws Exception
	 */
	public void setData(String path, String data) throws Exception {
		zk.setData(path, data.getBytes(), -1);
		System.out.println("set Data:" + "testSetData");
	}

	/**
	 * 读取节点数据
	 * 
	 * @throws Exception
	 */
	public void getData(String nodePath) throws Exception {
		System.out.println("get Data:");
		zk.getData(nodePath, false, null);
	}

	/**
	 * 删除节点
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void delete(String path) throws Exception {
		System.out.println("删除节点:" + path);
		// 如果版本号与znode的版本号不一致，将无法删除，是一种乐观加锁机制；如果将版本号设置为-1，不会去检测版本，直接删除；
		zk.delete(path, -1);
	}

	/**
	 * 关闭连接
	 */
	public void close() {
		try {
			zk.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
