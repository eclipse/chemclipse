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

public class IonPercentages_3_Test extends TestCase {

	private IScanMSD massSpectrum;
	private IIon defaultIon;
	private IIonPercentages ionPercentages;
	private Map<Integer, Float> ions;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ions = new HashMap<Integer, Float>();
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

		assertEquals("TotalSignal", 0.0f, massSpectrum.getTotalSignal());
	}

	public void testIonPercentages_1() {

		assertEquals("45", 0.0f, ionPercentages.getPercentage(45));
	}

	public void testIonPercentages_2() {

		List<Integer> ionList = new ArrayList<Integer>(ions.keySet());
		assertEquals("All", 0.0f, ionPercentages.getPercentage(ionList));
	}
}
