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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.support;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.ISegment;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.Segment;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;

import junit.framework.TestCase;

public class Segment_4_Test extends TestCase {

	private ISegment segment;
	private IPoint p1;
	private IPoint p2;
	private IPoint p3;
	private IPoint p4;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		p1 = new Point(1, 2);
		p2 = new Point(10, 20);
		p3 = new Point(100, 200);
		p4 = new Point(1000, 2000);
		segment = new Segment();
	}

	@Override
	protected void tearDown() throws Exception {

		segment = null;
		super.tearDown();
	}

	public void testPoint_1() {

		segment.setChromatogramBaselinePoint1(p1);
		assertEquals("ChromatogramBaselinePoint1 X", 1.0d, segment.getChromatogramBaselinePoint1().getX());
		assertEquals("ChromatogramBaselinePoint1 Y", 2.0d, segment.getChromatogramBaselinePoint1().getY());
	}

	public void testPoint_2() {

		segment.setChromatogramBaselinePoint2(p2);
		assertEquals("ChromatogramBaselinePoint2 X", 10.0d, segment.getChromatogramBaselinePoint2().getX());
		assertEquals("ChromatogramBaselinePoint2 Y", 20.0d, segment.getChromatogramBaselinePoint2().getY());
	}

	public void testPoint_3() {

		segment.setPeakBaselinePoint1(p3);
		assertEquals("PeakBaselinePoint1 X", 100.0d, segment.getPeakBaselinePoint1().getX());
		assertEquals("PeakBaselinePoint1 Y", 200.0d, segment.getPeakBaselinePoint1().getY());
	}

	public void testPoint_4() {

		segment.setPeakBaselinePoint2(p4);
		assertEquals("PeakBaselinePoint2 X", 1000.0d, segment.getPeakBaselinePoint2().getX());
		assertEquals("PeakBaselinePoint2 Y", 2000.0d, segment.getPeakBaselinePoint2().getY());
	}
}
