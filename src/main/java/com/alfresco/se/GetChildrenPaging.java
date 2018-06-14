package com.alfresco.se;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * A java based web script for getting node children allowing paging.
 *
 * @author rui.fernandesg@alfresco.com
 */
public class GetChildrenPaging extends DeclarativeWebScript {
	
	private ServiceRegistry serviceRegistry;
	private NodeService nodeService;
	
    //private static Log logger = LogFactory.getLog(GetChildrenPaging.class);
    
    public void setServiceRegistry(ServiceRegistry serviceRegistry){
    	this.serviceRegistry=serviceRegistry;
    	this.nodeService=serviceRegistry.getNodeService();
    }

    protected Map<String, Object> executeImpl(
            WebScriptRequest req, Status status, Cache cache) {
    	String noderef=req.getParameter("noderef");
    	if(noderef==null)
    		throw new WebScriptException("No noderef argument provided.");
    	
    	int page=getInt(req,"page",0);

    	int perpage=getInt(req,"perpage",-1);

    	List<ChildAssociationRef> children=nodeService.getChildAssocs(new NodeRef(noderef));
    	ChildAssociationRef childs[]=children.toArray(new ChildAssociationRef[children.size()]);

    	if(perpage!=-1){
    		int offset=page*perpage;
    		int limsup=offset+perpage>childs.length?childs.length:offset+perpage;
    		childs=Arrays.copyOfRange(childs,offset,limsup);
    	}
    	
    	Map<String, Object> model = new HashMap<String, Object>();
        model.put("selection", getNodes(childs));

        return model;
    }
    
    
    private ScriptNode[] getNodes(ChildAssociationRef[] childs)
	{
    	Context context = Context.enter();
    	Scriptable scope = context.initStandardObjects();
		ScriptNode[] nodes= new ScriptNode[childs.length];
		for (int i=childs.length;i>0;i--){
			nodes[i-1]=new ScriptNode(childs[i-1].getChildRef(), serviceRegistry,scope);
		}
		return nodes;
	}


	private static int getInt(WebScriptRequest req, String param,int default_val){
    	String val=req.getParameter(param);
    	return val==null?default_val:Integer.parseInt(val);
    }
}