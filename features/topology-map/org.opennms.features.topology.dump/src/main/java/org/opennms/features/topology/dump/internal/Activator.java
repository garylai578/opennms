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

package org.opennms.features.topology.dump.internal;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {
	
	public int m_indent = 0;
	public String m_prefix = "";
	public BundleContext m_context;
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		try {
			println("Starting Dump Bundle");
			m_context = context;
			showBundles();
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
			println("Started Dump Bundle");
		}
	}
	
	public void indent() {
		m_indent++;
		m_prefix = null;
	}
	
	public void unindent() {
		m_indent--;
		m_prefix = null;
	}
	
	public String prefix() {
		if (m_prefix == null) {
			StringBuilder buf = new StringBuilder();
			for(int i = 0; i < m_indent; i++) {
				buf.append("\t");
			}
			m_prefix = buf.toString();
		}
		return m_prefix;
		
	}
	
	public void println(String fmt, Object... args) {
		System.err.println(String.format(prefix()+fmt, args));
	}
	
	public String state(int state) {
		switch (state) {
		case Bundle.INSTALLED: 
			return "INSTALLED";
		case Bundle.ACTIVE:
			return "ACTIVE";
		case Bundle.RESOLVED:
			return "RESOLVED";
		case Bundle.STARTING:
			return "STARTING";
		case Bundle.STOPPING:
			return "STOPPING";
		case Bundle.UNINSTALLED:
			return "UNINSTALLED";
		default:
			return "UNKNOWN STATE";
		}
	}
	
	public void showBundles() {
		Bundle[] bundles = m_context.getBundles();
		for(Bundle bundle: bundles) {
			println("Bundle: %d (%s)", bundle.getBundleId(), bundle.getSymbolicName());
			indent();
			println("State: %s", state(bundle.getState()));
			println("URL: %s", bundle.getLocation());
			println("Last Modified: %s", new Date(bundle.getLastModified()));
			println("Registered Services:");
			indent();
			showRegisteredServices(bundle);
			unindent();
			unindent();
		}

	}
	
	public String valToString(String key, ServiceReference reference) {
		Object val = reference.getProperty(key);
		return val instanceof Object[] ? Arrays.toString((Object[])val) : "" + val;
	}
	
	public void showRegisteredServices(Bundle bundle) {
		ServiceReference references[] = bundle.getRegisteredServices();
		if (references == null) {
			println("NONE");
			return;
		}
		
		for(ServiceReference reference : references) {
			Object service = m_context.getService(reference);
			println("Class: %s", service.getClass());
			println("Properties:");
			indent();
			String[] propertyKeys = reference.getPropertyKeys();
			for(String key : propertyKeys) {
				println("%s: %s", key, valToString(key, reference));
			}
			unindent();
			service = null;
			m_context.ungetService(reference);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		println("Stopping Dump Bundle");
	}
	
	

}
