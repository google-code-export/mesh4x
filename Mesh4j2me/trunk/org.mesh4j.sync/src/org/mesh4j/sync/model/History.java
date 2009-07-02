package org.mesh4j.sync.model;

import java.util.Date;

import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;

public class History {

	// MODEL VARIABLES
	private Date when;
	private String by;
	private int sequence;

	// BUSINESS METHODS
	public History(String by) {
		this(by, null, 1);
	}

	public History(Date when) {
		this(null, when, 1);
	}

	public History(String by, Date when) {
		this(by, when, 1);
	}
	
	public History(String by, Date when, int sequence) {

		if ((by == null || by.length() == 0) && when == null)
			Guard.throwsArgumentException("Arg_EitherWhenOrByMustBeSpecified");
		if (sequence <= 0)
			Guard.throwsArgumentException("Arg_SequenceMustBeGreaterThanZero");

		this.by = by;
		if (when != null) {
			this.when = DateHelper.normalize(when);
		}
		this.sequence = sequence;
	}

	public Date getWhen() {
		return when;
	}

	public String getBy() {
		return by;
	}

	public int getSequence() {
		return sequence;
	}

	public boolean IsSubsumedBy(History history) {
		History Hx = this;
		History Hy = history;

		if (Hx.getBy() != null && Hx.getBy().trim().length() > 0) {
			return Hx.getBy().equals(Hy.getBy())
					&& Hy.getSequence() >= Hx.getSequence();
		} else if (Hy.getBy() == null || Hy.getBy().trim().length() == 0) {
			boolean okWhen =
				(Hx.getWhen() == null && Hy.getWhen() == null) ||
				(Hx.getWhen() != null && Hx.getWhen().equals(Hy.getWhen()));
			return okWhen && Hx.getSequence() == Hy.getSequence();
		}
		return false;
	}

	public History clone() {
		return new History(by, when, sequence);
	} 
	
	public int hashCode(){
		int hash = (by == null || by.trim().length() == 0) ? 0 : this.by.hashCode();
		hash = hash ^ ((this.when == null) ? 0 : this.when.hashCode());
		hash = hash ^ new Integer(sequence).hashCode();

		return hash;
	}
	
	public boolean equals(Object obj){
		
		if (this == obj) return true;
		if (obj != null)
		{
			if(obj instanceof History){
				History h2 = (History) obj;
				boolean okBy = 
					(this.getBy() == null && h2.getBy() == null) ||
					(this.getBy() != null && this.getBy().equals(h2.getBy()));
				boolean okWhen =
					(this.getWhen() == null && h2.getWhen() == null) ||
					(this.getWhen() != null && this.getWhen().equals(h2.getWhen()));
				
				return okBy && okWhen && this.getSequence() == h2.getSequence();
			}
		}
		return false;
		
	}
}
