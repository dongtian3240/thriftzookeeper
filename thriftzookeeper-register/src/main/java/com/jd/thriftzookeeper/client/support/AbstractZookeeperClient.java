package com.jd.thriftzookeeper.client.support;


import com.jd.thriftzookeeper.client.ChildListener;
import com.jd.thriftzookeeper.client.DataListener;
import com.jd.thriftzookeeper.client.StateListener;
import com.jd.thriftzookeeper.client.ZookeeperClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractZookeeperClient<TargetChildListener,TargetDataListener> implements ZookeeperClient {
    private static final Log logger = LogFactory.getLog(AbstractZookeeperClient.class);

    private final Set<StateListener> stateListeners = new CopyOnWriteArraySet<StateListener>();

	private final ConcurrentMap<String, ConcurrentMap<ChildListener, TargetChildListener>> childListeners = new ConcurrentHashMap<String, ConcurrentMap<ChildListener, TargetChildListener>>();

    private final ConcurrentMap<String, ConcurrentMap<DataListener, TargetDataListener>> dataListeners = new ConcurrentHashMap<String, ConcurrentMap<DataListener, TargetDataListener>>();
	
    private volatile boolean closed = false;
    private String connectionString;

	public AbstractZookeeperClient(String connectionString) {
		this.connectionString = connectionString;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void create(String path, byte[] bytes,boolean ephemeral) {
		int i = path.lastIndexOf('/');
		if (i > 0) {
			create(path.substring(0, i),new byte[0], false);
		}
		if (ephemeral) {
			createEphemeral(path,bytes);
		} else {
			createPersistent(path,bytes);
		}
	}

	public void addStateListener(StateListener listener) {
		stateListeners.add(listener);
	}

	public void removeStateListener(StateListener listener) {
		stateListeners.remove(listener);
	}

	public Set<StateListener> getSessionListeners() {
		return stateListeners;
	}

	public List<String> addChildListener(String path, final ChildListener listener) {
		ConcurrentMap<ChildListener, TargetChildListener> listeners = childListeners.get(path);
		if (listeners == null) {
			childListeners.putIfAbsent(path, new ConcurrentHashMap<ChildListener, TargetChildListener>());
			listeners = childListeners.get(path);
		}
		TargetChildListener targetListener = listeners.get(listener);
		if (targetListener == null) {
			listeners.putIfAbsent(listener, createTargetChildListener(path, listener));
			targetListener = listeners.get(listener);
		}
		return addTargetChildListener(path, targetListener);
	}

	public void removeChildListener(String path, ChildListener listener) {
		ConcurrentMap<ChildListener, TargetChildListener> listeners = childListeners.get(path);
		if (listeners != null) {
			TargetChildListener targetListener = listeners.remove(listener);
			if (targetListener != null) {
				removeTargetChildListener(path, targetListener);
			}
		}
	}

    public void addDataChanges(String path,DataListener dataListener){
        ConcurrentMap<DataListener, TargetDataListener> listeners = dataListeners.get(path);
        if (listeners == null) {
            dataListeners.putIfAbsent(path, new ConcurrentHashMap<DataListener, TargetDataListener>());
            listeners = dataListeners.get(path);
        }
        TargetDataListener targetListener = listeners.get(dataListener);
        if (targetListener == null) {
            listeners.putIfAbsent(dataListener, createTargetDataListener(path, dataListener));
            targetListener = listeners.get(dataListener);
        }
        addTargetDataListener(path, targetListener);

    }

    public void removeDataChanges(String path,DataListener dataListener){
       dataListeners.remove(path);
       ConcurrentMap<DataListener, TargetDataListener> listeners = dataListeners.get(path);
       if (listeners != null) {
           TargetDataListener targetListener = listeners.remove(dataListener);
           if (targetListener != null) {
               removeTargetDataListener(path, targetListener);
           }
       }
    }

	protected void stateChanged(int state) {
		for (StateListener sessionListener : getSessionListeners()) {
			sessionListener.stateChanged(state);
		}
	}

	public void close() {
		if (closed) {
			return;
		}
		closed = true;
		try {
			doClose();
		} catch (Throwable t) {
			logger.warn(t.getMessage(), t);
		}
	}

    public abstract byte[] getNodeValue(String path);

	protected abstract void doClose();

	protected abstract void createPersistent(String path,byte[] bytes);

	protected abstract void createEphemeral(String path,byte[] bytes);

	protected abstract TargetChildListener createTargetChildListener(String path, ChildListener listener);

	protected abstract TargetDataListener createTargetDataListener(String path, DataListener listener);

	protected abstract List<String> addTargetChildListener(String path, TargetChildListener listener);

    protected abstract void addTargetDataListener(String path,TargetDataListener listener);

    protected abstract void removeTargetDataListener(String path,TargetDataListener listener);

	protected abstract void removeTargetChildListener(String path, TargetChildListener listener);

}
