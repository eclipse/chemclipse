/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
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

import java.util.List;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class AnalysisSupport_4_Test extends TestCase {

	private IAnalysisSupport support;
	private IScanRange scanRange;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		scanRange = new ScanRange(1, 100);
		support = new AnalysisSupport(scanRange, 10);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		support = null;
	}

	public void testGetNumberOfAnalysisSegments_1() {

		assertEquals("NumberOfAnalysisSegments", 10, support.getNumberOfAnalysisSegments());
	}

	public void testSegment_1() {

		List<IAnalysisSegment> segments = support.getAnalysisSegments();
		IAnalysisSegment segment = segments.get(0);
		assertEquals("StartScan", 1, segment.getStartScan());
		assertEquals("StopScan", 10, segment.getStopScan());
		assertEquals("SegmentWidth", 10, segment.getWidth());
	}

	public void testSegment_2() {

		List<IAnalysisSegment> segments = support.getAnalysisSegments();
		IAnalysisSegment segment = segments.get(9);
		assertEquals("StartScan", 91, segment.getStartScan());
		assertEquals("StopScan", 100, segment.getStopScan());
		assertEquals("SegmentWidth", 10, segment.getWidth());
	}
}
