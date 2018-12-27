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
package org.eclipse.chemclipse.msd.model.core.quantitation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntries;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignals;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;

public class QuantitationCompoundMSD_3_Test extends ReferencePeakMSDTestCase {

	private IQuantitationCompoundMSD quantitationCompound;
	private IQuantitationSignals quantitationSignals;
	private IConcentrationResponseEntries concentrationResponseEntries;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		quantitationCompound = new QuantitationCompoundMSD("Styrene", "mg/ml", 5500);
		//
		List<IQuantitationPeakMSD> quantitationPeaks = new ArrayList<IQuantitationPeakMSD>();
		IQuantitationPeakMSD quantitationPeak = new QuantitationPeakMSD(getReferencePeakMSD_TIC_1(), 0.1d, "mg/ml");
		quantitationPeaks.add(quantitationPeak);
		//
		quantitationCompound.setUseTIC(true);
		quantitationCompound.calculateQuantitationSignalsAndConcentrationResponseEntries(quantitationPeaks);
		//
		quantitationSignals = quantitationCompound.getQuantitationSignalsMSD();
		concentrationResponseEntries = quantitationCompound.getConcentrationResponseEntriesMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		quantitationCompound = null;
	}

	public void testGetQuantitationSignals_1() {

		assertNotNull(quantitationSignals);
	}

	public void testGetQuantitationSignals_2() {

		assertEquals(1, quantitationSignals.size());
	}

	public void testGetQuantitationSignals_3() {

		IQuantitationSignal quantitationSignal = quantitationSignals.get(0);
		assertEquals(AbstractIon.TIC_ION, quantitationSignal.getSignal());
		assertEquals(IQuantitationSignal.ABSOLUTE_RESPONSE, quantitationSignal.getRelativeResponse());
		assertEquals(0.0d, quantitationSignal.getUncertainty());
		assertTrue(quantitationSignal.isUse());
	}

	public void testGetConcentrationResponseEntries_1() {

		assertNotNull(concentrationResponseEntries);
	}

	public void testGetConcentrationResponseEntries_2() {

		assertEquals(1, concentrationResponseEntries.size());
	}

	public void testGetConcentrationResponseEntries_3() {

		IConcentrationResponseEntry concentrationResponseEntry = concentrationResponseEntries.get(0);
		assertEquals(AbstractIon.TIC_ION, concentrationResponseEntry.getSignal());
		assertEquals(0.1d, concentrationResponseEntry.getConcentration());
		assertEquals(750220.0d, concentrationResponseEntry.getResponse());
	}
}
