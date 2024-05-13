/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.implementation.QuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.InternalStandard;

public class PeakQuantifierSupport_3_Test extends PeakQuantifierSupportTestCase {

	private IPeak peak = null;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * ISTD
		 */
		peak = getPeak();
		//
		InternalStandard internalStandard1 = new InternalStandard("Styrene", 25.5d, "mg/g", 1.25d);
		peak.addInternalStandard(internalStandard1);
		InternalStandard internalStandard2 = new InternalStandard("Toluene", 12.75d, "mg/kg", 10.0d);
		peak.addInternalStandard(internalStandard2);
		//
		IQuantitationEntry quantitationEntry1 = new QuantitationEntry("Benzene", 12.45d, "ppm", 30000);
		peak.addQuantitationEntry(quantitationEntry1);
		IQuantitationEntry quantitationEntry2 = new QuantitationEntry("Naphthalene", 7.76d, "ppm", 40000);
		peak.addQuantitationEntry(quantitationEntry2);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("25.5 mg/g x1.25 [*2]", PeakQuantifierSupport.getInternalStandardConcentrations(peak));
	}

	public void test2() {

		assertEquals("12.45 ppm [*2]", PeakQuantifierSupport.getPeakConcentrations(peak));
	}

	public void test3() {

		IInternalStandard internalStandard = PeakQuantifierSupport.getInternalStandard("Toluene", "12.75 mg/kg");
		assertEquals("Toluene", internalStandard.getName());
		assertEquals(12.75d, internalStandard.getConcentration());
		assertEquals("mg/kg", internalStandard.getConcentrationUnit());
		assertEquals(1.0d, internalStandard.getCompensationFactor());
	}
}