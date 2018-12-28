/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.baseline;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.BaselineIsNotDefinedException;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;

/**
 * This class represents the baseline model of the current chromatogram.
 * 
 * @author eselmeister
 */
public class BaselineModel implements IBaselineModel {

	@SuppressWarnings("rawtypes")
	private transient IChromatogram chromatogram;
	/*
	 * The start retention time is the key.
	 */
	private NavigableMap<Integer, IBaselineSegment> baselineSegments;
	private float defaultBackgroundAbundance;
	private boolean interpolate;

	@SuppressWarnings("rawtypes")
	public BaselineModel(IChromatogram chromatogram) {

		this.chromatogram = chromatogram;
		this.baselineSegments = new TreeMap<Integer, IBaselineSegment>();
		this.defaultBackgroundAbundance = 0f;
		this.interpolate = false;
	}

	@SuppressWarnings("rawtypes")
	public BaselineModel(IChromatogram chromatogram, float defaultBackgroundAbundance) {

		this.chromatogram = chromatogram;
		this.baselineSegments = new TreeMap<Integer, IBaselineSegment>();
		this.defaultBackgroundAbundance = defaultBackgroundAbundance;
		if(Double.isNaN(defaultBackgroundAbundance)) {
			this.interpolate = true;
		} else {
			this.interpolate = false;
		}
	}

	// --------------------------------------------IBaselineModel
	@Override
	public void addBaseline(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance, boolean validate) {

		if(startRetentionTime > stopRetentionTime) {
			return;
		}
		if(validate) {
			addBaselineChecked(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance);
		} else {
			addBaselineUnchecked(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance);
		}
	}

	private void removeBaselineSegments(int startRetentionTime, int stopRetentionTime) {

		removeMiddleSegments(startRetentionTime, stopRetentionTime);
		/*
		 * part which should be remove lie in interval one segment
		 */
		cutSegmentInTwoParts(startRetentionTime, stopRetentionTime);
		/*
		 * 
		 */
		cutSegmentBeginningPart(startRetentionTime, stopRetentionTime);
		/*
		 * 
		 */
		cutSegmentEndingPart(startRetentionTime, stopRetentionTime);
	}

	/**
	 * remove middle segments
	 * 
	 */
	private void removeMiddleSegments(int startRetentionTime, int stopRetentionTime) {

		SortedMap<Integer, IBaselineSegment> sortedMap = baselineSegments.subMap(startRetentionTime, stopRetentionTime);
		Set<Integer> keyToRemove = new HashSet<>();
		//
		for(Entry<Integer, IBaselineSegment> entry : sortedMap.entrySet()) {
			int stopRetentionTimeRemoveSegment = entry.getValue().getStopRetentionTime();
			if(stopRetentionTimeRemoveSegment <= stopRetentionTime) {
				keyToRemove.add(entry.getKey());
			}
		}
		/**
		 * remove key
		 */
		keyToRemove.forEach(k -> baselineSegments.remove(k));
	}

	/**
	 * Cut the beginning part of an existing segment.
	 * 
	 */
	private void cutSegmentBeginningPart(int startRetentionTime, int stopRetentionTime) {

		Map.Entry<Integer, IBaselineSegment> cuttingSegmentEntry = baselineSegments.floorEntry(startRetentionTime);
		cuttingSegmentEntry = baselineSegments.floorEntry(stopRetentionTime);
		if(cuttingSegmentEntry != null) {
			IBaselineSegment cuttingSegment = cuttingSegmentEntry.getValue();
			int x0 = cuttingSegment.getStartRetentionTime();
			int x1 = cuttingSegment.getStopRetentionTime();
			if(startRetentionTime <= x0 && x0 <= stopRetentionTime && stopRetentionTime < x1) {
				baselineSegments.remove(cuttingSegmentEntry.getKey());
				int partSegmentStartRetentionTime = stopRetentionTime + 1;
				int partSegmentStopRetentionTime = x1;
				float partSegmentStartAbundance = cuttingSegment.getBackgroundAbundance(partSegmentStartRetentionTime);
				float partSegmentStopAbundance = cuttingSegment.getStopBackgroundAbundance();
				/*
				 * 
				 */
				addBaselineUnchecked(partSegmentStartRetentionTime, partSegmentStopRetentionTime, partSegmentStartAbundance, partSegmentStopAbundance);
			}
		}
	}

