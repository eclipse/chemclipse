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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.calculator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;

public class QuantitationCalculator_TIC_TestCase extends ReferencePeakMSDTestCase {

	private IQuantitationCompound quantitationCompound;
	private List<IQuantitationPeak> quantitationPeaks;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		quantitationCompound = new QuantitationCompoundMSD("Styrene", "mg/ml", 5500);
		quantitationCompound.setChemicalClass("Styrene-Butadiene");
		//
		quantitationPeaks = new ArrayList<IQuantitationPeak>();
		IQuantitationPeak quantitationPeak1 = new QuantitationPeakMSD(getReferencePeakMSD_TIC_1(), 0.01d, "mg/ml");
		quantitationPeaks.add(quantitationPeak1);
		IQuantitationPeak quantitationPeak2 = new QuantitationPeakMSD(getReferencePeakMSD_TIC_2(), 0.05d, "mg/ml");
		quantitationPeaks.add(quantitationPeak2);
		IQuantitationPeak quantitationPeak3 = new QuantitationPeakMSD(getReferencePeakMSD_TIC_3(), 0.1d, "mg/ml");
		quantitationPeaks.add(quantitationPeak3);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		quantitationCompound = null;
		quantitationPeaks = null;
	}

	public IQuantitationCompound getQuantitationCompound() {

		return quantitationCompound;
	}

	public List<IQuantitationPeak> getQuantitationPeaks() {

		return quantitationPeaks;
	}
}
