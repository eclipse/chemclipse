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
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

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

	@SuppressWarnings("rawtypes")
	public BaselineModel(IChromatogram chromatogram) {

		this.chromatogram = chromatogram;
		this.baselineSegments = new TreeMap<Integer, IBaselineSegment>();
	}

	// --------------------------------------------IBaselineModel
	@Override
	public void addBaseline(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance, boolean validate) {

		if(validate) {
			addBaselineChecked(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance);
		} else {
			addBaselineUnchecked(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance);
		}
	}

	@Override
	public void removeBaseline(int startRetentionTime, int stopRetentionTime) {

		if(startRetentionTime >= stopRetentionTime) {
			return;
		}
		SortedMap<Integer, IBaselineSegment> sortedMap = baselineSegments.subMap(startRetentionTime, stopRetentionTime);
		Set<Integer> keyToRemove = new HashSet<>();
		for(Entry<Integer, IBaselineSegment> entry : sortedMap.entrySet()) {
			int stopRetentionTimeRemoveSegment = entry.getValue().getStopRetentionTime();
			if(stopRetentionTimeRemoveSegment <= stopRetentionTime) {
				keyToRemove.add(entry.getKey());
			}
		}
		keyToRemove.forEach(k -> baselineSegments.remove(k));
		Map.Entry<Integer, IBaselineSegment> floorStartRetentionTime = baselineSegments.floorEntry(startRetentionTime);
		if(floorStartRetentionTime != null) {
			IBaselineSegment floorBaselineSegment = floorStartRetentionTime.getValue();
			if(startRetentionTime < floorBaselineSegment.getStopRetentionTime()) {
				Point p1 = new Point(floorBaselineSegment.getStartRetentionTime(), floorBaselineSegment.getStartBackgroundAbundance());
				Point p2 = new Point(floorBaselineSegment.getStopRetentionTime(), floorBaselineSegment.getStopBackgroundAbundance());
				LinearEquation eq = Equations.createLinearEquation(p1, p2);
				float abundance = (float)eq.calculateY(startRetentionTime);
				floorBaselineSegment.setStopRetentionTime(startRetentionTime);
				floorBaselineSegment.setStopBackgroundAbundance(abundance);
			}
		}
		Map.Entry<Integer, IBaselineSegment> floorStopRetentionTime = baselineSegments.floorEntry(stopRetentionTime);
		if(floorStopRetentionTime != null) {
			IBaselineSegment floorBaselineSegment = floorStopRetentionTime.getValue();
			if(floorBaselineSegment.getStartRetentionTime() < stopRetentionTime) {
				Point p1 = new Point(floorBaselineSegment.getStartRetentionTime(), floorBaselineSegment.getStartBackgroundAbundance());
				Point p2 = new Point(floorBaselineSegment.getStopRetentionTime(), floorBaselineSegment.getStopBackgroundAbundance());
				LinearEquation eq = Equations.createLinearEquation(p1, p2);
				float abundance = (float)eq.calculateY(stopRetentionTime);
				floorBaselineSegment.setStartRetentionTime(stopRetentionTime);
				floorBaselineSegment.setStartBackgroundAbundance(abundance);
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

		if(retentionTime < chromatogram.getStartRetentionTime() || retentionTime > chromatogram.getStopRetentionTime()) {
			return 0.0f;
		}
		/*
		 * Get the correct baseline segment and calculate the abundance.
		 */
		Map.Entry<Integer, IBaselineSegment> entry = baselineSegments.floorEntry(retentionTime);
		if(entry == null) {
			return 0.0f;
		} else {
			IBaselineSegment segment = entry.getValue();
			Point p1 = new Point(segment.getStartRetentionTime(), segment.getStartBackgroundAbundance());
			Point p2 = new Point(segment.getStopRetentionTime(), segment.getStopBackgroundAbundance());
			LinearEquation eq = Equations.createLinearEquation(p1, p2);
			return (float)eq.calculateY(retentionTime);
		}
	}

	@Override
	public float getBackground(int retentionTime, float defaultAbudance) {

		if(baselineSegments.isEmpty() || retentionTime < baselineSegments.firstKey() || retentionTime > baselineSegments.lastEntry().getValue().getStopRetentionTime()) {
			return defaultAbudance;
		}
		/*
		 * Get the correct baseline segment and calculate the abundance.
		 */
		IBaselineSegment floorSegment = baselineSegments.floorEntry(retentionTime).getValue();
		int stopRetentionTime = floorSegment.getStopRetentionTime();
		if(retentionTime <= stopRetentionTime) {
			Point p1 = new Point(floorSegment.getStartRetentionTime(), floorSegment.getStartBackgroundAbundance());
			Point p2 = new Point(floorSegment.getStopRetentionTime(), floorSegment.getStopBackgroundAbundance());
			LinearEquation eq = Equations.createLinearEquation(p1, p2);
			return (float)eq.calculateY(retentionTime);
		} else {
			return defaultAbudance;
		}
	}

	@Override
	public float getBackground(int retentionTime) throws BaselineIsNotDefinedException {

		float background = getBackground(retentionTime, Float.NaN);
		if(background != Float.NaN) {
			return background;
		} else {
			throw new BaselineIsNotDefinedException();
		}
	}

	@Override
	public IBaselineModel makeDeepCopy() {

		IBaselineModel baselineModelCopy = new BaselineModel(chromatogram);
		int startRT;
		int stopRT;
		float startAB;
		float stopAB;
		for(IBaselineSegment segment : baselineSegments.values()) {
			startRT = segment.getStartRetentionTime();
			stopRT = segment.getStopRetentionTime();
			startAB = segment.getStartBackgroundAbundance();
			stopAB = segment.getStopBackgroundAbundance();
			baselineModelCopy.addBaseline(startRT, stopRT, startAB, stopAB, true);
		}
		return baselineModelCopy;
	}

	// --------------------------------------------IBaselineModel
	// ------------------------------------------private methods
	private void addBaselineUnchecked(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance) {

		IBaselineSegment segment = new BaselineSegment(startRetentionTime, stopRetentionTime);
		segment.setStartBackgroundAbundance(startBackgroundAbundance);
		segment.setStopBackgroundAbundance(stopBackgroundAbundance);
		baselineSegments.put(segment.getStartRetentionTime(), segment);
	}

	private void addBaselineChecked(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance) {

		if(startRetentionTime >= stopRetentionTime) {
			return;
		}
		SortedMap<Integer, IBaselineSegment> soretedMap = baselineSegments.subMap(startRetentionTime, stopRetentionTime);
		Set<Integer> keyToRemove = new HashSet<>();
		for(Entry<Integer, IBaselineSegment> entry : soretedMap.entrySet()) {
			int stopRetentionTimeRemoveSegment = entry.getValue().getStopRetentionTime();
			if(stopRetentionTimeRemoveSegment <= stopRetentionTime) {
				keyToRemove.add(entry.getKey());
			}
		}
		keyToRemove.forEach(k -> baselineSegments.remove(k));
		Map.Entry<Integer, IBaselineSegment> floorEntry = baselineSegments.floorEntry(startRetentionTime);
		if(floorEntry != null) {
			IBaselineSegment floorBaselineSegment = floorEntry.getValue();
			if(startRetentionTime <= floorBaselineSegment.getStopRetentionTime()) {
				floorBaselineSegment.setStopRetentionTime(startRetentionTime);
				floorBaselineSegment.setStopBackgroundAbundance(startBackgroundAbundance);
			}
		}
		Map.Entry<Integer, IBaselineSegment> floorStopRetentionTime = baselineSegments.floorEntry(stopRetentionTime);
		if(floorStopRetentionTime != null) {
			IBaselineSegment floorBaselineSegment = floorStopRetentionTime.getValue();
			if(floorBaselineSegment.getStartRetentionTime() < stopRetentionTime) {
				floorBaselineSegment.setStartRetentionTime(stopRetentionTime);
				floorBaselineSegment.setStartBackgroundAbundance(stopBackgroundAbundance);
			}
		}
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
}
