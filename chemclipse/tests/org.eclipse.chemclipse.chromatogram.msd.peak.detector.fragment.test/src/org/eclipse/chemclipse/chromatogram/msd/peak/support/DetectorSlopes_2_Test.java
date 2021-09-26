/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.peak.detector.support.DetectorSlope;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.DetectorSlopes;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IDetectorSlope;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IDetectorSlopes;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;

import junit.framework.TestCase;

/**
 * Testing calculateMovingAverage
 * 
 * @author Philip Wenig
 */
public class DetectorSlopes_2_Test extends TestCase {

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
		abundances.add(21782.39437f);
		abundances.add(21623.94366f);
		abundances.add(21896.47887f);
		abundances.add(21348.59155f);
		abundances.add(22217.60563f);
		abundances.add(27317.60563f);
		abundances.add(45388.02817f);
		abundances.add(84380.98592f);
		abundances.add(127508.4507f);
		abundances.add(153907.7465f);
		abundances.add(153160.5634f);
		abundances.add(133292.9577f);
		abundances.add(109999.2958f);
		abundances.add(90078.16901f);
		abundances.add(75899.29577f);
		abundances.add(61307.04225f);
		abundances.add(50657.04225f);
		abundances.add(42513.38028f);
		abundances.add(37465.49296f);
		abundances.add(32107.74648f);
		abundances.add(29959.15493f);
		abundances.add(27964.78873f);
		abundances.add(26906.33803f);
		abundances.add(24441.5493f);
		abundances.add(23981.69014f);
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

		assertEquals("Size", 26, slopes.size());
	}

	// -----------------------------------------------WindowSize.SCANS_3
	public void testCalculateMovingAverage_1() {

		slopes.calculateMovingAverage(3);
		slope = slopes.getDetectorSlope(1);
		assertEquals("scan 1 slope", 0.1549296875, slope.getSlope());
		assertEquals("scan 1 retention time", 1000, slope.getRetentionTime());
	}

	public void testCalculateMovingAverage_2() {

		slopes.calculateMovingAverage(3);
		slope = slopes.getDetectorSlope(2);
		assertEquals("scan 2 slope", 0.020187499999999997, slope.getSlope());
		assertEquals("scan 2 retention time", 2000, slope.getRetentionTime());
	}

	public void testCalculateMovingAverage_3() {

		slopes.calculateMovingAverage(3);
		slope = slopes.getDetectorSlope(25);
		assertEquals("scan 25 slope", -1.5866998856461185, slope.getSlope());
		assertEquals("scan 25 retention time", 25000, slope.getRetentionTime());
	}

	public void testCalculateMovingAverage_4() {

		slopes.calculateMovingAverage(3);
		slope = slopes.getDetectorSlope(26);
		assertEquals("scan 26 slope", -0.459859375, slope.getSlope());
		assertEquals("scan 26 retention time", 26000, slope.getRetentionTime());
	}

	// -----------------------------------------------WindowSize.SCANS_3
	// -----------------------------------------------WindowSize.SCANS_5
	public void testCalculateMovingAverage_5() {

		slopes.calculateMovingAverage(5);
		slope = slopes.getDetectorSlope(2);
		assertEquals("scan 2 slope", 0.064083984375, slope.getSlope());
		assertEquals("scan 2 retention time", 2000, slope.getRetentionTime());
	}

	public void testCalculateMovingAverage_6() {

		slopes.calculateMovingAverage(5);
		slope = slopes.getDetectorSlope(3);
		assertEquals("scan 3 slope", -0.04295781249999999, slope.getSlope());
		assertEquals("scan 3 retention time", 3000, slope.getRetentionTime());
	}

	public void testCalculateMovingAverage_7() {

		slopes.calculateMovingAverage(5);
		slope = slopes.getDetectorSlope(24);
		assertEquals("scan 24 slope", -1.9802620761875385, slope.getSlope());
		assertEquals("scan 24 retention time", 24000, slope.getRetentionTime());
	}

	public void testCalculateMovingAverage_8() {

		slopes.calculateMovingAverage(5);
		slope = slopes.getDetectorSlope(25);
		assertEquals("scan 25 slope", -2.4647890625, slope.getSlope());
		assertEquals("scan 25 retention time", 25000, slope.getRetentionTime());
	}

	// -----------------------------------------------WindowSize.SCANS_5
	// -----------------------------------------------WindowSize.SCANS_7
	public void testCalculateMovingAverage_9() {

		slopes.calculateMovingAverage(7);
		slope = slopes.getDetectorSlope(3);
		assertEquals("scan 3 slope", -0.158451171875, slope.getSlope());
		assertEquals("scan 3 retention time", 3000, slope.getRetentionTime());
	}

	public void testCalculateMovingAverage_10() {

		slopes.calculateMovingAverage(7);
		slope = slopes.getDetectorSlope(4);
		assertEquals("scan 4 slope", 0.8220320870535714, slope.getSlope());
		assertEquals("scan 4 retention time", 4000, slope.getRetentionTime());
	}

	public void testCalculateMovingAverage_11() {

		slopes.calculateMovingAverage(7);
		slope = slopes.getDetectorSlope(23);
		assertEquals("scan 23 slope", -2.879118316072511, slope.getSlope());
		assertEquals("scan 23 retention time", 23000, slope.getRetentionTime());
	}

	public void testCalculateMovingAverage_12() {

		slopes.calculateMovingAverage(7);
		slope = slopes.getDetectorSlope(24);
		assertEquals("scan 24 slope", -1.058451171875, slope.getSlope());
		assertEquals("scan 24 retention time", 24000, slope.getRetentionTime());
	}
	// -----------------------------------------------WindowSize.SCANS_7
}
