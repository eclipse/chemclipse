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

import org.eclipse.chemclipse.msd.model.core.IScanMSD;

import junit.framework.TestCase;

public class IonPercentages_4_Test extends TestCase {

	private IScanMSD massSpectrum;
	private IIonPercentages ionPercentages;
	private Map<Integer, Float> ions;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ions = new HashMap<Integer, Float>();
		massSpectrum = null;
		ionPercentages = new IonPercentages(massSpectrum);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testMassSpectrum_1() {

		assertNull("MassSpectrum", massSpectrum);
	}

	public void testIonPercentages_1() {

		assertEquals("45", 0.0f, ionPercentages.getPercentage(45));
	}

	public void testIonPercentages_2() {

		List<Integer> ionList = new ArrayList<Integer>(ions.keySet());
		assertEquals("All", 0.0f, ionPercentages.getPercentage(ionList));
	}
}
