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

public class BaselineSegment_5_Test extends TestCase {

	private IBaselineSegment segmentI;
	private IBaselineSegment segmentII;
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
		segmentI = new BaselineSegment(startRetentionTime, stopRetentionTime);
		segmentI.setStartBackgroundAbundance(startBackgroundAbundance);
		segmentI.setStopBackgroundAbundance(stopBackgroundAbundance);
		segmentII = new BaselineSegment(startRetentionTime + 1, stopRetentionTime);
		segmentII.setStartBackgroundAbundance(startBackgroundAbundance);
		segmentII.setStopBackgroundAbundance(stopBackgroundAbundance);
	}

	@Override
	protected void tearDown() throws Exception {

		segmentI = null;
		segmentII = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertFalse("Equals", segmentI.equals(segmentII));
	}

	public void testEquals_2() {

		assertFalse("Equals", segmentII.equals(segmentI));
	}

	public void testEquals_3() {

		assertFalse("Equals", segmentI.equals(null));
	}

	public void testEquals_4() {

		assertFalse("Equals", segmentII.equals(null));
	}

	public void testEquals_5() {

		assertFalse("Equals", segmentI.equals(new String("Test")));
	}

	public void testEquals_6() {

		assertFalse("Equals", segmentII.equals(new String("Test")));
	}
}
