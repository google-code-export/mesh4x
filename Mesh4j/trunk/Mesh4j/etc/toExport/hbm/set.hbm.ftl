<#assign value = property.value>
<#assign keyValue = value.getKey()>
<#assign elementValue = value.getElement()>
<#assign elementTag = c2h.getCollectionElementTag(property)>

	<set name="${property.name}" 
	inverse="${value.inverse?string}"
	lazy="${c2h.getCollectionLazy(value)}" 
	table="${value.collectionTable.name}"
	<#if property.cascade != "none">
        cascade="${property.cascade}"
	</#if>
	<#if c2h.hasFetchMode(property)> fetch="${c2h.getFetchMode(property)}"</#if>
	>
		<#assign metaattributable=property>
		<#include "meta.hbm.ftl">
		<#include "key.hbm.ftl">
		<#include "${elementTag}-element.hbm.ftl">
	</set>
