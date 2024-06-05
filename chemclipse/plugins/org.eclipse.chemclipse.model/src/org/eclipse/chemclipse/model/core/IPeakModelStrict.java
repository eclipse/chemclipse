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

import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public interface IPeakModelStrict {

	/**
	 * Returns the abundance (height) of the peak determined by use of the
	 * points of inflection.<br/>
	 * The abundance or height is determined by the intersection between both
	 * inflection equations.<br/>
	 * To get the stored height you can also use getPeakAbundance().
	 * 
	 * @return float
	 */
	float getPeakAbundanceByInflectionPoints();

	/**
	 * Returns the width of the peak by using the points of inflection.<br/>
	 * The width is measured were the equations of the point of inflection
	 * intersects with the baseline of the peak.<br/>
	 * The width will be returned in milliseconds.<br/>
	 * If you would like to have the total width at baseline call
	 * getWidthBaselineTotal().
	 * 
	 * @return int
	 */
	int getWidthBaselineByInflectionPoints();

	/**
	 * Returns the width of the actual peak in milliseconds at a half of the
	 * peak height.<br/>
	 * 
	 * @return int
	 */
	int getWidthByInflectionPoints();

	/**
	 * Returns the peak width at a given height.<br/>
	 * The width is measured were the equations of the point of inflection
	 * intersects with the percentage height of the peak.<br/>
	 * As the height, the abundance (height) by inflection points is chosen. The
	 * abundance can be achieved by getPeakAbundanceByInflectionPoints(). Use
	 * this to get the width e.g. at a height of 50%, or at 85%.<br/>
	 * The height must be a value between 0.0f (0%) and 1.0f (100%).<br/>
	 * 
	 * @param height
	 * @return int
	 */
	int getWidthByInflectionPoints(float height);

	/**
	 * Returns the retention time at the peak maximum in milliseconds.<br/>
	 * The retention time is calculated by the intersection of both inflection
	 * point equations (increasing and decreasing equation).<br/>
	 * To get the retention time at maximum of the tabular peak model, call
	 * getRetentionTimeAtPeakMaximum().
	 * 
	 * @return int
	 */
	int getRetentionTimeAtPeakMaximumByInflectionPoints();

	/**
	 * Returns the increasing inflection point abundance (height) of the current
	 * peak model at a given retention time.<br/>
	 * The abundance is given exclusive the background abundance.<br/>
	 * You can call also getIncreasingInflectionPointAbudance(int retentionTime)
	 * to get the increasing abundance at the given retention time.
	 * 
	 * @param retentionTime
	 * @return float
	 */
	float getIncreasingInflectionPointAbundance(int retentionTime);

	/**
	 * Returns the decreasing inflection point abundance (height) of the current
	 * peak model at a given retention time.<br/>
	 * The abundance is given exclusive the background abundance.<br/>
	 * You can call also getDecreasingInflectionPointAbudance(int retentionTime)
	 * to get the increasing abundance at the given retention time.
	 * 
	 * @param retentionTime
	 * @return float
	 */
	float getDecreasingInflectionPointAbundance(int retentionTime);

	// TODO JUnit
	/**
	 * Returns the increasing inflection point equation.
	 * May return null.
	 * 
	 * @return {@link LinearEquation}
	 */
	LinearEquation getIncreasingInflectionPointEquation();

	// TODO JUnit
	/**
	 * Returns the decreasing inflection point equation.
	 * May return null.
	 * 
	 * @return {@link LinearEquation}
	 */
	LinearEquation getDecreasingInflectionPointEquation();
}