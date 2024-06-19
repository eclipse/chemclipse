/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.exceptions.SolverException;

public abstract class AbstractPeakModelStrict implements IPeakModel {

	private static final long serialVersionUID = 9209028522266021366L;
	/*
	 * Used normally in AbstractPeakModel
	 */
	private IScan peakMaximum;
	private IPeakIntensityValues peakIntensityValues;
	/*
	 * Points of inflection. We do not need to determine the linear equation of
	 * the points of inflection, because the peak is not given by a mathematical
	 * function. The peak consists of several points so that the linear equation
	 * of the point of inflection is described by the segment of the peak with
	 * the highest or lowest slope.
	 */
	private LinearEquation increasingInflectionPointEquation = null;
	private LinearEquation decreasingInflectionPointEquation = null;

	protected AbstractPeakModelStrict(IScan peakMaximum, IPeakIntensityValues peakIntensityValues) {

		this.peakMaximum = peakMaximum;
		this.peakIntensityValues = peakIntensityValues;
	}

	@Override
	public boolean areInflectionPointsAvailable() {

		return increasingInflectionPointEquation != null && decreasingInflectionPointEquation != null;
	}

	@Override
	public IPoint calculateIntersection() throws SolverException {

		IPoint point = null;
		if(areInflectionPointsAvailable()) {
			point = Equations.calculateIntersection(increasingInflectionPointEquation, decreasingInflectionPointEquation);
		} else {
			throw new SolverException("The increasing/decreasing equation is missing.");
		}
		//
		return point;
	}

	@Override
	public IPoint calculateIntersection(LinearEquation linearEquation, boolean increasing) throws SolverException {

		IPoint point = null;
		if(areInflectionPointsAvailable()) {
			point = Equations.calculateIntersection(increasing ? increasingInflectionPointEquation : decreasingInflectionPointEquation, linearEquation);
		} else {
			throw new SolverException("The increasing/decreasing equation is missing.");
		}
		//
		return point;
	}

	@Override
	public float getPeakAbundanceByInflectionPoints() {

		float abundance = 0.0f;
		if(areInflectionPointsAvailable()) {
			try {
				IPoint intersection = Equations.calculateIntersection(increasingInflectionPointEquation, decreasingInflectionPointEquation);
				abundance = (float)intersection.getY();
			} catch(SolverException e) {
			}
		}
		//
		return abundance;
	}

	@Override
	public int getRetentionTimeAtPeakMaximumByInflectionPoints() {

		int x = 0;
		if(areInflectionPointsAvailable()) {
			try {
				IPoint intersection = Equations.calculateIntersection(increasingInflectionPointEquation, decreasingInflectionPointEquation);
				x = (int)intersection.getX();
			} catch(SolverException e) {
			}
		}
		//
		return x;
	}

	@Override
	public int getWidthBaselineByInflectionPoints() {

		/*
		 * Why do we use base equation, where the baseline is f(x) = 0x + 0?
		 * Because peak and background can be retrieved seperately.
		 */
		int width = 0;
		if(areInflectionPointsAvailable()) {
			try {
				LinearEquation base = new LinearEquation(0, 0);
				IPoint p1 = Equations.calculateIntersection(increasingInflectionPointEquation, base);
				IPoint p2 = Equations.calculateIntersection(decreasingInflectionPointEquation, base);
				width = (int)(p2.getX() - p1.getX() + 1);
				/*
				 * The value was such too high, so don't use the Equations method.
				 * width = (int)Equations.calculateWidth(p1, p2);
				 */
			} catch(SolverException e) {
				/*
				 * If an error occurs, the width will be 0.
				 */
			}
		}
		//
		return width;
	}

	@Override
	public int getWidthByInflectionPoints() {

		return getWidthByInflectionPoints(0.5f);
	}

	@Override
	public int getWidthByInflectionPoints(float height) {

		int width = 0;
		if(areInflectionPointsAvailable()) {
			try {
				LinearEquation percentageHeightBaseline = getPercentageHeightBaselineEquation(height);
				if(percentageHeightBaseline == null) {
					return 0;
				}
				//
				IPoint p1 = Equations.calculateIntersection(increasingInflectionPointEquation, percentageHeightBaseline);
				IPoint p2 = Equations.calculateIntersection(decreasingInflectionPointEquation, percentageHeightBaseline);
				width = (int)(p2.getX() - p1.getX() + 1);
				/*
				 * The value was such too high, so don't use the Equations method.
				 * width = (int)Equations.calculateWidth(p1, p2);
				 */
			} catch(SolverException e) {
				/*
				 * If an error occurs, the width will be 0.
				 */
			}
		}
		//
		return width;
	}

	@Override
	public float getIncreasingInflectionPointAbundance(int retentionTime) {

		float abundance = 0.0f;
		if(increasingInflectionPointEquation != null) {
			abundance = (float)increasingInflectionPointEquation.calculateY(retentionTime);
		}
		//
		return abundance;
	}

	@Override
	public float getDecreasingInflectionPointAbundance(int retentionTime) {

		float abundance = 0.0f;
		if(decreasingInflectionPointEquation != null) {
			abundance = (float)decreasingInflectionPointEquation.calculateY(retentionTime);
		}
		//
		return abundance;
	}

	// TODO JUnit
	@Override
	public LinearEquation getPercentageHeightBaselineEquation(float height) {

		if(height < 0 || height > 1.0f) {
			return null;
		}
		/*
		 * Use the calculated abundance of the peak and not the stored one in
		 * peakMaximum.
		 */
		float abundance = getPeakAbundanceByInflectionPoints();
		float percentageHeight = abundance * height;
		/*
		 * Why can we use a linear baseline without adopting it to the angle
		 * given by the background?<br/> Because the peak and the background are
		 * separated. So it is not necessary to shift the baseline for the given
		 * percentage abundance.
		 */
		return new LinearEquation(0, percentageHeight);
	}

	protected float calucalteTailingByInflectionPoints(LinearEquation percentageHeightBaseline) {

		float tailing = 0.0f;
		/*
		 * Is it correct to use the inflection point values (peak height,
		 * intersection baseline left and right)?<br/> It is much easier to
		 * implement than to search after the tabular model values, but can it
		 * be used to determine the peak tailing?
		 */
		if(areInflectionPointsAvailable()) {
			try {
				IPoint p1 = Equations.calculateIntersection(increasingInflectionPointEquation, percentageHeightBaseline);
				IPoint p2 = Equations.calculateIntersection(decreasingInflectionPointEquation, percentageHeightBaseline);
				int retentionTimeMax = getRetentionTimeAtPeakMaximumByInflectionPoints();
				float leftWidth = retentionTimeMax - (float)p1.getX();
				float rightWidth = (float)p2.getX() - retentionTimeMax;
				tailing = rightWidth / leftWidth;
			} catch(SolverException e) {
			}
		}
		//
		return tailing;
	}

	protected boolean calculateInflectionPointEquations() {

		/*
		 * Calculate the equation for the points of inflection.<br/> The peak
		 * maximum has been checked, so it can be used here.
		 */
		try {
			increasingInflectionPointEquation = peakIntensityValues.calculateIncreasingInflectionPointEquation(peakMaximum.getTotalSignal());
			decreasingInflectionPointEquation = peakIntensityValues.calculateDecreasingInflectionPointEquation(peakMaximum.getTotalSignal());
		} catch(Exception e) {
			increasingInflectionPointEquation = null;
			decreasingInflectionPointEquation = null;
		}
		//
		return areInflectionPointsAvailable();
	}
}