/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.vaadin.topology.gwt.client;

import org.opennms.features.vaadin.topology.gwt.client.d3.D3;
import org.opennms.features.vaadin.topology.gwt.client.d3.D3Behavior;
import org.opennms.features.vaadin.topology.gwt.client.d3.Func;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class GWTVertex extends JavaScriptObject {

    protected GWTVertex() {};
    
    public final native String getId()/*-{
        return this.id;
    }-*/;
    
    public final native int getX()/*-{
        return this.x;
    }-*/;
    
    public final native int getY()/*-{
        return this.y;
    }-*/;
    
    public final native void setSelected(boolean selected) /*-{
        this.selected = selected;
    }-*/;
    
    public final native boolean isSelected() /*-{
        return this.selected;
    }-*/;
    
    public static native GWTVertex create(String id, int x, int y) /*-{
        return {"id":id, "x":x, "y":y, "selected":false, "actions":[], "iconUrl":"", "semanticZoomLevel":0, "group":null};
    }-*/;

    public final native void setX(int newX) /*-{
        this.x = newX;
    }-*/;

    public final native void setY(int newY) /*-{
        this.y = newY;
    }-*/;
    
    public final native JsArrayString actionKeys() /*-{
    	return this.actions;
    }-*/;
    
    public final native JsArrayString actionKeys(JsArrayString keys) /*-{
    	this.actions = keys;
    	return this.actions;
    }-*/;
    
    public final String getTooltipText() {
        return "id: " + getId() + " SZL: " + getSemanticZoomLevel() + " Group: " + (getParent() == null ? "null" : getParent().getId());
    }
    
    
    public final native int getSemanticZoomLevel() /*-{
		return this.semanticZoomLevel;
	}-*/;

	public final void setActionKeys(String[] keys) {
    	JsArrayString actionKeys = actionKeys(newStringArray());
    	for(String key : keys) {
    		actionKeys.push(key);
    	}
    }

	private JsArrayString newStringArray() {
		return JsArrayString.createArray().<JsArrayString>cast();
	}
    
    public final String[] getActionKeys() {
    	JsArrayString actionKeys = actionKeys();
    	String[] keys = new String[actionKeys.length()];
    	for(int i = 0; i < keys.length; i++) {
    		keys[i] = actionKeys.get(i);
    	}
    	return keys;
    }
    
    public final native String getIconUrl() /*-{
        return this.iconUrl;
    }-*/;
    
    public final native void setIcon(String iconUrl) /*-{
        this.iconUrl = iconUrl;
    }-*/;

    static Func<String, GWTVertex> selectedFill() {
    	return new Func<String, GWTVertex>(){
    
    		public String call(GWTVertex vertex, int index) {
    			return vertex.isSelected() ? "blue" : "black";
    		}
    	};
    }
    
    protected static Func<String, GWTVertex> selectionFilter() {
        return new Func<String, GWTVertex>(){

            public String call(GWTVertex vertex, int index) {
                return vertex.isSelected() ? "1" : "0";
            }
            
        };
    }

    static Func<String, GWTVertex> getTranslation() {
    	return new Func<String, GWTVertex>() {
    
    		public String call(GWTVertex datum, int index) {
    			return "translate( " + datum.getX() + "," + datum.getY() + ")";
    		}
    		
    	};
    }
    
    static Func<String, GWTVertex> getIconPath(){
        return new Func<String, GWTVertex>(){

            public String call(GWTVertex datum, int index) {
                if(datum.getIconUrl().equals("")) {
                    return GWT.getModuleBaseURL() + "topologywidget/images/server.png";
                }else {
                    return datum.getIconUrl();
                }
                
            }
        };
    }
    
    public static D3Behavior draw() {
        return new D3Behavior() {

            @Override
            public D3 run(D3 selection) {
                return selection.attr("transform", GWTVertex.getTranslation()).select(".highlight").attr("opacity", GWTVertex.selectionFilter());
            }
        };
    }
    
    public static D3Behavior create() {
        return new D3Behavior() {

            @Override
            public D3 run(D3 selection) {
                D3 vertex = selection.append("g").attr("class", "little");
                vertex.attr("opacity",1e-6);
                vertex.style("cursor", "pointer");
                
                vertex.append("rect").attr("class", "highlight").attr("fill", "yellow").attr("x", "-26px").attr("y", "-26px").attr("width", "52px").attr("height", "52px").attr("opacity", 0);
                
                vertex.append("svg:image").attr("xlink:href", getIconPath())
                      .attr("x", "-24px")
                      .attr("y", "-24px")
                      .attr("width", "48px")
                      .attr("height", "48px");
                
                vertex.call(draw());
                
                return vertex;
            }
        };
    }

	public final native void setParent(GWTGroup group) /*-{
		this.group = group;
	}-*/;
	
	public final native GWTGroup getParent() /*-{
		return this.group;
	}-*/;

	public final native void setSemanticZoomLevel(int semanticZoomLevel) /*-{
		this.semanticZoomLevel = semanticZoomLevel;
	}-*/;

	public final GWTVertex getDisplayVertex(int semanticZoomLevel) {
		
		if(getParent() == null || getSemanticZoomLevel() <= semanticZoomLevel) {
			return this;
		}else {
			return getParent().getDisplayVertex(semanticZoomLevel);
		}
		
	}

}
