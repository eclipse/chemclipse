/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
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

import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.SeparationColumnIndices;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public class RetentionIndexMap {

	private ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
	private TreeMap<Integer, Integer> retentionIndexMap = new TreeMap<>(); // Retention Index, Retention Time

	public RetentionIndexMap() {

	}

	public RetentionIndexMap(IChromatogram<?> chromatogram) {

		update(chromatogram);
	}

	public void update(IChromatogram<?> chromatogram) {

		/*
		 * Map the available retention indices.
		 */
		TreeMap<Integer, Integer> treeMap = new TreeMap<>();
		if(chromatogram != null) {
			for(IScan scan : chromatogram.getScans()) {
				int retentionIndex = Math.round(scan.getRetentionIndex());
				updateIndexMap(treeMap, retentionIndex, scan.getRetentionTime());
			}
		}
		/*
		 * Mappings
		 */
		mapSeparationColumnIndices(treeMap);
		mapRetentionIndices(separationColumnIndices);
	}

	public ISeparationColumnIndices getSeparationColumnIndices() {

		return separationColumnIndices;
	}

	/**
	 * Returns the retention time for a given index or -1 (RetentionIndexMath.RETENTION_TIME_MISSING) if none is available.
	 * 
	 * @param retentionIndex
	 * @return int
	 */
	public int getRetentionTime(int retentionIndex) {

		return RetentionIndexMath.calculateRetentionTime(retentionIndex, retentionIndexMap);
	}

	/**
	 * Returns the retention index for a given time or 0 if none is available.
	 * 
	 * @param retentionTime
	 * @return int
	 */
	public float getRetentionIndex(int retentionTime) {

		return RetentionIndexMath.calculateRetentionIndex(retentionTime, separationColumnIndices);
	}

	private void updateIndexMap(TreeMap<Integer, Integer> treeMap, int retentionIndex, int retentionTime) {

		if(retentionIndex > 0) {
			if(!treeMap.containsKey(retentionIndex)) {
				treeMap.put(retentionIndex, retentionTime);
			}
		}
	}

	private void mapSeparationColumnIndices(TreeMap<Integer, Integer> treeMap) {

		/*
		 * Clear and populate the retention index entries.
		 */
		separationColumnIndices.clear();
		/*
		 * First Entry
		 */
		Entry<Integer, Integer> firstEntry = treeMap.firstEntry();
		separationColumnIndices.put(new RetentionIndexEntry(firstEntry.getValue(), firstEntry.getKey(), "Start"));
		/*
		 * Check C1 - C99
		 */
		for(int i = 1; i < 99; i++) {
			//
			int retentionIndex = i * 100;
			String alkane = RetentionIndexMath.ALKANE_PREFIX + i;
			Entry<Integer, Integer> floorEntry = treeMap.floorEntry(retentionIndex);
			Entry<Integer, Integer> higherEntry = treeMap.higherEntry(retentionIndex + 100);
			//
			if(floorEntry != null && higherEntry != null) {
				if(floorEntry.getKey() % 100 == 0) {
					separationColumnIndices.put(new RetentionIndexEntry(floorEntry.getValue(), retentionIndex, alkane));
				} else {
					float retentionIndexLow = floorEntry.getKey();
					float retentionIndexHigh = higherEntry.getKey();
					int retentionTimeLow = floorEntry.getValue();
					int retentionTimeHigh = higherEntry.getValue();
					int retentionTime = RetentionIndexMath.calculateRetentionTime(retentionIndex, retentionIndexLow, retentionIndexHigh, retentionTimeLow, retentionTimeHigh);
					separationColumnIndices.put(new RetentionIndexEntry(retentionTime, retentionIndex, alkane));
				}
			}
		}
		/*
		 * Last Entry
		 */
		Entry<Integer, Integer> lastEntry = treeMap.lastEntry();
		separationColumnIndices.put(new RetentionIndexEntry(lastEntry.getValue(), lastEntry.getKey(), "Stop"));
	}

	private void mapRetentionIndices(ISeparationColumnIndices separationColumnIndices) {

		retentionIndexMap.clear();
		for(IRetentionIndexEntry retentionIndexEntry : separationColumnIndices.values()) {
			retentionIndexMap.put(Math.round(retentionIndexEntry.getRetentionIndex()), retentionIndexEntry.getRetentionTime());
		}
	}
}