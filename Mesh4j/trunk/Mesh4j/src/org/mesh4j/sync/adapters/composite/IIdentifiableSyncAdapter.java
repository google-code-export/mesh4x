package org.mesh4j.sync.adapters.composite;

import org.mesh4j.sync.ISyncAdapter;

public interface IIdentifiableSyncAdapter extends ISyncAdapter {

	String getType();

	String getIdName();
}