	/**
	 * Cut the ending part of an existing segment.
	 * 
	 */
	private void cutSegmentEndingPart(int startRetentionTime, int stopRetentionTime) {

		Map.Entry<Integer, IBaselineSegment> cuttingSegmentEntry = baselineSegments.floorEntry(startRetentionTime);
		cuttingSegmentEntry = baselineSegments.floorEntry(startRetentionTime);
		if(cuttingSegmentEntry != null) {
			IBaselineSegment cuttingSegment = cuttingSegmentEntry.getValue();
			int x0 = cuttingSegment.getStartRetentionTime();
			int x1 = cuttingSegment.getStopRetentionTime();
			if(x0 < startRetentionTime && startRetentionTime <= x1 && x1 <= stopRetentionTime) {
				baselineSegments.remove(cuttingSegmentEntry.getKey());
				int partSegmentStartRetentionTime = x0;
				int partSegmentStopRetentionTime = startRetentionTime - 1;
				float partSegmentStartAbundance = cuttingSegment.getStartBackgroundAbundance();
				float partSegmentStopAbundance = cuttingSegment.getBackgroundAbundance(partSegmentStopRetentionTime);
				/*
				 * 
				 */
				addBaselineUnchecked(partSegmentStartRetentionTime, partSegmentStopRetentionTime, partSegmentStartAbundance, partSegmentStopAbundance);
			}
		}
	}

	/**
	 * Cut an existing segment into two peaces.
	 */
	private void cutSegmentInTwoParts(int startRetentionTime, int stopRetentionTime) {

		Map.Entry<Integer, IBaselineSegment> cuttingSegmentEntry = baselineSegments.floorEntry(startRetentionTime);
		if(cuttingSegmentEntry != null) {
			IBaselineSegment cuttingSegment = cuttingSegmentEntry.getValue();
			int x0 = cuttingSegment.getStartRetentionTime();
			int x1 = cuttingSegment.getStopRetentionTime();
			if(x0 < startRetentionTime && stopRetentionTime < x1) {
				baselineSegments.remove(cuttingSegmentEntry.getKey());
				/*
				 * add first segment
				 */
				int firstPartSegmentStartRetentionTime = x0;
				int firstPartSegmentStopRetentionTime = startRetentionTime - 1;
				float firstPartSegmentStartAbundance = cuttingSegment.getStartBackgroundAbundance();
				float firstPartSegmentStopAbundance = cuttingSegment.getBackgroundAbundance(firstPartSegmentStopRetentionTime);
				/*
				 * 
				 */
				addBaselineUnchecked(firstPartSegmentStartRetentionTime, firstPartSegmentStopRetentionTime, firstPartSegmentStartAbundance, firstPartSegmentStopAbundance);
				/*
				 * add second segment
				 */
				int secondPartSegmentStartRetentionTime = stopRetentionTime + 1;
				int secondPartSegmentStopRetentionTime = x1;
				float secondPartSegmentStartAbundance = cuttingSegment.getBackgroundAbundance(secondPartSegmentStartRetentionTime);
				float secondPartSegmentStopAbundance = cuttingSegment.getStopBackgroundAbundance();
				/*
				 * 
				 */
				addBaselineUnchecked(secondPartSegmentStartRetentionTime, secondPartSegmentStopRetentionTime, secondPartSegmentStartAbundance, secondPartSegmentStopAbundance);
			}
		}
	}

	@Override
	public void removeBaseline() {

		clearBaseline();
	}

