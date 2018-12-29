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
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignals;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;

public class QuantitationCompoundMSD_4_Test extends ReferencePeakMSDTestCase {

	private IQuantitationCompound quantitationCompound;
	private IQuantitationSignals quantitationSignals;
	private IConcentrationResponseEntries concentrationResponseEntries;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		quantitationCompound = new QuantitationCompoundMSD("Styrene", "mg/ml", 5500);
		//
		List<IQuantitationPeak> quantitationPeaks = new ArrayList<IQuantitationPeak>();
		IQuantitationPeak quantitationPeak1 = new QuantitationPeakMSD(getReferencePeakMSD_TIC_1(), 0.01d, "mg/ml");
		quantitationPeaks.add(quantitationPeak1);
		IQuantitationPeak quantitationPeak2 = new QuantitationPeakMSD(getReferencePeakMSD_TIC_2(), 0.05d, "mg/ml");
		quantitationPeaks.add(quantitationPeak2);
		IQuantitationPeak quantitationPeak3 = new QuantitationPeakMSD(getReferencePeakMSD_TIC_3(), 0.1d, "mg/ml");
		quantitationPeaks.add(quantitationPeak3);
		//
		quantitationCompound.getQuantitationPeaks().addAll(quantitationPeaks);
		quantitationCompound.setUseTIC(true);
		quantitationCompound.calculateQuantitationSignalsAndConcentrationResponseEntries();
		//
		quantitationSignals = quantitationCompound.getQuantitationSignals();
		concentrationResponseEntries = quantitationCompound.getConcentrationResponseEntries();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		quantitationCompound = null;
		quantitationSignals = null;
		concentrationResponseEntries = null;
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

		assertEquals(3, concentrationResponseEntries.size());
	}

	public void testGetConcentrationResponseEntries_3() {

		IConcentrationResponseEntry concentrationResponseEntry = concentrationResponseEntries.get(0);
		assertEquals(AbstractIon.TIC_ION, concentrationResponseEntry.getSignal());
		assertEquals(0.01d, concentrationResponseEntry.getConcentration());
		assertEquals(750220.0d, concentrationResponseEntry.getResponse());
	}

	public void testGetConcentrationResponseEntries_4() {

		IConcentrationResponseEntry concentrationResponseEntry = concentrationResponseEntries.get(1);
		assertEquals(AbstractIon.TIC_ION, concentrationResponseEntry.getSignal());
		assertEquals(0.05d, concentrationResponseEntry.getConcentration());
		assertEquals(3751100.0d, concentrationResponseEntry.getResponse());
	}

	public void testGetConcentrationResponseEntries_5() {

		IConcentrationResponseEntry concentrationResponseEntry = concentrationResponseEntries.get(2);
		assertEquals(AbstractIon.TIC_ION, concentrationResponseEntry.getSignal());
		assertEquals(0.1d, concentrationResponseEntry.getConcentration());
		assertEquals(7502200.0d, concentrationResponseEntry.getResponse());
	}
}
