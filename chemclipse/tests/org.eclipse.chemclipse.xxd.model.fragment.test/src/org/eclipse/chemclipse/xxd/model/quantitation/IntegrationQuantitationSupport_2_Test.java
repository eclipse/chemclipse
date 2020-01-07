/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.quantitation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.quantitation.QuantitationSupport;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;

public class IntegrationQuantitationSupport_2_Test extends QuantitationCalculator_TIC_TestCase {

	private QuantitationSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = new QuantitationSupport(getReferencePeakMSD_XIC_1());
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		support = null;
	}

	public void testIsTheTotalSignalIntegrated_1() {

		assertFalse(support.isTotalSignalIntegrated());
	}

	public void testValidateTIC_1() {

		assertFalse(support.validateTIC());
	}

	public void testValidateXIC_1() {

		List<Double> selectedQuantitationIons = new ArrayList<Double>();
		selectedQuantitationIons.add(AbstractIon.TIC_ION);
		assertFalse(support.validateXIC(selectedQuantitationIons));
	}

	public void testValidateXIC_2() {

		List<Double> selectedQuantitationIons = new ArrayList<Double>();
		selectedQuantitationIons.add(104.0d);
		selectedQuantitationIons.add(103.0d);
		assertTrue(support.validateXIC(selectedQuantitationIons));
	}

	public void testValidateXIC_3() {

		List<Double> selectedQuantitationIons = new ArrayList<Double>();
		selectedQuantitationIons.add(104.0d);
		selectedQuantitationIons.add(103.0d);
		selectedQuantitationIons.add(51.0d);
		selectedQuantitationIons.add(50.0d);
		selectedQuantitationIons.add(78.0d);
		selectedQuantitationIons.add(77.0d);
		selectedQuantitationIons.add(74.0d);
		selectedQuantitationIons.add(105.0d);
		assertTrue(support.validateXIC(selectedQuantitationIons));
	}

	public void testValidateXIC_4() {

		List<Double> selectedQuantitationIons = new ArrayList<Double>();
		selectedQuantitationIons.add(180.0d); // does not exist
		assertFalse(support.validateXIC(selectedQuantitationIons));
	}

	public void testValidateXIC_5() {

		List<Double> selectedQuantitationIons = new ArrayList<Double>();
		selectedQuantitationIons.add(104.0d);
		selectedQuantitationIons.add(103.0d);
		selectedQuantitationIons.add(51.0d);
		selectedQuantitationIons.add(50.0d);
		selectedQuantitationIons.add(78.0d);
		selectedQuantitationIons.add(77.0d);
		selectedQuantitationIons.add(74.0d);
		selectedQuantitationIons.add(105.0d);
		selectedQuantitationIons.add(180.0d); // does not exist
		assertFalse(support.validateXIC(selectedQuantitationIons));
	}

	public void testGetIntegrationArea_1() {

		assertEquals(2638892.754731409d, support.getIntegrationArea(104.0d));
	}

	public void testGetIntegrationArea_2() {

		assertEquals(665459.9120627032d, support.getIntegrationArea(103.0d));
	}

	public void testGetIntegrationArea_3() {

		assertEquals(270773.34352896194d, support.getIntegrationArea(78.0d));
	}

	public void testGetIntegrationArea_4() {

		assertEquals(0.0d, support.getIntegrationArea(180.0d));
	}
}
