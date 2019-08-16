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
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetectorSupplier;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.NoPeakDetectorAvailableException;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class PeakDetectorSupport_1_Test extends TestCase {

	private PeakDetectorMSDSupport support;
	private PeakDetectorMSDSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = new PeakDetectorMSDSupport();
		supplier = new PeakDetectorMSDSupplier("net.first.supplier", "Integrator Description", "Peak Detector Name");
		support.add(supplier);
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		supplier = null;
		super.tearDown();
	}

	public void testGetAvailableIntegratorIds_1() {

		List<String> ids;
		try {
			ids = support.getAvailablePeakDetectorIds();
			assertEquals("getId", "net.first.supplier", ids.get(0));
		} catch(NoPeakDetectorAvailableException e) {
			assertTrue("NoPeakDetectorAvailableException", false);
		}
	}

	public void testGetIntegratorId_1() {

		try {
			String name = support.getPeakDetectorId(0);
			assertEquals("Name", "net.first.supplier", name);
		} catch(NoPeakDetectorAvailableException e) {
			assertTrue("NoPeakDetectorAvailableException", false);
		}
	}

	public void testGetIntegratorSupplier_1() {

		IPeakDetectorSupplier supplier;
		try {
			supplier = support.getPeakDetectorSupplier("net.first.supplier");
			assertNotNull(supplier);
			assertEquals("Name", "Peak Detector Name", supplier.getPeakDetectorName());
		} catch(NoPeakDetectorAvailableException e) {
			assertTrue("NoPeakDetectorAvailableException", false);
		}
	}

	public void testGetIntegratorNames_1() {

		try {
			String[] names = support.getPeakDetectorNames();
			assertEquals("length", 1, names.length);
			assertEquals("name", "Peak Detector Name", names[0]);
		} catch(NoPeakDetectorAvailableException e) {
			assertTrue("NoPeakDetectorAvailableException", false);
		}
	}
}
