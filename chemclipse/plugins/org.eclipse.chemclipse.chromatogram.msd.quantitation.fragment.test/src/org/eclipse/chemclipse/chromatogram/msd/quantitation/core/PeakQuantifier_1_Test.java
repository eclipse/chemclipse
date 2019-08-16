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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.exceptions.NoPeakQuantifierAvailableException;

import junit.framework.TestCase;

public class PeakQuantifier_1_Test extends TestCase {

	IPeakQuantifierSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = PeakQuantifier.getPeakQuantifierSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		super.tearDown();
	}

	public void testGetPeakQuantifierSupport_1() throws NoPeakQuantifierAvailableException {

		int count = 0;
		String[] names = support.getPeakQuantifierNames();
		String[] rcs = new String[1];
		rcs[0] = "Peak Quantifier (ESTD)";
		for(String name : names) {
			for(String rc : rcs) {
				if(name.equals(rc)) {
					count++;
				}
			}
		}
		assertEquals("Registered Detector Names", 1, count);
	}

	public void testGetMassSpectrumComparatorSupport_2() throws NoPeakQuantifierAvailableException {

		int count = 0;
		List<String> ids = support.getAvailablePeakQuantifierIds();
		String[] rcs = new String[1];
		rcs[0] = "org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.peak.estd";
		for(String id : ids) {
			for(String rc : rcs) {
				if(id.equals(rc)) {
					count++;
				}
			}
		}
		assertEquals("Registered Detector Ids", 1, count);
	}

	public void testGetMassSpectrumComparatorSupport_3() throws NoPeakQuantifierAvailableException {

		List<String> ids = support.getAvailablePeakQuantifierIds();
		List<String> rcs = new ArrayList<String>();
		for(String id : ids) {
			rcs.add(id);
		}
		String id;
		for(int i = 0; i < rcs.size(); i++) {
			id = support.getPeakQuantifierId(i);
			assertEquals("getDetectorId", id, rcs.get(i));
		}
	}

	public void testGetMassSpectrumComparisonSupplier_1() {

		try {
			support.getPeakQuantifierSupplier("");
		} catch(NoPeakQuantifierAvailableException e) {
			assertTrue("NoPeakQuantifierAvailableException", true);
		}
	}

	public void testGetMassSpectrumComparisonSupplier_2() {

		try {
			support.getPeakQuantifierSupplier(null);
		} catch(NoPeakQuantifierAvailableException e) {
			assertTrue("NoPeakQuantifierAvailableException", true);
		}
	}

	public void testGetMassSpectrumComparisonSupplier_3() throws NoPeakQuantifierAvailableException {

		String comparatorName = "Peak Quantifier (ESTD)";
		String description = "This quantifier handles to execute a peak quantitation via external standards.";
		String id = "org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.peak.estd";
		IPeakQuantifierSupplier supplier = support.getPeakQuantifierSupplier(id);
		assertEquals("DetectorName", comparatorName, supplier.getPeakQuantifierName());
		assertEquals("Description", description, supplier.getDescription());
		assertEquals("Id", id, supplier.getId());
	}
}
