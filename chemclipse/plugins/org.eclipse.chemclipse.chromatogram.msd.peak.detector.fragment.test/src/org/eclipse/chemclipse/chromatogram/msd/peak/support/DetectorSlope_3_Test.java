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
package org.eclipse.chemclipse.chromatogram.msd.peak.support;

import org.eclipse.chemclipse.chromatogram.peak.detector.support.DetectorSlope;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IDetectorSlope;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;

import junit.framework.TestCase;

/**
 * ISlope has already been tested in org.eclipse.chemclipse.numeric. Only the
 * IFirstDerivativeSlope additions needs to be tested.
 * 
 * @author eselmeister
 */
public class DetectorSlope_3_Test extends TestCase {

	private IDetectorSlope slope;
	private IPoint p1, p2;
	private int retentionTime;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		p1 = null;
		p2 = null;
		slope = null;
		super.tearDown();
	}

	public void testGetDrift_1() {

		p1 = new Point(5.0d, 10.0d);
		p2 = new Point(7.0d, 3.0d);
		slope = new DetectorSlope(p1, p2, retentionTime);
		assertEquals("Slope", -3.5d, slope.getSlope());
		assertEquals("getDrift", "-", slope.getDrift());
	}

	public void testGetDrift_2() {

		p1 = new Point(7.0d, 3.0d);
		p2 = new Point(7.0d, 3.0d);
		slope = new DetectorSlope(p1, p2, retentionTime);
		assertEquals("Slope", 0.0d, slope.getSlope());
		assertEquals("getDrift", "0", slope.getDrift());
	}

	public void testGetDrift_3() {

		p1 = new Point(5.0d, 3.0d);
		p2 = new Point(7.0d, 10.0d);
		slope = new DetectorSlope(p1, p2, retentionTime);
		assertEquals("Slope", 3.5d, slope.getSlope());
		assertEquals("getDrift", "+", slope.getDrift());
	}
}
