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
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;

import junit.framework.TestCase;

public class IonPercentages_1_Test extends TestCase {

	private IScanMSD massSpectrum;
	private IIon defaultIon;
	private IIonPercentages ionPercentages;
	private Map<Integer, Float> ions;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ions = new HashMap<Integer, Float>();
		ions.put(45, 5000.0f);
		ions.put(55, 500.0f);
		ions.put(65, 250.0f);
		ions.put(75, 760.0f);
		ions.put(85, 8800.0f);
		massSpectrum = new ScanMSD();
		for(Integer ion : ions.keySet()) {
			defaultIon = new Ion(ion, ions.get(ion));
			massSpectrum.addIon(defaultIon);
		}
		ionPercentages = new IonPercentages(massSpectrum);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testMassSpectrum_1() {

		assertEquals("TotalSignal", 15310.0f, massSpectrum.getTotalSignal());
	}

	public void testIonPercentages_1() {

		assertEquals("45", 32.658394f, ionPercentages.getPercentage(45));
	}

	public void testIonPercentages_2() {

		assertEquals("55", 3.2658393f, ionPercentages.getPercentage(55));
	}

	public void testIonPercentages_3() {

		assertEquals("65", 1.6329197f, ionPercentages.getPercentage(65));
	}

	public void testIonPercentages_4() {

		assertEquals("75", 4.9640756f, ionPercentages.getPercentage(75));
	}

	public void testIonPercentages_5() {

		assertEquals("85", 57.47877f, ionPercentages.getPercentage(85));
	}

	public void testIonPercentages_6() {

		List<Integer> ionList = new ArrayList<Integer>(ions.keySet());
		assertEquals("All", 100.0f, ionPercentages.getPercentage(ionList));
	}

	public void testIonPercentages_7() {

		assertEquals("95", 0.0f, ionPercentages.getPercentage(95));
	}
}
