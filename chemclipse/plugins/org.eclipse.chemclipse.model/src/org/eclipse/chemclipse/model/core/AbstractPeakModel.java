/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.Scan;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.exceptions.SolverException;

public abstract class AbstractPeakModel implements IPeakModel {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -5447031278614316999L;
	/*
	 * The peak maximum is a mass spectrum or a simple tic signal.
	 */
	private IScan peakMaximum;
	//
	private LinearEquation backgroundEquation;
	private IPeakIntensityValues peakIntensityValues;
	/*
	 * Points of inflection. We do not need to determine the linear equation of
	 * the points of inflection, because the peak is not given by a mathematical
	 * function. The peak consists of several points so that the linear equation
	 * of the point of inflection is described by the segment of the peak with
	 * the highest or lowest slope.
	 */
	private LinearEquation increasingInflectionPointEquation;
	private LinearEquation decreasingInflectionPointEquation;
	/*
	 * If the background is non equal than there is an angle to which the
	 * background raises or falls. This needs to be considered when the width of
	 * the peak will be determined. If the gradient angle (alpha) is negative,
	 * the baseline raises from the beginning to the end. If the gradient angle
	 * (alpha) is positive, the baseline decreases from the beginning to the
	 * end.
	 */
	private double gradientAngle;
	/*
	 * Start and stop background abundance.
	 */
	private float startBackgroundAbundance;
	private float stopBackgroundAbundance;
	/*
	 * Temp info can be stored here.
	 * This data is not stored in the file format.
	 */
	private Map<String, Object> temporarilyInfo;

	/**
	 * The abstract peak model creates a peak model by defining its core values.<br/>
	 * The peak maximum is a {@link IPeakMassSpectrum} which stores the
	 * responsible mass spectrum for this peak.<br/>
	 * The peak maximum should contain absolute abundance values for the given
	 * peak. The background should not be considered in the peak maximum. So the
	 * peak maximum represents the pure maybe deconvoluted mass spectrum for the
	 * actual peak.<br/>
	 * 
	 * @param peakMaximum
	 * @param peakIntensityValues
	 * @param startBackgroundAbundance
	 * @param stopBackgroundAbundance
	 */
	protected AbstractPeakModel(IScan peakMaximum, IPeakIntensityValues peakIntensityValues, float startBackgroundAbundance, float stopBackgroundAbundance) throws IllegalArgumentException, PeakException {

		/*
		 * Checks all conditions for the peak model to be valid.
		 */
		this.peakMaximum = peakMaximum;
		this.peakIntensityValues = peakIntensityValues;
		this.startBackgroundAbundance = startBackgroundAbundance;
		this.stopBackgroundAbundance = stopBackgroundAbundance;
		//
		calculatePeakModel();
		/*
		 * Temp info
		 */
		temporarilyInfo = new HashMap<>();
	}

	@Override
	public float getBackgroundAbundance() {

		Map.Entry<Integer, Float> entry = peakIntensityValues.getHighestIntensityValue();
		if(entry != null) {
			return (float)backgroundEquation.calculateY(entry.getKey());
		} else {
			return 0;
		}
	}

	@Override
	public float getBackgroundAbundance(int retentionTime) {

		if(retentionTime >= getStartRetentionTime() && retentionTime <= getStopRetentionTime()) {
			return (float)backgroundEquation.calculateY(retentionTime);
		} else {
			return 0.0f;
		}
	}

	@Override
	public float getPeakAbundanceByInflectionPoints() {

		float abundance = 0.0f;
		try {
			IPoint intersection = Equations.calculateIntersection(increasingInflectionPointEquation, decreasingInflectionPointEquation);
			abundance = (float)intersection.getY();
		} catch(SolverException e) {
		}
		return abundance;
	}

	@Override
	public float getPeakAbundance() {

		return peakMaximum.getTotalSignal();
	}

	@Override
	public float getPeakAbundance(int retentionTime) {

		if(retentionTime >= getStartRetentionTime() && retentionTime <= getStopRetentionTime()) {
			Map.Entry<Integer, Float> entry = peakIntensityValues.getIntensityValue(retentionTime);
			if(entry != null) {
				return (float)((peakMaximum.getTotalSignal() * entry.getValue()) / 100.0d);
			}
		}
		return 0.0f;
	}

	@Override
	public int getStartRetentionTime() {

		return peakIntensityValues.getStartRetentionTime();
	}

	@Override
	public int getStopRetentionTime() {

		return peakIntensityValues.getStopRetentionTime();
	}

	@Override
	public int getRetentionTimeAtPeakMaximum() {

		return peakIntensityValues.getHighestIntensityValue().getKey();
	}

