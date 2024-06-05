/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public abstract class AbstractPeakIntensityValues extends AbstractPeakIntensityValuesStrict implements IPeakIntensityValues {

	private static final long serialVersionUID = 8193494258780090715L;
	//
	private NavigableMap<Integer, Float> intensityValues;
	private float maxIntensity;

	/**
	 * The intensity values must contain at least 3 key (retention time) value
	 * (abundance percentage) pairs to be valid.<br/>
	 * There must be at least 1 value with an intensity of
	 * IPeakIntensityValue.MAX_INTENSITY.<br/>
	 * Please use the addIntensityValue method to add retention time - relative
	 * intensity (key - value) mappings.
	 */
	public AbstractPeakIntensityValues() {

		intensityValues = new TreeMap<>();
		maxIntensity = MAX_INTENSITY;
	}

	/**
	 * Prefer to use the normal constructor.
	 * If this constructor is used, please use the normalize() method,
	 * after setting all intensity values.
	 * 
	 * @param maxIntensity
	 */
	public AbstractPeakIntensityValues(float maxIntensity) {

		this();
		if(maxIntensity > 0.0f) {
			this.maxIntensity = maxIntensity;
		}
	}

	@Override
	public void addIntensityValue(int retentionTime, float relativeIntensity) {

		if(retentionTime >= 0 && relativeIntensity >= 0 && relativeIntensity <= maxIntensity) {
			intensityValues.put(retentionTime, relativeIntensity);
		}
	}

	@Override
	public Entry<Integer, Float> getHighestIntensityValue() {

		/*
		 * Search for an element which fits the getValue() condition
		 * [entry.getValue() == MAX_INTENSITY].
		 */
		for(Map.Entry<Integer, Float> entry : intensityValues.entrySet()) {
			if(entry.getValue() == maxIntensity) {
				return entry;
			}
		}
		/*
		 * If no element MAX_INTENSITY was stored, null will be returned.
		 */
		return null;
	}

	@Override
	public Entry<Integer, Float> getIntensityValue(int retentionTime) {

		if(retentionTime >= getStartRetentionTime() && retentionTime <= getStopRetentionTime()) {
			return intensityValues.floorEntry(retentionTime);
		} else {
			return null;
		}
	}

	@Override
	public int getStartRetentionTime() {

		int retentionTime = 0;
		if(size() > 0) {
			retentionTime = intensityValues.firstKey();
		}
		return retentionTime;
	}

	@Override
	public int getStopRetentionTime() {

		int retentionTime = 0;
		if(size() > 0) {
			retentionTime = intensityValues.lastKey();
		}
		return retentionTime;
	}

	@Override
	public void replaceRetentionTimes(List<Integer> retentionTimes) {

		if(intensityValues.size() == retentionTimes.size()) {
			/*
			 * Create a new map.
			 */
			int i = 0;
			NavigableMap<Integer, Float> intensityValuesNew = new TreeMap<Integer, Float>();
			for(Map.Entry<Integer, Float> intensityValue : intensityValues.entrySet()) {
				intensityValuesNew.put(retentionTimes.get(i++), intensityValue.getValue());
			}
			/*
			 * Replace the old by the new map.
			 */
			intensityValues = intensityValuesNew;
		}
	}

	@Override
	public int size() {

		return intensityValues.size();
	}

	@Override
	public List<Integer> getRetentionTimes() {

		return new ArrayList<>(intensityValues.keySet());
	}

	@Override
	public void normalize() {

		// Get the max intensity of the collection.
		float maxIntensityValue = Collections.max(intensityValues.values());
		/*
		 * Cause collection could contain intensity value higher than MAX_INTENSITY,
		 * set maxIntensity to MAX_INTENSITY and normalize the values.
		 */
		this.maxIntensity = MAX_INTENSITY;
		Iterator<Integer> iterator = intensityValues.navigableKeySet().iterator();
		while(iterator.hasNext()) {
			int key = iterator.next();
			float actualIntensity = intensityValues.get(key);
			intensityValues.put(key, calculateNormalizedIntensityValue(maxIntensityValue, actualIntensity));
		}
	}

	@Override
	public LinearEquation calculateIncreasingInflectionPointEquation(float totalSignal) throws PeakException {

		return calculateIncreasingInflectionPointEquation(intensityValues, totalSignal, maxIntensity);
	}

	@Override
	public LinearEquation calculateDecreasingInflectionPointEquation(float totalSignal) throws PeakException {

		return calculateDecreasingInflectionPointEquation(intensityValues, totalSignal, maxIntensity);
	}

	private float calculateNormalizedIntensityValue(float maxIntensityValue, float actualIntensity) {

		float result = 0.0f;
		if(maxIntensityValue != 0) {
			/*
			 * Why do we use: maxIntensityValue == actualIntensity?
			 * It could be that the result is 99.99999f.
			 * In such a case, all following methods would fail, cause they expect at least one value equals to MAX_INTENSITY.
			 * A human being knows that 99.99999f is 100.0f [we're not operating in financial sector :-)] a computer doesn't know.
			 */
			if(maxIntensityValue == actualIntensity) {
				result = MAX_INTENSITY;
			} else {
				result = (maxIntensity / maxIntensityValue) * actualIntensity;
			}
		}
		return result;
	}
}