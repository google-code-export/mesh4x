package org.sms.exchanger.message.repository;

import java.util.Comparator;


public class MessageComparator implements Comparator<Message> {

	public static final MessageComparator INSTANCE = new MessageComparator();

	@Override
	public int compare(Message o1, Message o2) {
		if(o1.getDate() == null && o2.getDate() == null){
			return 0;
		}
		
		if(o1.getDate() == null){
			return -1;
		}
		
		if(o2.getDate() == null){
			return 1;
		}
		return o1.getDate().compareTo(o2.getDate());
	}
}
