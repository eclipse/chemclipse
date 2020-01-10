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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.exceptions.NoBaselineDetectorAvailableException;

import junit.framework.TestCase;

/**
 * Test the IBaselineDetectorSupport.
 * 
 * @author eselmeister
 */
public class BaselineDetector_1_Test extends TestCase {

	IBaselineDetectorSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = BaselineDetector.getBaselineDetectorSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		super.tearDown();
	}

	public void testGetMassSpectrumComparatorSupport_1() throws NoBaselineDetectorAvailableException {

		int count = 0;
		String[] names = support.getDetectorNames();
		String[] rcs = new String[1];
		rcs[0] = "Threshold (TIC) Baseline Detector";
		for(String name : names) {
			for(String rc : rcs) {
				if(name.equals(rc)) {
					count++;
				}
			}
		}
		assertEquals("Registered Detector Names", 1, count);
	}

	public void testGetMassSpectrumComparatorSupport_2() throws NoBaselineDetectorAvailableException {

		int count = 0;
		List<String> ids = support.getAvailableDetectorIds();
		String[] rcs = new String[1];
		rcs[0] = "org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.tic";
		for(String id : ids) {
			for(String rc : rcs) {
				if(id.equals(rc)) {
					count++;
				}
			}
		}
		assertEquals("Registered Detector Ids", 1, count);
	}

	public void testGetMassSpectrumComparatorSupport_3() throws NoBaselineDetectorAvailableException {

		List<String> ids = support.getAvailableDetectorIds();
		List<String> rcs = new ArrayList<String>();
		for(String id : ids) {
			rcs.add(id);
		}
		String id;
		for(int i = 0; i < rcs.size(); i++) {
			id = support.getDetectorId(i);
			assertEquals("getDetectorId", id, rcs.get(i));
		}
	}

	public void testGetMassSpectrumComparisonSupplier_1() {

		try {
			support.getBaselineDetectorSupplier("");
		} catch(NoBaselineDetectorAvailableException e) {
			assertTrue("NoBaselineDetectorAvailableException", true);
		}
	}

	public void testGetMassSpectrumComparisonSupplier_2() {

		try {
			support.getBaselineDetectorSupplier(null);
		} catch(NoBaselineDetectorAvailableException e) {
			assertTrue("NoBaselineDetectorAvailableException", true);
		}
	}

	public void testGetMassSpectrumComparisonSupplier_3() throws NoBaselineDetectorAvailableException {

		String comparatorName = "Threshold (TIC) Baseline Detector";
		String description = "This baseline detector calculator sets the baseline by the lowest TIC value.";
		String id = "org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.tic";
		IBaselineDetectorSupplier supplier = support.getBaselineDetectorSupplier(id);
		assertEquals("DetectorName", comparatorName, supplier.getDetectorName());
		assertEquals("Description", description, supplier.getDescription());
		assertEquals("Id", id, supplier.getId());
	}
}
