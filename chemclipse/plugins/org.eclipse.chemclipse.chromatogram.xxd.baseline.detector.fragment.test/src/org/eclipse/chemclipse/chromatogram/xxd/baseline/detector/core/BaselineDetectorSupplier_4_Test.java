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
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetectorSupplier;

import junit.framework.TestCase;

public class BaselineDetectorSupplier_4_Test extends TestCase {

	private BaselineDetectorSupplier supplier1;
	private BaselineDetectorSupplier supplier2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier1 = new BaselineDetectorSupplier();
		supplier1.setId("id1");
		supplier1.setDescription("description1");
		supplier1.setDetectorName("detectorName1");
		supplier2 = new BaselineDetectorSupplier();
		supplier2.setId("id2");
		supplier2.setDescription("description2");
		supplier2.setDetectorName("detectorName2");
	}

	@Override
	protected void tearDown() throws Exception {

		supplier1 = null;
		supplier2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertFalse("equals", supplier1.equals(supplier2));
	}

	public void testEquals_2() {

		assertFalse("equals", supplier2.equals(supplier1));
	}

	public void testEquals_3() {

		assertFalse("equals", supplier1.equals(null));
	}

	public void testEquals_4() {

		assertFalse("equals", supplier1.equals("Test"));
	}

	public void testHashCode_1() {

		assertFalse("hashCode", supplier1.hashCode() == supplier2.hashCode());
	}
}
