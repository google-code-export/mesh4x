package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

/**
 * @author sharif
 * version 1.0, 30-03-09
 *
 */
public interface IGSElement {
	boolean isDirty();
	boolean isDeleteCandiddate();
	String getId();
	IGSElement getParent();

}
