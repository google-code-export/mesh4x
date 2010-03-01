<#assign value = property.value>
<#assign keyValue = value.getKey()>
<#assign elementValue = value.getElement()>
<#assign indexValue = value.getIndex()>
<#assign elementTag = c2h.getCollectionElementTag(property)>

<array name="${property.name}"
	<#if value.elementClassName?exists> element-class="${value.elementClassName}"</#if>
	table="${value.collectionTable.quotedName}"
	<#if property.cascade != "none">
        cascade="${property.cascade}"
	</#if>
	<#if c2h.hasFetchMode(property)> fetch="${c2h.getFetchMode(property)}"</#if>>
 	<#assign metaattributable=property>
 	<#include "meta.hbm.ftl">
    <#include "key.hbm.ftl">
    <#if c2h.isManyToOne(indexValue)>
    <list-index class="${indexValue.getReferencedEntityName()}">
    <#foreach column in indexValue.columnIterator>
    	<#include "column.hbm.ftl">
    </#foreach>  
    </list-index>
    <#else>
    <index <#foreach column in indexValue.columnIterator>column="${column.quotedName}"</#foreach>/>
    </#if>
    <#include "${elementTag}-element.hbm.ftl">
</array>
