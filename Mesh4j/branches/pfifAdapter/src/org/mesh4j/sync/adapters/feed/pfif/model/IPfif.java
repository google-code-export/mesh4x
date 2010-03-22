package org.mesh4j.sync.adapters.feed.pfif.model;

import java.util.List;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;

public interface IPfif {

	public String getPfifFeedSourceFile();
	public String getSourceFile();
	public List<Item> getNonParticipantItems();
	public ISyndicationFormat getSyndicationFormat();
	public List<PfifModel> getPfifModels();
}
