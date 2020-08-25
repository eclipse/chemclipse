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

import junit.framework.TestCase;

public class AnalysisSegment_2_Test extends TestCase {

	private IAnalysisSegment segment;
	private final int startScan = 0;
	private final int segmentWidth = 0;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		segment = new AnalysisSegment(startScan, segmentWidth) {

			@Override
			public int getStartRetentionTime() {

				return 0;
			}

			@Override
			public int getStopRetentionTime() {

				return 0;
			}
		};
	}

	@Override
	protected void tearDown() throws Exception {

		segment = null;
		super.tearDown();
	}

	public void testGetStartScan_1() {

		assertEquals("StartScan", startScan, segment.getStartScan());
	}

	public void testGetStopScan_1() {

		assertEquals("StopScan", 0, segment.getStopScan());
	}

	public void testGetSegmentWidth_1() {

		assertEquals("SegmentWidth", segmentWidth, segment.getWidth());
	}
}
