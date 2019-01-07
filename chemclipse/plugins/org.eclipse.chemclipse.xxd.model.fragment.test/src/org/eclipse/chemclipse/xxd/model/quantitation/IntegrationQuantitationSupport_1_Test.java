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

public class IntegrationQuantitationSupport_1_Test extends QuantitationCalculator_TIC_TestCase {

	private QuantitationSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = new QuantitationSupport(getReferencePeakMSD_TIC_1());
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		support = null;
	}

	public void testIsTheTotalSignalIntegrated_1() {

		assertTrue(support.isTotalSignalIntegrated());
	}

	public void testValidateTIC_1() {

		assertTrue(support.validateTIC());
	}

	public void testValidateXIC_1() {

		List<Double> selectedQuantitationIons = new ArrayList<Double>();
		selectedQuantitationIons.add(AbstractIon.TIC_ION);
		assertTrue(support.validateXIC(selectedQuantitationIons));
	}

	public void testGetIntegrationArea_1() {

		assertEquals(750220.0d, support.getIntegrationArea(AbstractIon.TIC_ION));
	}
}
