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

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class AnalysisSupport_1_Test extends TestCase {

	private IAnalysisSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = new AnalysisSupport(5726, 13);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		support = null;
	}

	public void testGetNumberOfAnalysisSegments_1() {

		assertEquals("NumberOfAnalysisSegments", 441, support.getNumberOfAnalysisSegments());
	}

	public void testSegment_1() {

		List<IAnalysisSegment> segments = support.getAnalysisSegments();
		IAnalysisSegment segment = segments.get(439);
		assertEquals("StartScan", 5708, segment.getStartScan());
		assertEquals("StopScan", 5720, segment.getStopScan());
		assertEquals("SegmentWidth", 13, segment.getSegmentWidth());
	}

	public void testSegment_2() {

		List<IAnalysisSegment> segments = support.getAnalysisSegments();
		IAnalysisSegment segment = segments.get(440);
		assertEquals("StartScan", 5721, segment.getStartScan());
		assertEquals("StopScan", 5726, segment.getStopScan());
		assertEquals("SegmentWidth", 6, segment.getSegmentWidth());
	}
}
