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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.internal.core.settings;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.internal.core.settings.IRetentionTimeRange;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.internal.core.settings.RetentionTimeRange;

import junit.framework.TestCase;

public class RetentionTimeRange_1_Test extends TestCase {

	private IRetentionTimeRange retentionTimeRange;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetRetentionTime_1() {

		retentionTimeRange = new RetentionTimeRange(0, 0);
		assertEquals("Start", 0, retentionTimeRange.getStartRetentionTime());
		assertEquals("Stop", 0, retentionTimeRange.getStopRetentionTime());
	}

	public void testGetRetentionTime_2() {

		retentionTimeRange = new RetentionTimeRange(1500, 2500);
		assertEquals("Start", 1500, retentionTimeRange.getStartRetentionTime());
		assertEquals("Stop", 2500, retentionTimeRange.getStopRetentionTime());
	}

	public void testGetRetentionTime_3() {

		retentionTimeRange = new RetentionTimeRange(-1, 2500);
		assertEquals("Start", 0, retentionTimeRange.getStartRetentionTime());
		assertEquals("Stop", 2500, retentionTimeRange.getStopRetentionTime());
	}

	public void testGetRetentionTime_4() {

		retentionTimeRange = new RetentionTimeRange(1500, -1);
		assertEquals("Start", 0, retentionTimeRange.getStartRetentionTime());
		assertEquals("Stop", 1500, retentionTimeRange.getStopRetentionTime());
	}

	public void testGetRetentionTime_5() {

		retentionTimeRange = new RetentionTimeRange(2500, 1500);
		assertEquals("Start", 1500, retentionTimeRange.getStartRetentionTime());
		assertEquals("Stop", 2500, retentionTimeRange.getStopRetentionTime());
	}
}
