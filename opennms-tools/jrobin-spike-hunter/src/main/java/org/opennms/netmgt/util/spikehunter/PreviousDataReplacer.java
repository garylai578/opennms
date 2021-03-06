/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2008-2012 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.util.spikehunter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreviousDataReplacer implements DataReplacer {

	public double[] replaceValues(double[] values, List<Integer> indices) {
		Set<Integer> indexSet = new HashSet<Integer>();
		for (int i : indices) {
			indexSet.add(i);
		}
		
		for (int i : indices) {
			int newIndex = walkBackwards(i, indexSet);
			if (newIndex >= 0) {
				values[i] = newIndex;
			} else {
				values[i] = Double.NaN;
			}
		}
		return null;
	}
	
	private int walkBackwards(int badIndex, Set<Integer> invalidIndices) {
		for (int i = badIndex - 1; i >= 0; i--) {
			if (! invalidIndices.contains(i)) {
				return i;
			}
		}
		return -1;
	}

}
