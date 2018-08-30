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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IChromatogram;
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
	private NavigableMap<Integer, IBaselineSegment> baselineSegments = null;

	@SuppressWarnings("rawtypes")
	public BaselineModel(IChromatogram chromatogram) {
		this.chromatogram = chromatogram;
		clearBaseline();
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
		/*
		 * Adds a new baseline segment with the abundance 0, which means, that
		 * the actual baseline between the start and stop retention time will be
		 * removed.
		 */
		addBaseline(startRetentionTime, stopRetentionTime, 0.0f, 0.0f, true);
	}

	@Override
	public void removeBaseline() {

		clearBaseline();
	}

	@Override
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
		IBaselineSegment segment;
		/*
		 * Use lists to store the segments that will be added or removed.
		 */
		List<IBaselineSegment> addSegments = new ArrayList<IBaselineSegment>();
		List<Integer> removeSegments = new ArrayList<Integer>();
		/*
		 * Start and stop retention times of the segment that should be added to
		 * the model.
		 */
		int start = startRetentionTime;
		int stop = stopRetentionTime;
		/*
		 * That is the new segment, that will be added to the model.
		 */
		segment = new BaselineSegment(start, stop);
		segment.setStartBackgroundAbundance(startBackgroundAbundance);
		segment.setStopBackgroundAbundance(stopBackgroundAbundance);
		addSegments.add(segment);
		/*
		 * Iterate through all existing segments and add, modify or mark
		 * segments to be deleted if necessary.<br/> The code is not optimal but
		 * it works correctly.<br/> Further modifications may help to optimize
		 * this class.
		 */
		for(Integer key : baselineSegments.keySet()) {
			segment = baselineSegments.get(key);
			int x0 = segment.getStartRetentionTime();
			int x1 = segment.getStopRetentionTime();
			/*
			 * Do nothing if the segment does not overlap the segment to be
			 * included.
			 */
			if((start < x0 && stop < x0) || (start > x1 && stop > x1)) {
				continue;
			}
			/*
			 * If the segment is totally hidden by the segment to be inserted,
			 * remove it.
			 */
			if(start < x0 && stop > x1) {
				removeSegments.add(key);
				continue;
			}
			/*
			 * Cut the beginning part of an existing segment.
			 */
			if(stop > x0 && stop < x1 && start < x0) {
				cutSegmentsBeginningPart(stop, segment, removeSegments, addSegments, key);
			}
			/*
			 * Cut the ending part of an existing segment.
			 */
			if(start < x1 && stop >= x1) {
				cutSegmentsEndingPart(start, segment, removeSegments, key);
			}
			/*
			 * Cut an existing segment into two peaces.
			 */
			if((start > x0 && start < x1) && (stop > x0 && stop < x1)) {
				cutExistingSegmentInTwoParts(start, stop, segment, removeSegments, addSegments, key);
			}
		}
		/*
		 * Remove the no longer used segments.
		 */
		for(Integer key : removeSegments) {
			baselineSegments.remove(key);
		}
		/*
		 * Add the new segments.
		 */
		for(IBaselineSegment addSegment : addSegments) {
			baselineSegments.put(addSegment.getStartRetentionTime(), addSegment);
		}
	}

	/*
	 * Cut the beginning part of an existing segment.
	 */
	private void cutSegmentsBeginningPart(int stop, IBaselineSegment segment, List<Integer> removeSegments, List<IBaselineSegment> addSegments, int key) {

		/*
		 * Adjust the abundance and the start retention time. Use (startRT =
		 * stop -1) so that the segments will not overlap.
		 */
		int startRT = stop + 1;
		if(startRT == 0) {
			removeSegments.add(key);
		} else {
			/*
			 * Remove the actual segment, because the mapping (start retention
			 * time, segment) is not valid any more.
			 */
			removeSegments.add(key);
			/*
			 * Adjust the segment and add it to the list.
			 */
			segment.setStartBackgroundAbundance(getBackgroundAbundance(startRT));
			segment.setStartRetentionTime(startRT);
			addSegments.add(segment);
		}
	}

	/*
	 * Cut the ending part of an existing segment.
	 */
	private void cutSegmentsEndingPart(int start, IBaselineSegment segment, List<Integer> removeSegments, int key) {

		/*
		 * Adjust the abundance and the start retention time.<br/> Use (stopRT =
		 * start -1) so that the segments will not overlap.
		 */
		int stopRT = start - 1;
		if(stopRT == 0) {
			removeSegments.add(key);
		} else {
			segment.setStopBackgroundAbundance(getBackgroundAbundance(stopRT));
			segment.setStopRetentionTime(stopRT);
		}
	}

	/*
	 * Cut an existing segment into two peaces.
	 */
	private void cutExistingSegmentInTwoParts(int start, int stop, IBaselineSegment segment, List<Integer> removeSegments, List<IBaselineSegment> addSegments, int key) {

		/*
		 * Adjust the abundance and the start retention time of the segment
		 * divided in two pieces.<br/> Use (startRT = stop +1) so that the
		 * segments will not overlap.
		 */
		int startRT = stop + 1;
		if(startRT == chromatogram.getStopRetentionTime()) {
			removeSegments.add(key);
		} else {
			IBaselineSegment segmentII = new BaselineSegment(startRT, segment.getStopRetentionTime());
			segmentII.setStartBackgroundAbundance(getBackgroundAbundance(startRT));
			segmentII.setStopBackgroundAbundance(segment.getStopBackgroundAbundance());
			addSegments.add(segmentII);
		}
		/*
		 * The adjustment of the first part needs to be done after adding the
		 * second, freshly created segment as you need the correct
		 * abundance.<br/> Adjust the abundance and the start retention
		 * time.<br/> Use (stopRT = start -1) so that the segments will not
		 * overlap.
		 */
		int stopRT = start - 1;
		if(stopRT == 0) {
			removeSegments.add(key);
		} else {
			removeSegments.add(key);
			segment.setStopBackgroundAbundance(getBackgroundAbundance(stopRT));
			segment.setStopRetentionTime(stopRT);
			addSegments.add(segment);
		}
	}

	/**
	 * Clear the baseline segments and create a new tree map to store them.
	 */
	private void clearBaseline() {

		/*
		 * Clear the tree map if not null and create a new one.
		 */
		if(baselineSegments != null && baselineSegments.size() > 0) {
			baselineSegments.clear();
		}
		baselineSegments = new TreeMap<Integer, IBaselineSegment>();
	}
	// ------------------------------------------private methods
}
