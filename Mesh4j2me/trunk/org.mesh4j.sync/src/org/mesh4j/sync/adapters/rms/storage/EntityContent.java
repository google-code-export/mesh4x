package org.mesh4j.sync.adapters.rms.storage;

import java.io.Writer;

import org.kxml2.kdom.Element;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.XmlHelper;

public class EntityContent extends Content {

	// MODEL VARIABLESs
	private String entityName;

	// BUSINESS METHODS
	public EntityContent(String payload, String entityName, String entityID) {
		super(payload, entityID);
		this.entityName = entityName;
	}

	public EntityContent clone() {
		return new EntityContent(new String(this.getPayload()), entityName, this.getId());
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null) {
			if (obj instanceof EntityContent) {
				EntityContent otherXmlItem = (EntityContent) obj;
				return super.equals(obj)
						&& this.getType().equals(otherXmlItem.getType());
			} else {
				return super.equals(obj);
			}
		}
		return false;
	}

	public int hashCode() {
		return super.hashCode() + this.entityName.hashCode();
	}

	public String getType() {
		return entityName;
	}

	public static EntityContent normalizeContent(IContent content, String entityNode, String entityIDNode) {
		
		if (content instanceof EntityContent) {
			EntityContent entity = (EntityContent) content;
			entity.refreshVersion();
			return entity;
		} else {
			Element payloadElement = XmlHelper.getElement(content.getPayload());
			Element entityElement = null;
			String payloadAsXml = null;
			if (entityNode.equals(payloadElement.getName())) {
				entityElement = payloadElement;
				payloadAsXml = content.getPayload();
			} else {
				entityElement = payloadElement.getElement(null, entityNode);
			}
			if (entityElement == null) {
				return null;
			} else {
				Element idElement = entityElement.getElement(null, entityIDNode);
				if (idElement == null) {
					return null;
				} else {
					if(payloadAsXml == null){
						payloadAsXml = XmlHelper.getXml(entityElement);
					}
					String entityID = idElement.getText(0);
					return new EntityContent(payloadAsXml, entityNode, entityID);
				}
			}
		}
	}

	public void addToFeedPayload(Writer writer, Item item, ISyndicationFormat format) throws Exception{
		format.addFeedItemTitleElement(writer, this.entityName);
		format.addFeedItemDescriptionElement(writer, "Entity id: " + this.getId() + " version: " + this.getVersion());
		format.addFeedItemPayloadElement(writer, this.getPayload());
	}
}
