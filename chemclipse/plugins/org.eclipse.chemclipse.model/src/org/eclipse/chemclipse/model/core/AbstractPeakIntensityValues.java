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
import org.eclipse.chemclipse.model.support.ITwoPoints;
import org.eclipse.chemclipse.model.support.TwoPoints;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.exceptions.PointIsNullException;

public abstract class AbstractPeakIntensityValues implements IPeakIntensityValues {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 1597422380319731942L;
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
		intensityValues = new TreeMap<Integer, Float>();
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
	public LinearEquation calculateIncreasingInflectionPointEquation(float totalSignal) throws PeakException {

		Map.Entry<Integer, Float> entry = getHighestIntensityValue();
		if(entry != null) {
			NavigableMap<Integer, Float> increasingValues = intensityValues.headMap(entry.getKey(), true);
			return calculateInflectionPointEquation(increasingValues, totalSignal);
		}
		return null;
	}

	@Override
	public LinearEquation calculateDecreasingInflectionPointEquation(float totalSignal) throws PeakException {

		Map.Entry<Integer, Float> entry = getHighestIntensityValue();
		if(entry != null) {
			NavigableMap<Integer, Float> decreasingValues = intensityValues.tailMap(entry.getKey(), true);
			return calculateInflectionPointEquation(decreasingValues, totalSignal);
		}
		return null;
	}

	@Override
	public List<Integer> getRetentionTimes() {

		return new ArrayList<Integer>(intensityValues.keySet());
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

	// ------------------------------private methods
	/**
	 * Calculates a inflection point equation.
	 * 
	 * @throws PeakException
	 */
	private LinearEquation calculateInflectionPointEquation(NavigableMap<Integer, Float> values, float totalSignal) throws PeakException {

		NavigableMap<Double, ITwoPoints> slopes = new TreeMap<Double, ITwoPoints>();
		IPoint p1 = null;
		Map.Entry<Integer, Float> e1 = null;
		IPoint p2 = null;
		Map.Entry<Integer, Float> e2 = null;
		ITwoPoints points = null;
		List<Integer> keys = new ArrayList<Integer>(values.keySet());
		for(int i = 0; i < keys.size() - 1; i++) {
			/*
			 * Use the existing entry and point to avoid unnecessary object
			 * creation.
			 */
			if(e1 == null) {
				e1 = values.floorEntry(keys.get(i));
				p1 = new Point(e1.getKey(), (e1.getValue() / maxIntensity) * totalSignal);
			} else {
				e1 = e2;
				p1 = p2;
			}
			e2 = values.floorEntry(keys.get(i + 1));
			p2 = new Point(e2.getKey(), (e2.getValue() / maxIntensity) * totalSignal);
			try {
				points = new TwoPoints(p1, p2);
				slopes.put(Math.abs(points.getSlope()), points);
			} catch(PointIsNullException e) {
			}
		}
		/*
		 * Why can i use the highest entry here? Are there not increasing and
		 * decreasing slopes? Yes, but the slope has been added to the list with
		 * its absolute value Math.abs(points.getSlope()), for that this
		 * statement is correct.
		 */
		Entry<Double, ITwoPoints> entry = slopes.lastEntry();
		if(entry != null) {
			return entry.getValue().getLinearEquation();
		} else {
			throw new PeakException("The inflection point equation could not be calculated. [values=" + values + ", totalSignal: " + totalSignal);
		}
	}
	// ------------------------------private methods
}
