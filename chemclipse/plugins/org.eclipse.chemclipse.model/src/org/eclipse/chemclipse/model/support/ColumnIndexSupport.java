/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
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

	public static final String COLUMN_TYPE_CHROMATOGRAM = "{chromatogram}";
	public static final String COLUMN_TYPES = "polar" + "\n" + //
			"semi-polar" + "\n" + //
			"non-polar (apolar)" + "\n" + //
			"DB-5";
	public static final String COLUMN_TYPE_DESCRIPTION = "Select the column that shall be used to select the retention index. Use e.g.:\n" + //
			COLUMN_TYPE_CHROMATOGRAM + "\n" + //
			COLUMN_TYPES;

	public static float getRetentionIndex(List<IColumnIndexMarker> columnIndexMarkers, String searchColumn, boolean caseSensitive, boolean removeWhiteSpace) {

		return getRetentionIndex(0.0f, columnIndexMarkers, searchColumn, caseSensitive, removeWhiteSpace);
	}

	public static float getRetentionIndex(float retentionIndexTarget, List<IColumnIndexMarker> columnIndexMarkers, String searchColumn, boolean caseSensitive, boolean removeWhiteSpace) {

		float retentionIndexReference = 0.0f;
		//
		List<Float> retentionIndices = getRetentionIndices(columnIndexMarkers, searchColumn, caseSensitive, removeWhiteSpace);
		if(!retentionIndices.isEmpty()) {
			float deltaReference = Float.MAX_VALUE;
			for(float retentionIndex : retentionIndices) {
				float delta = Math.abs(retentionIndexTarget - retentionIndex);
				if(delta < deltaReference) {
					retentionIndexReference = retentionIndex;
					deltaReference = delta;
				}
			}
		}
		//
		return retentionIndexReference;
	}

	protected static String adjustValue(String value, boolean caseSensitive, boolean removeWhiteSpace) {

		value = removeWhiteSpace ? value.replace(" ", "") : value;
		return caseSensitive ? value : value.toLowerCase();
	}

	private static List<Float> getRetentionIndices(List<IColumnIndexMarker> columnIndexMarkers, String searchColumn, boolean caseSensitive, boolean removeWhiteSpace) {

		List<Float> retentionIndices = new ArrayList<>();
		//
		List<IColumnIndexMarker> columnIndexMarkersSorted = new ArrayList<>(columnIndexMarkers);
		Collections.sort(columnIndexMarkersSorted, (c1, c2) -> Float.compare(c1.getRetentionIndex(), c2.getRetentionIndex()));
		searchColumn = adjustValue(searchColumn, caseSensitive, removeWhiteSpace);
		//
		for(IColumnIndexMarker columnIndexMarker : columnIndexMarkersSorted) {
			ISeparationColumn separationColumn = columnIndexMarker.getSeparationColumn();
			String separationColumnType = adjustValue(separationColumn.getSeparationColumnType().label(), caseSensitive, removeWhiteSpace);
			String name = adjustValue(separationColumn.getName(), caseSensitive, removeWhiteSpace);
			//
			if(separationColumnType.contains(searchColumn) || name.contains(searchColumn)) {
				retentionIndices.add(columnIndexMarker.getRetentionIndex());
			}
		}
		//
		return retentionIndices;
	}
}