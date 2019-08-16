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
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.support.AnalysisSegment;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;

import junit.framework.TestCase;

public class AnalysisSegment_3_Test extends TestCase {

	private IAnalysisSegment segment;
	private int startScan = -5;
	private int segmentWidth = -5;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		segment = new AnalysisSegment(startScan, segmentWidth);
	}

	@Override
	protected void tearDown() throws Exception {

		segment = null;
		super.tearDown();
	}

	public void testGetStartScan_1() {

		assertEquals("StartScan", 0, segment.getStartScan());
	}

	public void testGetStopScan_1() {

		assertEquals("StopScan", 0, segment.getStopScan());
	}

	public void testGetSegmentWidth_1() {

		assertEquals("SegmentWidth", 0, segment.getSegmentWidth());
	}
}
