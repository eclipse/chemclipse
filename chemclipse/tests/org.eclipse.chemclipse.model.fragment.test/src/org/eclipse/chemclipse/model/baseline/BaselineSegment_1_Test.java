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
package org.eclipse.chemclipse.model.baseline;

import org.eclipse.chemclipse.model.baseline.BaselineSegment;
import org.eclipse.chemclipse.model.baseline.IBaselineSegment;

import junit.framework.TestCase;

public class BaselineSegment_1_Test extends TestCase {

	private IBaselineSegment segment;
	private int startRetentionTime;
	private int stopRetentionTime;
	private float startBackgroundAbundance;
	private float stopBackgroundAbundance;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		startRetentionTime = 4500;
		stopRetentionTime = 10500;
		startBackgroundAbundance = 500.0f;
		stopBackgroundAbundance = 4000.0f;
		segment = new BaselineSegment(startRetentionTime, stopRetentionTime);
		segment.setStartBackgroundAbundance(startBackgroundAbundance);
		segment.setStopBackgroundAbundance(stopBackgroundAbundance);
	}

	@Override
	protected void tearDown() throws Exception {

		segment = null;
		super.tearDown();
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("StartRetentionTime", startRetentionTime, segment.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("StopRetentionTime", stopRetentionTime, segment.getStopRetentionTime());
	}

	public void testGetStartBackgroundAbundance_1() {

		assertEquals("StartBackgroundAbundance", startBackgroundAbundance, segment.getStartBackgroundAbundance());
	}

	public void testGetStopBackgroundAbundance_1() {

		assertEquals("StopBackgroundAbundance", stopBackgroundAbundance, segment.getStopBackgroundAbundance());
	}
}
