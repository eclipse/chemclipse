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

import junit.framework.TestCase;

public class Segment_2_Test extends TestCase {

	private ISegment segment;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		segment = new Segment(null, null, null, null);
	}

	@Override
	protected void tearDown() throws Exception {

		segment = null;
		super.tearDown();
	}

	public void testPoint_1() {

		assertNotNull("ChromatogramBaselinePoint1", segment.getChromatogramBaselinePoint1());
	}

	public void testPoint_2() {

		assertNotNull("ChromatogramBaselinePoint2", segment.getChromatogramBaselinePoint2());
	}

	public void testPoint_3() {

		assertNotNull("PeakBaselinePoint1", segment.getPeakBaselinePoint1());
	}

	public void testPoint_4() {

		assertNotNull("PeakBaselinePoint2", segment.getPeakBaselinePoint2());
	}
}
