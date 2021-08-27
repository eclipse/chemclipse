/*******************************************************************************
 * Copyright (c) 2013, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

import junit.framework.TestCase;

public class RetentionTimeWindow_1_Test extends TestCase {

	private RetentionTimeWindow retentionTimeWindow;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		retentionTimeWindow = new RetentionTimeWindow();
	}

	@Override
	protected void tearDown() throws Exception {

		retentionTimeWindow = null;
		super.tearDown();
	}

	public void testGetAllowedNegativeDeviation_1() {

		assertEquals(0.0f, retentionTimeWindow.getAllowedNegativeDeviation());
	}

	public void testGetAllowedNegativeDeviation_2() {

		retentionTimeWindow.setAllowedNegativeDeviation(-3.4f);
		assertEquals(0.0f, retentionTimeWindow.getAllowedNegativeDeviation());
	}

	public void testGetAllowedNegativeDeviation_3() {

		retentionTimeWindow.setAllowedNegativeDeviation(0);
		assertEquals(0.0f, retentionTimeWindow.getAllowedNegativeDeviation());
	}

	public void testGetAllowedNegativeDeviation_4() {

		retentionTimeWindow.setAllowedNegativeDeviation(2.1f);
		assertEquals(2.1f, retentionTimeWindow.getAllowedNegativeDeviation());
	}

	public void testGetAllowedPositiveDeviation_1() {

		assertEquals(0.0f, retentionTimeWindow.getAllowedPositiveDeviation());
	}

	public void testGetAllowedPositiveDeviation_2() {

		retentionTimeWindow.setAllowedPositiveDeviation(5.2f);
		assertEquals(5.2f, retentionTimeWindow.getAllowedPositiveDeviation());
	}

	public void testGetAllowedPositiveDeviation_3() {

		retentionTimeWindow.setAllowedPositiveDeviation(0);
		assertEquals(0.0f, retentionTimeWindow.getAllowedPositiveDeviation());
	}

	public void testGetAllowedPositiveDeviation_4() {

		retentionTimeWindow.setAllowedPositiveDeviation(-2.2f);
		assertEquals(0.0f, retentionTimeWindow.getAllowedPositiveDeviation());
	}

	public void testGetRetentionTime_1() {

		assertEquals(0, retentionTimeWindow.getRetentionTime());
	}

	public void testGetRetentionTime_2() {

		retentionTimeWindow.setRetentionTime(3490);
		assertEquals(3490, retentionTimeWindow.getRetentionTime());
	}

	public void testGetRetentionTime_3() {

		retentionTimeWindow.setRetentionTime(0);
		assertEquals(0, retentionTimeWindow.getRetentionTime());
	}

	public void testGetRetentionTime_4() {

		retentionTimeWindow.setRetentionTime(-90);
		assertEquals(0, retentionTimeWindow.getRetentionTime());
	}

	public void testIsUseMilliseconds_1() {

		assertEquals(true, retentionTimeWindow.isUseMilliseconds());
	}

	public void testIsUseMilliseconds_2() {

		retentionTimeWindow.setUseMilliseconds(false);
		assertEquals(false, retentionTimeWindow.isUseMilliseconds());
	}
}
