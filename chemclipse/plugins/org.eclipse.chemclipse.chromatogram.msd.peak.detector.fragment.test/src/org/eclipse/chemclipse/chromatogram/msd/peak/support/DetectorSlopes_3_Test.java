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

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.DetectorSlope;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.DetectorSlopes;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IDetectorSlope;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IDetectorSlopes;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

import junit.framework.TestCase;

/**
 * Testing calculateMovingAverage
 * 
 * @author eselmeister
 */
public class DetectorSlopes_3_Test extends TestCase {

	private IDetectorSlope slope;
	private IDetectorSlopes slopes;
	private IPoint p1, p2;
	private int retentionTime;
	private List<Float> abundances;
	private ITotalScanSignals signals;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		abundances = new ArrayList<Float>();
		abundances.add(21563.38028f);
		abundances.add(21718.30986f);
		signals = EasyMock.createMock(ITotalScanSignals.class);
		EasyMock.expect(signals.getStartScan()).andStubReturn(1);
		EasyMock.expect(signals.getStopScan()).andStubReturn(abundances.size());
		EasyMock.replay(signals);
		slopes = new DetectorSlopes(signals);
		for(int i = 1; i < abundances.size(); i++) {
			retentionTime = i * 1000;
			p1 = new Point(retentionTime, abundances.get(i - 1));
			p2 = new Point((i + 1) * 1000, abundances.get(i));
			slope = new DetectorSlope(p1, p2, retentionTime);
			slopes.add(slope);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		abundances = null;
		p1 = null;
		p2 = null;
		slope = null;
		slopes = null;
		super.tearDown();
	}

	public void testSize_1() {

		assertEquals("Size", 1, slopes.size());
	}

	// -----------------------------------------------WindowSize.SCANS_3
	public void testCalculateMovingAverage_1() {

		slopes.calculateMovingAverage(WindowSize.WIDTH_3);
		slope = slopes.getDetectorSlope(1);
		assertEquals("scan 1 slope", 0.1549296875, slope.getSlope());
		assertEquals("scan 1 retention time", 1000, slope.getRetentionTime());
	}
}
