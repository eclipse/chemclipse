/*******************************************************************************
 * Copyright (c) 2013, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.core;

import static org.junit.Assert.assertNotEquals;

import junit.framework.TestCase;

public class PeakQuantifierSupplier_4_Test extends TestCase {

	private PeakQuantifierSupplier supplier1;
	private PeakQuantifierSupplier supplier2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier1 = new PeakQuantifierSupplier();
		supplier1.setId("id1");
		supplier1.setDescription("description1");
		supplier1.setPeakQuantifierName("detectorName1");
		supplier2 = new PeakQuantifierSupplier();
		supplier2.setId("id2");
		supplier2.setDescription("description2");
		supplier2.setPeakQuantifierName("detectorName2");
	}

	@Override
	protected void tearDown() throws Exception {

		supplier1 = null;
		supplier2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertNotEquals("equals", supplier1, supplier2);
	}

	public void testEquals_2() {

		assertNotEquals("equals", supplier2, supplier1);
	}

	public void testEquals_3() {

		assertNotNull("equals", supplier1);
	}

	public void testEquals_4() {

		assertNotEquals("equals", supplier1, new Object());
	}

	public void testHashCode_1() {

		assertNotEquals("hashCode", supplier1.hashCode(), supplier2.hashCode());
	}
}
