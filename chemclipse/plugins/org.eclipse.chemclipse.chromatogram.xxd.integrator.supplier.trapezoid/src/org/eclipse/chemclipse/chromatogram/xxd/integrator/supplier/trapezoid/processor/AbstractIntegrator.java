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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.Segment;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.SegmentAreaCalculator;
import org.eclipse.chemclipse.numeric.core.Point;

public abstract class AbstractIntegrator {

	/*
	 * ChemStation Factor
	 */
	private static final double CORRECTION_FACTOR_TRAPEZOID = 100.0d;

	/**
	 * Calculate the area of given retention time segment.
	 * This method should only calculate the absolute area.
	 * Any other correction shall be calculated separately.
	 * 
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 * @param signalAbundanceStart
	 * @param signalAbundanceStop
	 * @return double
	 */
	protected double calculateArea(int startRetentionTime, int stopRetentionTime, float signalAbundanceStart, float signalAbundanceStop) {

		return calculateArea(startRetentionTime, stopRetentionTime, signalAbundanceStart, signalAbundanceStop, 0.0f, 0.0f);
	}

	/**
	 * Calculate the area of given retention time segment.
	 * This method should only calculate the absolute area.
	 * Any other correction shall be calculated separately.
	 * 
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 * @param signalAbundanceStart
	 * @param signalAbundanceStop
	 * @param baselineAbundanceStart
	 * @param baselineAbundanceStop
	 * @return double
	 */
	protected double calculateArea(int startRetentionTime, int stopRetentionTime, float signalAbundanceStart, float signalAbundanceStop, float baselineAbundanceStart, float baselineAbundanceStop) {

		/*
		 * Signal and Baseline (Zero) by default
		 */
		Point baselineStart = new Point(startRetentionTime, baselineAbundanceStart);
		Point baselineStop = new Point(stopRetentionTime, baselineAbundanceStop);
		Point signalStart = new Point(startRetentionTime, signalAbundanceStart);
		Point signalStop = new Point(stopRetentionTime, signalAbundanceStop);
		Segment segment = new Segment(baselineStart, baselineStop, signalStart, signalStop);
		//
		return SegmentAreaCalculator.calculateSegmentArea(segment) / CORRECTION_FACTOR_TRAPEZOID;
	}
}