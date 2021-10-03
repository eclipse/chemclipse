/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import static org.junit.Assert.assertNotEquals;

import junit.framework.TestCase;

public class MassSpectrumComparisonSupplier_3_Test extends TestCase {

	private MassSpectrumComparisonSupplier supplier1;
	private MassSpectrumComparisonSupplier supplier2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier1 = new MassSpectrumComparisonSupplier();
		supplier1.setId("id");
		supplier1.setDescription("description");
		supplier1.setComparatorName("comparatorName");
		supplier2 = new MassSpectrumComparisonSupplier();
		supplier2.setId("id");
		supplier2.setDescription("description");
		supplier2.setComparatorName("comparatorName");
	}

	@Override
	protected void tearDown() throws Exception {

		supplier1 = null;
		supplier2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertEquals("equals", supplier1, supplier2);
	}

	public void testEquals_2() {

		assertEquals("equals", supplier2, supplier1);
	}

	public void testEquals_3() {

		assertNotNull("equals", supplier1);
	}

	public void testEquals_4() {

		assertNotEquals("equals", supplier1, new Object());
	}

	public void testHashCode_1() {

		assertEquals("hashCode", supplier1.hashCode(), supplier2.hashCode());
	}
}
