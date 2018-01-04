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

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;

public class Segment implements ISegment {

	private IPoint chromatogramBaselinePoint1 = new Point(0, 0);
	private IPoint chromatogramBaselinePoint2 = new Point(0, 0);
	private IPoint peakBaselinePoint1 = new Point(0, 0);
	private IPoint peakBaselinePoint2 = new Point(0, 0);

	public Segment() {
	}

	public Segment(IPoint chromatogramBaselinePoint1, IPoint chromatogramBaselinePoint2, IPoint peakBaselinePoint1, IPoint peakBaselinePoint2) {
		setChromatogramBaselinePoint1(chromatogramBaselinePoint1);
		setChromatogramBaselinePoint2(chromatogramBaselinePoint2);
		setPeakBaselinePoint1(peakBaselinePoint1);
		setPeakBaselinePoint2(peakBaselinePoint2);
	}

	/*
	 * The area methods are implemented in SegmentAreaCalculator.
	 */
	@Override
	public IPoint getChromatogramBaselinePoint1() {

		return chromatogramBaselinePoint1;
	}

	@Override
	public void setChromatogramBaselinePoint1(IPoint point) {

		if(point != null) {
			this.chromatogramBaselinePoint1 = point;
		}
	}

	@Override
	public IPoint getChromatogramBaselinePoint2() {

		return chromatogramBaselinePoint2;
	}

	@Override
	public void setChromatogramBaselinePoint2(IPoint point) {

		if(point != null) {
			this.chromatogramBaselinePoint2 = point;
		}
	}

	@Override
	public IPoint getPeakBaselinePoint1() {

		return peakBaselinePoint1;
	}

	@Override
	public void setPeakBaselinePoint1(IPoint point) {

		if(point != null) {
			this.peakBaselinePoint1 = point;
		}
	}

	@Override
	public IPoint getPeakBaselinePoint2() {

		return peakBaselinePoint2;
	}

	@Override
	public void setPeakBaselinePoint2(IPoint point) {

		if(point != null) {
			this.peakBaselinePoint2 = point;
		}
	}
	// TODO hashCode, equals, toString
}
