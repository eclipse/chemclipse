/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.core;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.ISegment;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.Segment;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.SegmentAreaCalculator;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;

public abstract class AbstractSumareaIntegrator implements ISumareaIntegrator {

	protected double calculateArea(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) {

		IPoint psp1, psp2;
		IPoint pbp1, pbp2;
		ISegment segment;
		double integratedArea = 0.0f;
		/*
		 * Calculate the area of the peak in the given retention time
		 * segment.
		 */
		psp1 = new Point(startRetentionTime / ISumareaIntegrator.INTEGRATION_STEPS, startAbundance);
		psp2 = new Point(stopRetentionTime / ISumareaIntegrator.INTEGRATION_STEPS, stopAbundance);
		pbp1 = new Point(startRetentionTime / ISumareaIntegrator.INTEGRATION_STEPS, 0.0d);
		pbp2 = new Point(stopRetentionTime / ISumareaIntegrator.INTEGRATION_STEPS, 0.0d);
		segment = new Segment(pbp1, pbp2, psp1, psp2);
		integratedArea = SegmentAreaCalculator.calculateSegmentArea(segment);
		return integratedArea;
	}
}