	@Override
	public int getRetentionTimeAtPeakMaximumByInflectionPoints() {

		int x = 0;
		try {
			IPoint intersection = Equations.calculateIntersection(increasingInflectionPointEquation, decreasingInflectionPointEquation);
			x = (int)intersection.getX();
		} catch(SolverException e) {
		}
		return x;
	}

	@Override
	public void replaceRetentionTimes(List<Integer> retentionTimes) throws IllegalArgumentException, PeakException {

		peakIntensityValues.replaceRetentionTimes(retentionTimes);
		calculatePeakModel();
	}

	@Override
	public int getNumberOfScans() {

		return peakIntensityValues.size();
	}

	@Override
	public int getWidthBaselineTotal() {

		return getStopRetentionTime() - getStartRetentionTime() + 1;
	}

	@Override
	public int getWidthBaselineByInflectionPoints() {

		/*
		 * Why do we use base equation, where the baseline is f(x) = 0x + 0?
		 * Because peak and background can be retrieved seperately.
		 */
		LinearEquation base = new LinearEquation(0, 0);
		int width = 0;
		try {
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
		return width;
	}

	@Override
	public int getWidthByInflectionPoints() {

		return getWidthByInflectionPoints(0.5f);
	}

	@Override
	public int getWidthByInflectionPoints(float height) {

		int width = 0;
		LinearEquation percentageHeightBaseline = getPercentageHeightBaselineEquation(height);
		if(percentageHeightBaseline == null) {
			return 0;
		}
		try {
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
		return width;
	}

	@Override
	public float getIncreasingInflectionPointAbundance(int retentionTime) {

		return (float)increasingInflectionPointEquation.calculateY(retentionTime);
	}

	@Override
	public float getDecreasingInflectionPointAbundance(int retentionTime) {

		return (float)decreasingInflectionPointEquation.calculateY(retentionTime);
	}

	@Override
	public double getGradientAngle() {

		return gradientAngle;
	}

	// TODO JUnit
	@Override
	public float getLeading() {

		float leading = 0.0f;
		float tailing = getTailing();
		if(tailing != 0.0f) {
			leading = 1 / tailing;
		}
		return leading;
	}

	@Override
	public float getTailing() {

		float tailing = 0.0f;
		/*
		 * Baseline at 10% peak height.
		 */
		LinearEquation percentageHeightBaseline = getPercentageHeightBaselineEquation(0.1f);
		if(percentageHeightBaseline == null) {
			return 0;
		}
		/*
		 * Is it correct to use the inflection point values (peak height,
		 * intersection baseline left and right)?<br/> It is much easier to
		 * implement than to search after the tabular model values, but can it
		 * be used to determine the peak tailing?
		 */
		try {
			IPoint p1 = Equations.calculateIntersection(increasingInflectionPointEquation, percentageHeightBaseline);
			IPoint p2 = Equations.calculateIntersection(decreasingInflectionPointEquation, percentageHeightBaseline);
			int retentionTimeMax = getRetentionTimeAtPeakMaximumByInflectionPoints();
			float leftWidth = retentionTimeMax - (float)p1.getX();
			float rightWidth = (float)p2.getX() - retentionTimeMax;
			tailing = rightWidth / leftWidth;
		} catch(SolverException e) {
		}
		return tailing;
	}

	@Override
	public List<Integer> getRetentionTimes() {

		return peakIntensityValues.getRetentionTimes();
	}

	// TODO JUnit
	@Override
	public LinearEquation getIncreasingInflectionPointEquation() {

		return increasingInflectionPointEquation;
	}

	// TODO JUnit
	@Override
	public LinearEquation getDecreasingInflectionPointEquation() {

		return decreasingInflectionPointEquation;
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

	@Override
	public IScan getPeakMaximum() {

		return peakMaximum;
	}

	@Override
	public IScan getPeakScan(int retentionTime) {

		if(retentionTime >= getStartRetentionTime() && retentionTime <= getStopRetentionTime()) {
			Map.Entry<Integer, Float> entry = peakIntensityValues.getIntensityValue(retentionTime);
			if(entry != null) {
				float intensity = entry.getValue();
				return new Scan(intensity);
			}
		}
		return null;
	}

	@Override
	public float getIntensity(int retentionTime) {

		float intensity = 0.0f;
		Map.Entry<Integer, Float> entry = peakIntensityValues.getIntensityValue(retentionTime);
		if(entry != null) {
			intensity = entry.getValue();
		}
		return intensity;
	}

	private void checkModelConditions(IScan peakMaximum, IPeakIntensityValues peakIntensityValues) throws IllegalArgumentException, PeakException {

		if(peakMaximum == null) {
			throw new IllegalArgumentException("The peak maximum must not be null.");
		}
		/*
		 * Test if there are intensity values stored.
		 */
		if(peakIntensityValues == null || peakIntensityValues.size() < IPeakModel.MINIMUM_SCANS) {
			throw new IllegalArgumentException("The intensity values must not be null or must contain at least 3 key value pairs.");
		}
		/*
		 * Check that the highest intensity is 100.0f. If it is more or less,
		 * the intensity map was not instantiated correctly.
		 */
		Map.Entry<Integer, Float> entry = peakIntensityValues.getHighestIntensityValue();
		if(entry == null) {
			throw new PeakException("There must be at least one intensity value stored with a relative intensity of IPeakIntensityValues.MAX_INTENSITY.");
		}
		/*
		 * Sets the peak maximum retention time.
		 */
		peakMaximum.setRetentionTime(peakIntensityValues.getHighestIntensityValue().getKey());
		this.peakMaximum = peakMaximum;
		this.peakIntensityValues = peakIntensityValues;
	}

	private LinearEquation calculateBackgroundEquation(float startBackgroundAbundance, float stopBackgroundAbundance) {

		/*
		 * A value < 0 for start and stop background abundance is not
		 * applicable.
		 */
		if(startBackgroundAbundance < 0) {
			startBackgroundAbundance = 0;
		}
		if(stopBackgroundAbundance < 0) {
			stopBackgroundAbundance = 0;
		}
		/*
		 * Create the background equation.<br/> With the equation you can
		 * calculate the background abundance for a given retention time.
		 */
		int startRetentionTime = peakIntensityValues.getStartRetentionTime();
		int stopRetentionTime = peakIntensityValues.getStopRetentionTime();
		IPoint p1 = new Point(startRetentionTime, startBackgroundAbundance);
		IPoint p2 = new Point(stopRetentionTime, stopBackgroundAbundance);
		return Equations.createLinearEquation(p1, p2);
	}

	/**
	 * If the gradient angle (alpha) is negative, the baseline raises from the
	 * beginning to the end.<br/>
	 * If the gradient angle (alpha) is positive, the baseline decreases from
	 * the beginning to the end.
	 * 
	 * @return double
	 */
	private double calculateGradientAngle() {

		int b = getWidthBaselineTotal();
		float start = getBackgroundAbundance(peakIntensityValues.getStartRetentionTime());
		float stop = getBackgroundAbundance(peakIntensityValues.getStopRetentionTime());
		/*
		 * If the values of start and stop are equal, there is no angle. If b ==
		 * 0, there could be also no angle.
		 */
		if(stop == start || b == 0) {
			return 0.0d;
		}
		/*
		 * If a is negative, the baseline raises. If a is positive, the baseline
		 * decreases.
		 */
		double a = stop - start;
		/*
		 * Use the arcus tangens to determine alpha.
		 */
		return Math.toDegrees(Math.atan(a / b));
	}

	@Override
	public Object getTemporarilyInfo(String key) {

		return temporarilyInfo.get(key);
	}

	@Override
	public void setTemporarilyInfo(String key, Object value) {

		temporarilyInfo.put(key, value);
	}

	private void calculatePeakModel() throws IllegalArgumentException, PeakException {

		checkModelConditions(peakMaximum, peakIntensityValues);
		backgroundEquation = calculateBackgroundEquation(startBackgroundAbundance, stopBackgroundAbundance);
		gradientAngle = calculateGradientAngle();
		/*
		 * Calculate the equation for the points of inflection.<br/> The peak
		 * maximum has been checked, so it can be used here.
		 */
		increasingInflectionPointEquation = peakIntensityValues.calculateIncreasingInflectionPointEquation(peakMaximum.getTotalSignal());
		decreasingInflectionPointEquation = peakIntensityValues.calculateDecreasingInflectionPointEquation(peakMaximum.getTotalSignal());
	}

	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		AbstractPeakModel other = (AbstractPeakModel)otherObject;
		return getPeakMaximum().equals(other.getPeakMaximum()) && gradientAngle == other.getGradientAngle() && increasingInflectionPointEquation.equals(other.getIncreasingInflectionPointEquation()) && decreasingInflectionPointEquation.equals(other.getDecreasingInflectionPointEquation());
	}

	// TODO JUnit
	@Override
	public int hashCode() {

		return 7 * peakMaximum.hashCode() + 11 * Double.valueOf(gradientAngle).hashCode() + 13 * increasingInflectionPointEquation.hashCode() + 15 * decreasingInflectionPointEquation.hashCode();
	}

	// TODO JUnit
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("peakMaximum=" + peakMaximum);
		builder.append(",");
		builder.append("gradientAngle=" + gradientAngle);
		builder.append(",");
		builder.append("increasingInflectionPointEquation=" + increasingInflectionPointEquation);
		builder.append(",");
		builder.append("decreasingInflectionPointEquation=" + decreasingInflectionPointEquation);
		builder.append("]");
		return builder.toString();
	}
}
