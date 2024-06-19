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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.Scan;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public abstract class AbstractPeakModel extends AbstractPeakModelStrict implements IPeakModel {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -5447031278614316999L;
	/*
	 * If strict model is used, the inflection points are calculated.
	 * CAUTION - more tests needed - keep it true for now.
	 */
	private boolean strictModel = false;
	/*
	 * The peak maximum is a mass spectrum or a simple tic signal.
	 */
	private IScan peakMaximum;
	private LinearEquation backgroundEquation;
	private IPeakIntensityValues peakIntensityValues;
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
	protected AbstractPeakModel(IScan peakMaximum, IPeakIntensityValues peakIntensityValues, float startBackgroundAbundance, float stopBackgroundAbundance, boolean strictModel) throws IllegalArgumentException, PeakException {

		super(peakMaximum, peakIntensityValues);
		/*
		 * Checks all conditions for the peak model to be valid.
		 */
		this.peakMaximum = peakMaximum;
		this.peakIntensityValues = peakIntensityValues;
		this.startBackgroundAbundance = startBackgroundAbundance;
		this.stopBackgroundAbundance = stopBackgroundAbundance;
		this.strictModel = strictModel;
		this.temporarilyInfo = new HashMap<>();
		/*
		 * Run calculation
		 */
		calculatePeakModel();
	}

	@Override
	public boolean isStrictModel() {

		return strictModel;
	}

	@Override
	public void setStrictModel(boolean strictModel) {

		this.strictModel = strictModel;
		validateStrictModel();
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

		/*
		 * Baseline at 10% peak height.
		 */
		LinearEquation percentageHeightBaseline = getPercentageHeightBaselineEquation(0.1f);
		if(percentageHeightBaseline == null) {
			return 0;
		}
		/*
		 * Calculation
		 */
		if(strictModel) {
			return calucalteTailingByInflectionPoints(percentageHeightBaseline);
		} else {
			return calucalteTailingByIntensityValues();
		}
	}

	@Override
	public List<Integer> getRetentionTimes() {

		return peakIntensityValues.getRetentionTimes();
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

	@Override
	public Object getTemporarilyInfo(String key) {

		return temporarilyInfo.get(key);
	}

	@Override
	public void setTemporarilyInfo(String key, Object value) {

		temporarilyInfo.put(key, value);
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
		//
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

	private void calculatePeakModel() throws IllegalArgumentException, PeakException {

		checkModelConditions(peakMaximum, peakIntensityValues);
		backgroundEquation = calculateBackgroundEquation(startBackgroundAbundance, stopBackgroundAbundance);
		gradientAngle = calculateGradientAngle();
		validateStrictModel();
	}

	private float calucalteTailingByIntensityValues() {

		float tailing = 0.0f;
		List<Integer> retentionTimes = peakIntensityValues.getRetentionTimes();
		if(retentionTimes.size() >= 3) {
			/*
			 * Values
			 */
			Map.Entry<Integer, Float> maximum = peakIntensityValues.getHighestIntensityValue();
			float halfHeight = maximum.getValue() / 2;
			boolean first = true;
			/*
			 * A peak consist of at least 3 points.
			 * Hence, some additional checks are not required
			 * to speed up performance.
			 */
			IPoint leftA = null;
			IPoint leftB = null;
			IPoint rightA = null;
			IPoint rightB = null;
			//
			exitloop:
			for(int i = 0; i < retentionTimes.size(); i++) {
				int retentionTime = retentionTimes.get(i);
				float intensity = peakIntensityValues.getIntensityValue(retentionTime).getValue();
				if(first) {
					if(intensity > halfHeight) {
						if(i > 0) {
							int retentionTimePrevious = retentionTimes.get(i - 1);
							float intensityPrevious = peakIntensityValues.getIntensityValue(retentionTimePrevious).getValue();
							leftA = new Point(retentionTimePrevious, intensityPrevious);
							leftB = new Point(retentionTime, intensity);
						} else {
							/*
							 * Special case if the peak intensity starts above 50%.
							 */
							leftA = new Point(retentionTime, intensity);
							leftB = new Point(retentionTime, intensity);
						}
						first = false;
					}
				} else {
					if(intensity < halfHeight) {
						int retentionTimePrevious = retentionTimes.get(i - 1);
						float intensityPrevious = peakIntensityValues.getIntensityValue(retentionTimePrevious).getValue();
						rightA = new Point(retentionTimePrevious, intensityPrevious);
						rightB = new Point(retentionTime, intensity);
						break exitloop;
					}
				}
			}
			/*
			 * Calculate
			 */
			if(leftA != null && leftB != null && rightA != null && rightB != null) {
				LinearEquation linearEquationPre = Equations.createLinearEquation(leftA, leftB);
				LinearEquation linearEquationPost = Equations.createLinearEquation(rightA, rightB);
				//
				if(linearEquationPre != null && linearEquationPost != null) {
					int retentionTimeStart = (int)Math.round(linearEquationPre.calculateX(halfHeight));
					int retentionTimeCenter = maximum.getKey();
					int retentionTimeStop = (int)Math.round(linearEquationPost.calculateX(halfHeight));
					//
					float rightWidth = retentionTimeCenter - retentionTimeStart;
					float leftWidth = retentionTimeStop - retentionTimeCenter;
					if(leftWidth > 0) {
						tailing = rightWidth / leftWidth;
					}
				}
			}
		}
		//
		return tailing;
	}

	private void validateStrictModel() {

		/*
		 * Only if a strict model is used.
		 */
		if(strictModel) {
			boolean success = calculateInflectionPointEquations();
			if(!success) {
				strictModel = false;
			}
		}
	}

	@Override
	public int hashCode() {

		return Objects.hash(gradientAngle, peakMaximum);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		AbstractPeakModel other = (AbstractPeakModel)obj;
		return Double.doubleToLongBits(gradientAngle) == Double.doubleToLongBits(other.gradientAngle) && //
				peakMaximum.getRetentionTime() == other.peakMaximum.getRetentionTime() && //
				peakMaximum.getTotalSignal() == other.peakMaximum.getTotalSignal();
	}

	@Override
	public String toString() {

		return "AbstractPeakModel [peakMaximum=" + peakMaximum + ", backgroundEquation=" + backgroundEquation + ", peakIntensityValues=" + peakIntensityValues + ", gradientAngle=" + gradientAngle + ", startBackgroundAbundance=" + startBackgroundAbundance + ", stopBackgroundAbundance=" + stopBackgroundAbundance + ", temporarilyInfo=" + temporarilyInfo + "]";
	}
}