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

import java.io.Serializable;
import java.util.List;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public interface IPeakModel extends IPeakModelStrict, Serializable {

	int MINIMUM_SCANS = 3;

	/**
	 * If strict model is used, the increasing and decreasing tangents
	 * are available.
	 * 
	 * @return boolean
	 */
	boolean isStrictModel();

	/**
	 * If strict model is set true, the increasing and decreasing tangents
	 * are calculated. If this fails, strict model is set to false.
	 * 
	 * @param boolean
	 */
	void setStrictModel(boolean strictModel);

	/**
	 * Returns the abundance of the background at the given retention time.<br/>
	 * If the given retention time is out of peak borders, 0 will be returned.<br/>
	 * The abundance of the background is still 0 based.<br/>
	 * The retention time is given in milliseconds.
	 * 
	 * @param retentionTime
	 * @return float
	 */
	float getBackgroundAbundance(int retentionTime);

	/**
	 * Returns the abundance of the background at the peak maximum.<br/>
	 * The abundance of the background is still 0 based.<br/>
	 * 
	 * @return float
	 */
	float getBackgroundAbundance();

	/**
	 * Returns the abundance of the peak at the given retention time.<br/>
	 * The abundance of the peak at the given retention time is 0 based.<br/>
	 * If you would like to present the peak graphically, add the peak abundance
	 * on top of the background abundance.<br/>
	 * The retention time is given in milliseconds.<br/>
	 * The floor value will be still returned.<br/>
	 * For example:<br/>
	 * <br/>
	 * Retention Time - Intensity 123720 - 2.640416908<br/>
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
	 * getPeakAbundance(126050) would return a value which is 13.15576144% of
	 * the total signal of peak maximum.
	 * 
	 * @param retentionTime
	 * @return float
	 */
	float getPeakAbundance(int retentionTime);

	/**
	 * Returns the abundance of the peak at the peak maximum.<br/>
	 * The abundance of the peak at the peak maximum is 0 based.<br/>
	 * If you would like to present the peak graphically, add the peak abundance
	 * on top of the background abundance.<br/>
	 * 
	 * @return float
	 */
	float getPeakAbundance();

	/**
	 * Returns the width of the actual peak in milliseconds at its baseline.<br/>
	 * If you would like to have the width measured with help of the points of
	 * inflection, call getWidthBaseline().
	 * 
	 * @return int
	 */
	int getWidthBaselineTotal();

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
	 * Returns the retention time at the peak maximum in milliseconds.<br/>
	 * To get the retention time at maximum of the calculated maximum, call
	 * getRetentionTimeAtPeakMaximumByInflectionPointEquations().
	 * 
	 * @return int
	 */
	int getRetentionTimeAtPeakMaximum();

	/**
	 * Replaces the existing retentionTimes by the new list.
	 * Retention times are given in milliseconds.
	 * Number of scans and list size must be equal.
	 * 
	 * @param retentionTimes
	 */
	void replaceRetentionTimes(List<Integer> retentionTimes) throws IllegalArgumentException, PeakException;

	/**
	 * Returns the number of scans.
	 * 
	 * @return
	 */
	int getNumberOfScans();

	/**
	 * Returns the gradient angle of the peak.<br/>
	 * If the angle is positive, the background increases over time.<br/>
	 * If the angle is negative, the background decreases over time.
	 * 
	 * @return double
	 */
	double getGradientAngle();

	/**
	 * Returns the leading of the peak.<br/>
	 * 
	 * @return float
	 */
	float getLeading();

	/**
	 * Returns the tailing of the peak.<br/>
	 * If the value is 1 the peak has an optimal distribution of leading and
	 * tailing peak parts.<br/>
	 * If the value is > 1 the tailing part of the peak is higher than the
	 * leading part.<br/>
	 * If the value is < 1 the leading part of the peak is higher than the
	 * tailing part.
	 * 
	 * @return float
	 */
	float getTailing();

	/**
	 * Returns a list with the retention times of the peak model.<br/>
	 * The retention times are sorted ascending. The retention times are given
	 * in milliseconds.
	 * 
	 * @return List<Integer>
	 */
	List<Integer> getRetentionTimes();

	// TODO JUnit
	/**
	 * Returns the baseline at the given peak height.<br/>
	 * Use percentage height values between 0 (0%) and 1 (100%).
	 * 
	 * @param height
	 * @return {@link LinearEquation}
	 */
	LinearEquation getPercentageHeightBaselineEquation(float height);

	/**
	 * Returns the peak scan at maximum.
	 * 
	 * @return {@link IScan}
	 */
	IScan getPeakMaximum();

	/**
	 * Returns an copy of {@link IScan} from the given retention
	 * time.<br/>
	 * The retention time is given in milliseconds.<br/>
	 * If there is no mass spectrum, null will be returned.<br/>
	 * The floor value will be still returned.<br/>
	 * For example:<br/>
	 * <br/>
	 * Retention Time - Intensity 123720 - 2.640416908<br/>
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
	 * getPeakAbundance(126050) would return an IScan instance total
	 * signal is 13.15576144% of the total signal of peak maximum.
	 * 
	 * @param retentionTime
	 * @return IScan
	 */
	IScan getPeakScan(int retentionTime);

	float getIntensity(int retentionTime);

	/**
	 * This method may return null if no value was set.
	 * 
	 * @param key
	 * @return String
	 */
	Object getTemporarilyInfo(String key);

	/**
	 * Set additional information.
	 * This data is not stored. Use this for temporarily data only.
	 * 
	 * @param key
	 * @param value
	 */
	void setTemporarilyInfo(String key, Object value);
}