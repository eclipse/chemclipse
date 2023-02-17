/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.identifier.IColumnIndexMarker;

public class ColumnIndexSupport {

	public static float getRetentionIndex(List<IColumnIndexMarker> columnIndexMarkers, String searchColumn, boolean caseSensitive, boolean removeWhiteSpace) {

		float retentionIndex = 0.0f;
		List<IColumnIndexMarker> columnIndexMarkersSorted = new ArrayList<>(columnIndexMarkers);
		Collections.sort(columnIndexMarkersSorted, (c1, c2) -> Float.compare(c1.getRetentionIndex(), c2.getRetentionIndex()));
		searchColumn = adjustValue(searchColumn, caseSensitive, removeWhiteSpace);
		//
		exitloop:
		for(IColumnIndexMarker columnIndexMarker : columnIndexMarkersSorted) {
			ISeparationColumn separationColumn = columnIndexMarker.getSeparationColumn();
			String separationColumnType = adjustValue(separationColumn.getSeparationColumnType().label(), caseSensitive, removeWhiteSpace);
			String name = adjustValue(separationColumn.getName(), caseSensitive, removeWhiteSpace);
			//
			if(separationColumnType.contains(searchColumn) || name.contains(searchColumn)) {
				retentionIndex = columnIndexMarker.getRetentionIndex();
				break exitloop;
			}
		}
		//
		return retentionIndex;
	}

	protected static String adjustValue(String value, boolean caseSensitive, boolean removeWhiteSpace) {

		value = removeWhiteSpace ? value.replace(" ", "") : value;
		return caseSensitive ? value : value.toLowerCase();
	}
}