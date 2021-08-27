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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.support;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;

import junit.framework.TestCase;

public class SegmentAreaCalculator_1_Test extends TestCase {

	private IPoint chromatogramBaselinePoint1;
	private IPoint chromatogramBaselinePoint2;
	private IPoint peakBaselinePoint1;
	private IPoint peakBaselinePoint2;
	private ISegment segment;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCalculator_1() {

		/*
		 * Chromatogram baseline linear, 0 Peak baseline increasing
		 */
		peakBaselinePoint1 = new Point(1, 2);
		peakBaselinePoint2 = new Point(4, 3);
		chromatogramBaselinePoint1 = new Point(1, 0);
		chromatogramBaselinePoint2 = new Point(4, 0);
		segment = new Segment(chromatogramBaselinePoint1, chromatogramBaselinePoint2, peakBaselinePoint1, peakBaselinePoint2);
		assertEquals("Area", 7.5d, SegmentAreaCalculator.calculateSegmentArea(segment));
	}

	public void testCalculator_2() {

		/*
		 * Chromatogram baseline decreasing Peak baseline increasing
		 */
		peakBaselinePoint1 = new Point(1, 2);
		peakBaselinePoint2 = new Point(4, 3);
		chromatogramBaselinePoint1 = new Point(1, 1);
		chromatogramBaselinePoint2 = new Point(4, 0);
		segment = new Segment(chromatogramBaselinePoint1, chromatogramBaselinePoint2, peakBaselinePoint1, peakBaselinePoint2);
		assertEquals("Area", 6.0d, SegmentAreaCalculator.calculateSegmentArea(segment));
	}

	public void testCalculator_3() {

		/*
		 * Chromatogram baseline linear above peak baseline Peak baseline
		 * increasing
		 */
		peakBaselinePoint1 = new Point(1, 2);
		peakBaselinePoint2 = new Point(4, 3);
		chromatogramBaselinePoint1 = new Point(1, 4);
		chromatogramBaselinePoint2 = new Point(4, 4);
		segment = new Segment(chromatogramBaselinePoint1, chromatogramBaselinePoint2, peakBaselinePoint1, peakBaselinePoint2);
		assertEquals("Area", -4.5d, SegmentAreaCalculator.calculateSegmentArea(segment));
	}

	public void testCalculator_4() {

		/*
		 * Chromatogram baseline linear above peak baseline Peak baseline
		 * increasing
		 */
		peakBaselinePoint1 = new Point(1, 2);
		peakBaselinePoint2 = new Point(4, 3);
		chromatogramBaselinePoint1 = new Point(1, 5);
		chromatogramBaselinePoint2 = new Point(4, 4);
		segment = new Segment(chromatogramBaselinePoint1, chromatogramBaselinePoint2, peakBaselinePoint1, peakBaselinePoint2);
		assertEquals("Area", -6.0d, SegmentAreaCalculator.calculateSegmentArea(segment));
	}

	public void testCalculator_5() {

		/*
		 * Chromatogram baseline and peak baseline are crossing Peak baseline
		 * increasing Chromatogram baseline linear
		 */
		peakBaselinePoint1 = new Point(1, 1);
		peakBaselinePoint2 = new Point(5, 5);
		chromatogramBaselinePoint1 = new Point(1, 3);
		chromatogramBaselinePoint2 = new Point(5, 3);
		segment = new Segment(chromatogramBaselinePoint1, chromatogramBaselinePoint2, peakBaselinePoint1, peakBaselinePoint2);
		assertEquals("Area", 0.0d, SegmentAreaCalculator.calculateSegmentArea(segment));
	}

	public void testCalculator_6() {

		/*
		 * Chromatogram baseline and peak baseline are crossing Peak baseline
		 * linear Chromatogram baseline increasing
		 */
		peakBaselinePoint1 = new Point(1, 3);
		peakBaselinePoint2 = new Point(5, 3);
		chromatogramBaselinePoint1 = new Point(1, 1);
		chromatogramBaselinePoint2 = new Point(5, 5);
		segment = new Segment(chromatogramBaselinePoint1, chromatogramBaselinePoint2, peakBaselinePoint1, peakBaselinePoint2);
		assertEquals("Area", 0.0d, SegmentAreaCalculator.calculateSegmentArea(segment));
	}

	public void testCalculator_7() {

		/*
		 * Chromatogram baseline and peak baseline are crossing Peak baseline
		 * decreasing Chromatogram baseline increasing
		 */
		peakBaselinePoint1 = new Point(1, 5);
		peakBaselinePoint2 = new Point(5, 1);
		chromatogramBaselinePoint1 = new Point(1, 1);
		chromatogramBaselinePoint2 = new Point(5, 5);
		segment = new Segment(chromatogramBaselinePoint1, chromatogramBaselinePoint2, peakBaselinePoint1, peakBaselinePoint2);
		assertEquals("Area", 0.0d, SegmentAreaCalculator.calculateSegmentArea(segment));
	}

	public void testCalculator_8() {

		/*
		 * Chromatogram baseline and peak baseline are crossing Peak baseline
		 * decreasing Chromatogram baseline increasing
		 */
		peakBaselinePoint1 = new Point(1, 5);
		peakBaselinePoint2 = new Point(6, 0);
		chromatogramBaselinePoint1 = new Point(1, 1);
		chromatogramBaselinePoint2 = new Point(6, 6);
		segment = new Segment(chromatogramBaselinePoint1, chromatogramBaselinePoint2, peakBaselinePoint1, peakBaselinePoint2);
		assertEquals("Area", -5.0d, SegmentAreaCalculator.calculateSegmentArea(segment));
	}

	public void testCalculator_9() {

		/*
		 * Chromatogram baseline and peak baseline are crossing Peak baseline
		 * decreasing Chromatogram baseline increasing
		 */
		peakBaselinePoint1 = new Point(0, 6);
		peakBaselinePoint2 = new Point(5, 1);
		chromatogramBaselinePoint1 = new Point(0, 0);
		chromatogramBaselinePoint2 = new Point(5, 5);
		segment = new Segment(chromatogramBaselinePoint1, chromatogramBaselinePoint2, peakBaselinePoint1, peakBaselinePoint2);
		assertEquals("Area", 5.0d, SegmentAreaCalculator.calculateSegmentArea(segment));
	}
}
