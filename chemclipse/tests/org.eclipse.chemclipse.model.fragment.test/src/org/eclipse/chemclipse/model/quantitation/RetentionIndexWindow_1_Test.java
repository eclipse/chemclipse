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

public class RetentionIndexWindow_1_Test extends TestCase {

	private RetentionIndexWindow retentionIndexWindow;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		retentionIndexWindow = new RetentionIndexWindow();
	}

	@Override
	protected void tearDown() throws Exception {

		retentionIndexWindow = null;
		super.tearDown();
	}

	public void testGetAllowedNegativeDeviation_1() {

		assertEquals(0.0f, retentionIndexWindow.getAllowedNegativeDeviation());
	}

	public void testGetAllowedNegativeDeviation_2() {

		retentionIndexWindow.setAllowedNegativeDeviation(-3.4f);
		assertEquals(0.0f, retentionIndexWindow.getAllowedNegativeDeviation());
	}

	public void testGetAllowedNegativeDeviation_3() {

		retentionIndexWindow.setAllowedNegativeDeviation(0);
		assertEquals(0.0f, retentionIndexWindow.getAllowedNegativeDeviation());
	}

	public void testGetAllowedNegativeDeviation_4() {

		retentionIndexWindow.setAllowedNegativeDeviation(2.1f);
		assertEquals(2.1f, retentionIndexWindow.getAllowedNegativeDeviation());
	}

	public void testGetAllowedPositiveDeviation_1() {

		assertEquals(0.0f, retentionIndexWindow.getAllowedPositiveDeviation());
	}

	public void testGetAllowedPositiveDeviation_2() {

		retentionIndexWindow.setAllowedPositiveDeviation(5.2f);
		assertEquals(5.2f, retentionIndexWindow.getAllowedPositiveDeviation());
	}

	public void testGetAllowedPositiveDeviation_3() {

		retentionIndexWindow.setAllowedPositiveDeviation(0);
		assertEquals(0.0f, retentionIndexWindow.getAllowedPositiveDeviation());
	}

	public void testGetAllowedPositiveDeviation_4() {

		retentionIndexWindow.setAllowedPositiveDeviation(-2.2f);
		assertEquals(0.0f, retentionIndexWindow.getAllowedPositiveDeviation());
	}

	public void testGetRetentionIndex_1() {

		assertEquals(0.0f, retentionIndexWindow.getRetentionIndex());
	}

	public void testGetRetentionIndex_2() {

		retentionIndexWindow.setRetentionIndex(895.2f);
		assertEquals(895.2f, retentionIndexWindow.getRetentionIndex());
	}

	public void testGetRetentionIndex_3() {

		retentionIndexWindow.setRetentionIndex(0);
		assertEquals(0.0f, retentionIndexWindow.getRetentionIndex());
	}

	public void testGetRetentionIndex_4() {

		retentionIndexWindow.setRetentionIndex(-90);
		assertEquals(0.0f, retentionIndexWindow.getRetentionIndex());
	}
}
