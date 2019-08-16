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

import java.util.List;

import org.eclipse.chemclipse.model.support.AnalysisSupport;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.IAnalysisSupport;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class AnalysisSupport_6_Test extends TestCase {

	private IAnalysisSupport support;
	private IScanRange scanRange;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		scanRange = new ScanRange(178, 250);
		support = new AnalysisSupport(scanRange, 10);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		support = null;
	}

	public void testGetNumberOfAnalysisSegments_1() {

		assertEquals("NumberOfAnalysisSegments", 8, support.getNumberOfAnalysisSegments());
	}

	public void testSegment_1() {

		List<IAnalysisSegment> segments = support.getAnalysisSegments();
		IAnalysisSegment segment = segments.get(0);
		assertEquals("StartScan", 178, segment.getStartScan());
		assertEquals("StopScan", 187, segment.getStopScan());
		assertEquals("SegmentWidth", 10, segment.getSegmentWidth());
	}

	public void testSegment_2() {

		List<IAnalysisSegment> segments = support.getAnalysisSegments();
		IAnalysisSegment segment = segments.get(7);
		assertEquals("StartScan", 248, segment.getStartScan());
		assertEquals("StopScan", 250, segment.getStopScan());
		assertEquals("SegmentWidth", 3, segment.getSegmentWidth());
	}
}
