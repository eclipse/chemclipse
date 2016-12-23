/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.peak.detector.core.PeakDetectorSupplier;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class PeakDetectorSupplier_2_Test extends TestCase {

	private PeakDetectorSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier = new PeakDetectorSupplier("", "", "");
	}

	@Override
	protected void tearDown() throws Exception {

		supplier = null;
		super.tearDown();
	}

	public void testGetId_1() {

		assertEquals("Id", "", supplier.getId());
	}

	public void testGetDescription_1() {

		assertEquals("Description", "", supplier.getDescription());
	}

	public void testGetFilterName_1() {

		assertEquals("Name", "", supplier.getPeakDetectorName());
	}
}