	@Override
	@Deprecated
	public float getBackgroundAbundance(int retentionTime) {

		float defaultBackgroundAbundance = 0f;
		if(retentionTime < chromatogram.getStartRetentionTime() || retentionTime > chromatogram.getStopRetentionTime()) {
			return defaultBackgroundAbundance;
		}
		return getBackground(retentionTime, defaultBackgroundAbundance);
	}

	@Override
	public float getBackground(int retentionTime) {

		return getBackground(retentionTime, defaultBackgroundAbundance);
	}

	private float getBackground(int retentionTime, float defaultAbudance) {

		if(baselineSegments.isEmpty() || retentionTime < baselineSegments.firstKey() || retentionTime > baselineSegments.lastEntry().getValue().getStopRetentionTime()) {
			return defaultAbudance;
		}
		/*
		 * Get the correct baseline segment and calculate the abundance.
		 */
		IBaselineSegment floorSegment = baselineSegments.floorEntry(retentionTime).getValue();
		int stopRetentionTime = floorSegment.getStopRetentionTime();
		if(retentionTime <= stopRetentionTime) {
			return floorSegment.getBackgroundAbundance(retentionTime);
		} else {
			if(interpolate) {
				IBaselineSegment ceilSegment = baselineSegments.ceilingEntry(retentionTime).getValue();
				Point p1 = new Point(floorSegment.getStopRetentionTime(), floorSegment.getStopBackgroundAbundance());
				Point p2 = new Point(ceilSegment.getStartRetentionTime(), ceilSegment.getStartBackgroundAbundance());
				return (float)Equations.createLinearEquation(p1, p2).calculateY(retentionTime);
			} else {
				return defaultAbudance;
			}
		}
	}

	@Override
	public float getBackgroundNotNaN(int retentionTime) throws BaselineIsNotDefinedException {

		float background = getBackground(retentionTime);
		if(background != Float.NaN) {
			return background;
		} else {
			throw new BaselineIsNotDefinedException();
		}
	}

	@Override
	public IBaselineModel makeDeepCopy() {

		IBaselineModel baselineModelCopy = new BaselineModel(chromatogram, defaultBackgroundAbundance);
		int startRT;
		int stopRT;
		float startAB;
		float stopAB;
		for(IBaselineSegment segment : baselineSegments.values()) {
			startRT = segment.getStartRetentionTime();
			stopRT = segment.getStopRetentionTime();
			startAB = segment.getStartBackgroundAbundance();
			stopAB = segment.getStopBackgroundAbundance();
			baselineModelCopy.addBaseline(startRT, stopRT, startAB, stopAB, false);
		}
		return baselineModelCopy;
	}

	// --------------------------------------------IBaselineModel
	// ------------------------------------------private methods
	private void addBaselineUnchecked(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance) {

		IBaselineSegment segment;
		if(startRetentionTime == stopRetentionTime) {
			segment = new BaselinePoint(startRetentionTime);
			segment.setStartBackgroundAbundance(startBackgroundAbundance);
		} else {
			segment = new BaselineSegment(startRetentionTime, stopRetentionTime);
			segment.setStartBackgroundAbundance(startBackgroundAbundance);
			segment.setStopBackgroundAbundance(stopBackgroundAbundance);
		}
		baselineSegments.put(segment.getStartRetentionTime(), segment);
	}

	private void addBaselineChecked(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance) {

		removeBaselineSegments(startRetentionTime, stopRetentionTime);
		addBaselineUnchecked(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance);
	}

	/**
	 * Clear the baseline segments and create a new tree map to store them.
	 */
	private void clearBaseline() {

		if(baselineSegments != null) {
			baselineSegments.clear();
		}
	}
	// ------------------------------------------private methods

	@Override
	public void removeBaseline(int startRetentionTime, int stopRetentionTime) {

		if(startRetentionTime > stopRetentionTime) {
			return;
		}
		removeBaselineSegments(startRetentionTime, stopRetentionTime);
	}
}
