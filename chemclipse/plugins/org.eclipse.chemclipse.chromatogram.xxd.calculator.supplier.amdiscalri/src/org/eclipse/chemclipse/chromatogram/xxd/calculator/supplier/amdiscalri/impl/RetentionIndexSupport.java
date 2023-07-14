/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexMarker;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.core.IChromatogram;

public class RetentionIndexSupport {

	public static void transferRetentionIndexMarker(RetentionIndexMarker retentionIndexMarkerSource, RetentionIndexMarker retentionIndexMarkerSink) {

		retentionIndexMarkerSink.clear();
		retentionIndexMarkerSink.addAll(retentionIndexMarkerSource);
	}

	public static void transferRetentionIndexMarker(ISeparationColumnIndices separationColumnIndicesSource, RetentionIndexMarker retentionIndexMarkerSink) {

		retentionIndexMarkerSink.clear();
		for(IRetentionIndexEntry retentionIndexEntry : separationColumnIndicesSource.values()) {
			int retentionTime = retentionIndexEntry.getRetentionTime();
			float retentionIndex = retentionIndexEntry.getRetentionIndex();
			String name = retentionIndexEntry.getName();
			retentionIndexMarkerSink.add(new RetentionIndexEntry(retentionTime, retentionIndex, name));
		}
	}

	public static void transferRetentionIndexMarker(RetentionIndexMarker retentionIndexMarkerSource, ISeparationColumnIndices separationColumnIndicesSink) {

		separationColumnIndicesSink.clear();
		for(IRetentionIndexEntry retentionIndexEntry : retentionIndexMarkerSource) {
			int retentionTime = retentionIndexEntry.getRetentionTime();
			float retentionIndex = retentionIndexEntry.getRetentionIndex();
			String name = retentionIndexEntry.getName();
			separationColumnIndicesSink.put(new RetentionIndexEntry(retentionTime, retentionIndex, name));
		}
	}

	public static RetentionIndexMarker getRetentionIndexMarker(ISeparationColumnIndices separationColumnIndices, IChromatogram<?> chromatogram, boolean extrapolateLeft, boolean extrapolateRight) {

		RetentionIndexMarker retentionIndexMarker = new RetentionIndexMarker();
		transferRetentionIndexMarker(separationColumnIndices, retentionIndexMarker);
		//
		return getRetentionIndexMarker(retentionIndexMarker, chromatogram, extrapolateLeft, extrapolateRight);
	}

	public static RetentionIndexMarker getRetentionIndexMarker(RetentionIndexMarker retentionIndexMarker, IChromatogram<?> chromatogram, boolean extrapolateLeft, boolean extrapolateRight) {

		if(retentionIndexMarker != null && retentionIndexMarker.size() >= 2) {
			if(RetentionIndexSupport.extrapolateData(extrapolateLeft, extrapolateRight)) {
				/*
				 * Get the start/stop marker
				 */
				Optional<IRetentionIndexEntry> markerStart = retentionIndexMarker.stream().min((m1, m2) -> Integer.compare(m1.getRetentionTime(), m2.getRetentionTime()));
				Optional<IRetentionIndexEntry> markerStop = retentionIndexMarker.stream().max((m1, m2) -> Integer.compare(m1.getRetentionTime(), m2.getRetentionTime()));
				/*
				 * Calculate the missing ranges.
				 */
				RetentionIndexExtrapolator retentionIndexExtrapolator = new RetentionIndexExtrapolator();
				retentionIndexExtrapolator.extrapolateMissingAlkaneRanges(retentionIndexMarker);
				/*
				 * Constraint Remove (Left)
				 */
				if(!extrapolateLeft && markerStart.isPresent()) {
					List<IRetentionIndexEntry> removeEntries = new ArrayList<>();
					int retentionTimeStart = markerStart.get().getRetentionTime();
					for(IRetentionIndexEntry retentionIndexEntry : retentionIndexMarker) {
						if(retentionIndexEntry.getRetentionTime() < retentionTimeStart) {
							removeEntries.add(retentionIndexEntry);
						}
					}
					retentionIndexMarker.removeAll(removeEntries);
				}
				/*
				 * Constraint Remove (Right)
				 */
				if(!extrapolateRight && markerStop.isPresent()) {
					List<IRetentionIndexEntry> removeEntries = new ArrayList<>();
					int retentionTimeStop = markerStop.get().getRetentionTime();
					for(IRetentionIndexEntry retentionIndexEntry : retentionIndexMarker) {
						if(retentionIndexEntry.getRetentionTime() > retentionTimeStop) {
							removeEntries.add(retentionIndexEntry);
						}
					}
					retentionIndexMarker.removeAll(removeEntries);
				}
			}
		}
		/*
		 * Return the initial marker.
		 */
		return retentionIndexMarker;
	}

	public static boolean extrapolateData(boolean extrapolateLeft, boolean extrapolateRight) {

		return extrapolateLeft == true || extrapolateRight == true;
	}
}