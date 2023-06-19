/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public interface IPeakIntensityValues extends Serializable {

	float MAX_INTENSITY = 100.0f; // means 100%

	/**
	 * Adds an relative intensity value. The intensity value is correlated with
	 * a specific retention time.<br/>
	 * The retention time is given in milliseconds.<br/>
	 * The retention time must be >= 0<br/>
	 * If the relative intensity is < 0 or > 100 the add operation will be
	 * skipped.<br/>
	 * If there is still an intensity value stored at the specified retention
	 * time, the old value will be overwritten by the new one. A tabular model
	 * of the peak intensity distribution is stored in the intensityValues
	 * navigable map. How to use it?<br/>
	 * NavigableMap<Integer, Float> means the integer values are the retention
	 * times and the float values the relative abundances related to the peak
	 * maximum. A value of 0 means 0% of the peak maximum and a value of 100
	 * mean 100% of the peak maximum.<br/>
	 * Is it still not clear?<br/>
	 * <br/>
	 * The NavigableMap could consists the following values (from 2.062 min to
	 * 2.217 min -> milliseconds = min * 60 * 1000)<br/>
	 * <br/>
	 * Retention Time - Intensity
	 * 123720 - 2.640416908<br/>
	 * 124500 - 1.876085698<br/>
	 * 125280 - 13.15576144<br/>
	 * 126060 - 25.52403011<br/>
	 * 126840 - 51.88187609<br/>
	 * 127560 - 77.92704111<br/>
	 * 128340 - 100 (peakMaximum)<br/>
	 * 129120 - 92.25246091<br/>
	 * 129900 - 65.75564563<br/>
	 * 130680 - 37.84597568<br/>
	 * 131460 - 14.62651998<br/>
	 * 132240 - 5.790387956<br/>
	 * 133020 - 0<br/>
	 * <br/>
	 * The intensity values must contain at least 3 key (retention time) value
	 * (abundance percentage) pairs.<br/>
	 * If the peakMaximum has an abundance value of 58672 for the ion
	 * ion 43 its abundance will be 7718.74835 at the retention time 125280
	 * (2.088 min).
	 * 
	 * @param retentionTime
	 * @param relativeIntensity
	 */
	void addIntensityValue(int retentionTime, float relativeIntensity);

	/**
	 * Returns the Map.Entry<Integer, Float> according to highest intensity
	 * value (IPeakIntensityValues.MAX_INTENSITY).<br/>
	 * If there is no such value stored, null will be returned.<br/>
	 * Integer is the retention time in milliseconds and Float is the relative
	 * intensity.
	 * 
	 * @return Map.Entry<Integer, Float>
	 */
	Map.Entry<Integer, Float> getHighestIntensityValue();

	/**
	 * Returns the number of the stored retentionTime - relativeIntensity
	 * mappings.
	 * 
	 * @return int
	 */
	int size();

	/**
	 * Returns the start retention time of the peak in milliseconds.
	 * 
	 * @return int
	 */
	int getStartRetentionTime();

	/**
	 * Returns the stop retention time of the peak in milliseconds.
	 * 
	 * @return int
	 */
	int getStopRetentionTime();

	/**
	 * Replaces the existing retentionTimes by the new list.
	 * Retention times are given in milliseconds.
	 * Number of scans and list size must be equal.
	 * 
	 * @param retentionTimes
	 */
	void replaceRetentionTimes(List<Integer> retentionTimes);

	/**
	 * Returns an Map.Entry<Integer, Float> corresponding to the given retention
	 * time.<br/>
	 * Internally floorEntry() will be used.<br/>
	 * It means that the entry - defined by its retention time key - will be
	 * returned which is equal or lower to the given retention time key.<br/>
	 * Only valid entries will be returned if the following condition is true:
	 * "retentionTime >= getStartRetentionTime() && retentionTime <= getStopRetentionTime()"
	 * , if it is not true, null will be returned.<br/>
	 * If there is no entry available, null will be returned.
	 * 
	 * @param retentionTime
	 * @return Map.Entry<Integer, Float>
	 */
	Map.Entry<Integer, Float> getIntensityValue(int retentionTime);

	/**
	 * Calculates the increasing inflection point equation.<br/>
	 * You must provide the total signal of the peak maximum unless the
	 * intensity values store only a percentage distribution between 0 (0%) and
	 * 100 (100%) signal.<br/>
	 * The intensity do not know nothing about the total signal of the peak, but
	 * to not adjust the equation each time to the total signal, it is important
	 * to provide such value.
	 * 
	 * @param totalSignal
	 * @throws PeakException
	 * @return LinearEquation
	 */
	LinearEquation calculateIncreasingInflectionPointEquation(float totalSignal) throws PeakException;

	/**
	 * Calculates the decreasing inflection point equation. You must provide the
	 * total signal of the peak maximum unless the intensity values store only a
	 * percentage distribution between 0 (0%) and 100 (100%) signal.<br/>
	 * The intensity do not know nothing about the total signal of the peak, but
	 * to not adjust the equation each time to the total signal, it is important
	 * to provide such value.
	 * 
	 * @param totalSignal
	 * @throws PeakException
	 * @return LinearEquation
	 */
	LinearEquation calculateDecreasingInflectionPointEquation(float totalSignal) throws PeakException;

	/**
	 * Returns a list of the retention times of the peak model.<br/>
	 * The retention times are sorted ascending. The retention times are given
	 * in milliseconds.
	 * 
	 * @return List<Integer>
	 */
	List<Integer> getRetentionTimes();

	/**
	 * Normalizes the intensity values to MAX_INTENSITY.
	 */
	void normalize();
}