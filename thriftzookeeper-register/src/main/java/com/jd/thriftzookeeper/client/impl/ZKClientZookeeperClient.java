package com.jd.thriftzookeeper.client.impl;

import com.github.zkclient.*;
import com.github.zkclient.exception.ZkNoNodeException;
import com.github.zkclient.exception.ZkNodeExistsException;
import com.jd.thriftzookeeper.client.ChildListener;
import com.jd.thriftzookeeper.client.DataListener;
import com.jd.thriftzookeeper.client.StateListener;
import com.jd.thriftzookeeper.client.support.AbstractZookeeperClient;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

public class ZKClientZookeeperClient extends AbstractZookeeperClient<IZkChildListener,IZkDataListener> {

	private final ZkClient client;

	private volatile KeeperState state = KeeperState.SyncConnected;

	public ZKClientZookeeperClient(String connectionString) {
		super(connectionString);
        ZkConnection zkConnection = new ZkConnection(connectionString);
		client = new ZkClient(zkConnection,5*1000);
		client.subscribeStateChanges(new IZkStateListener() {
			public void handleStateChanged(KeeperState state) throws Exception {
				ZKClientZookeeperClient.this.state = state;
				if (state == KeeperState.Disconnected) {
					stateChanged(StateListener.DISCONNECTED);
				} else if (state == KeeperState.SyncConnected) {
					stateChanged(StateListener.CONNECTED);
				}
			}
			public void handleNewSession() throws Exception {
				stateChanged(StateListener.RECONNECTED);
			}
		});
	}

    @Override
    public byte[] getNodeValue(String path) {
        //节点不存在时,返回空
        return client.readData(path,true);
    }

    public void createPersistent(String path,byte[] bytes) {
		try {
			client.createPersistent(path,bytes);
		} catch (ZkNodeExistsException e) {
		}
	}

    @Override
    public ZkClient getZkClient(){
        return client;
    }
	public void createEphemeral(String path,byte[] bytes) {
		try {
			client.createEphemeral(path,bytes);
		} catch (ZkNodeExistsException e) {
		}
	}

    @Override
    public ZooKeeper getZookeeper() {
        return client.getZooKeeper();
    }

    public void delete(String path) {
		try {
			client.delete(path);
		} catch (ZkNoNodeException e) {
		}
	}

	public List<String> getChildren(String path) {
		try {
			return client.getChildren(path);
        } catch (ZkNoNodeException e) {
            return null;
        }
	}

	public boolean isConnected() {
		return state == KeeperState.SyncConnected;
	}

	public void doClose() {
		client.close();
	}

	public IZkChildListener createTargetChildListener(String path, final ChildListener listener) {
		return new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChilds)
					throws Exception {
				listener.childChanged(parentPath, currentChilds);
			}
		};
	}

    public IZkDataListener createTargetDataListener(String path, final DataListener listener){
        return  new IZkDataListener(){

            @Override
            public void handleDataChange(String dataPath, byte[] data) throws Exception {
                listener.handleDataChange(dataPath,data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                listener.handleDataDeleted(dataPath);
            }
        };
    }

    public void addTargetDataListener(String path,final IZkDataListener listener){
        client.subscribeDataChanges(path,listener);
    }

    public void removeTargetDataListener(String path, IZkDataListener listener){
        client.unsubscribeDataChanges(path,listener);
    }

	public List<String> addTargetChildListener(String path, final IZkChildListener listener) {
		return client.subscribeChildChanges(path, listener);
	}

	public void removeTargetChildListener(String path, IZkChildListener listener) {
		client.unsubscribeChildChanges(path,  listener);
	}

}
