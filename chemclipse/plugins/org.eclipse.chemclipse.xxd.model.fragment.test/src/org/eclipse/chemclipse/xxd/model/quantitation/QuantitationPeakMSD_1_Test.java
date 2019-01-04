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

import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;

public class QuantitationPeakMSD_1_Test extends ReferencePeakMSDTestCase {

	private IQuantitationPeak quantitationPeakMSD;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		quantitationPeakMSD = new QuantitationPeakMSD(getReferencePeakMSD_TIC_1(), 0.67d, "mg/ml");
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		quantitationPeakMSD = null;
	}

	public void testGetConcentration_1() {

		assertEquals(0.67d, quantitationPeakMSD.getConcentration());
	}

	public void testGetConcentrationUnit_1() {

		assertEquals("mg/ml", quantitationPeakMSD.getConcentrationUnit());
	}

	public void testGetReferencePeakMSD_1() {

		assertNotNull(quantitationPeakMSD.getReferencePeak());
	}
}
