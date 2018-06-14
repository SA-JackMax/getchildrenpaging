{
   "items":
   [
      <#list selection as item>
      {
     "nodeRef": "${item.nodeRef}",
     "name": "${item.properties.name}"
      }<#if item_has_next>,</#if>
      </#list>
   ]
}