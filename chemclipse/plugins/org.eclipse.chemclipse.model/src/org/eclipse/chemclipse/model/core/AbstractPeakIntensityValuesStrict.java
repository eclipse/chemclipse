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

public abstract class AbstractPeakIntensityValuesStrict implements IPeakIntensityValues {

	private static final long serialVersionUID = -728565624897925421L;

	protected LinearEquation calculateIncreasingInflectionPointEquation(NavigableMap<Integer, Float> intensityValues, float totalSignal, float maxIntensity) throws PeakException {

		Map.Entry<Integer, Float> entry = getHighestIntensityValue();
		if(entry != null) {
			NavigableMap<Integer, Float> increasingValues = intensityValues.headMap(entry.getKey(), true);
			return calculateInflectionPointEquation(increasingValues, totalSignal, maxIntensity);
		}
		return null;
	}

	protected LinearEquation calculateDecreasingInflectionPointEquation(NavigableMap<Integer, Float> intensityValues, float totalSignal, float maxIntensity) throws PeakException {

		Map.Entry<Integer, Float> entry = getHighestIntensityValue();
		if(entry != null) {
			NavigableMap<Integer, Float> decreasingValues = intensityValues.tailMap(entry.getKey(), true);
			return calculateInflectionPointEquation(decreasingValues, totalSignal, maxIntensity);
		}
		return null;
	}

	/**
	 * Calculates a inflection point equation.
	 * 
	 * @throws PeakException
	 */
	private LinearEquation calculateInflectionPointEquation(NavigableMap<Integer, Float> values, float totalSignal, float maxIntensity) throws PeakException {

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
}