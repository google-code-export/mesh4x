package org.mesh4j.ektoo.ui.conflicts;

import org.mesh4j.sync.model.Item;

public interface IWinnerUI {

	String getSyncId();

	void setAsConflict();

	Item getWinner();

}
