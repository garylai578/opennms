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

package org.opennms.features.vaadin.topology;

import com.vaadin.data.Item;

public class Edge{
	private String m_key;
	private Object m_itemId;
	private Item m_item;
	private Vertex m_source;
	private Vertex m_target;

	public Edge(String key, Object itemId, Item item, Vertex source, Vertex target) {; 
		m_key = key;
		m_itemId = itemId;
		m_item = item;
		m_source = source;
		m_target = target;
	}

	public Vertex getSource(){
		return m_source;
	}
	
	public String getKey() {
		return m_key;
	}

	public int getX1(){
		return getSource().getX();
	}
	
	public int getX2(){
		return getTarget().getX();
	}
	
	public int getY1(){
		return getSource().getY();
	}
	
	public int getY2(){
		return getTarget().getY();
	}
	
	public Object getItemId() {
		return m_itemId;
	}
	
	public Vertex getTarget(){
		return m_target;
	}
	
	@Override
	public String toString() {
	    return "Edge :: source: " + getSource() + " target: " + getTarget();
	}

	public Object getItem() {
		return m_item;
	}
}