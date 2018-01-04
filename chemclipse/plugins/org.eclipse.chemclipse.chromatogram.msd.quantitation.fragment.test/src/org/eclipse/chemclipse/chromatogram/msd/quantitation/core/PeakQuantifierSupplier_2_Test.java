/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import junit.framework.TestCase;

public class PeakQuantifierSupplier_2_Test extends TestCase {

	private PeakQuantifierSupplier supplier;
	private String id = "id";
	private String description = "description";
	private String detectorName = "detectorName";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier = new PeakQuantifierSupplier();
		supplier.setId(id);
		supplier.setDescription(description);
		supplier.setPeakQuantifierName(detectorName);
	}

	@Override
	protected void tearDown() throws Exception {

		supplier = null;
		super.tearDown();
	}

	public void testGetId_1() {

		assertEquals("id", id, supplier.getId());
		id = "newId";
		supplier.setId(id);
		assertEquals("id", id, supplier.getId());
	}

	public void testGetDescription_1() {

		assertEquals("description", description, supplier.getDescription());
		description = "newDescription";
		supplier.setDescription(description);
		assertEquals("description", description, supplier.getDescription());
	}

	public void testGetDetectorName_1() {

		assertEquals("PeakQuantifierName", detectorName, supplier.getPeakQuantifierName());
		detectorName = "newDetectorName";
		supplier.setPeakQuantifierName(detectorName);
		assertEquals("detectorName", detectorName, supplier.getPeakQuantifierName());
	}
}
