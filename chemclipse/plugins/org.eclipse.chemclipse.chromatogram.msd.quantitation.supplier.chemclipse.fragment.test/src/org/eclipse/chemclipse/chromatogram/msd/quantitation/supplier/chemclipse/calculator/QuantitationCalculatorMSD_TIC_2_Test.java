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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.calculator.IQuantitationCalculatorMSD;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.calculator.QuantitationCalculatorMSD;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;

public class QuantitationCalculatorMSD_TIC_2_Test extends QuantitationCalculator_TIC_TestCase {

	/*
	 * UseTIC: true
	 * CalibrationMethod: LINEAR
	 * isZeroCrossing: false
	 */
	private IQuantitationCalculatorMSD calculator;
	private List<IQuantitationEntry> quantitationEntries;
	private IQuantitationEntry quantitationEntry;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
		IQuantitationCompound quantitationCompound = getQuantitationCompound();
		quantitationCompound.getQuantitationPeaks().addAll(getQuantitationPeaks());
		//
		quantitationCompound.setUseTIC(true);
		quantitationCompound.setCalibrationMethod(CalibrationMethod.LINEAR);
		quantitationCompound.calculateSignalTablesFromPeaks();
		//
		calculator = new QuantitationCalculatorMSD();
		quantitationCompound.setUseCrossZero(false);
		quantitationEntries = calculator.calculateQuantitationResults(getReferencePeakMSD_TIC_X(), quantitationCompound);
		quantitationEntry = quantitationEntries.get(0);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		calculator = null;
		quantitationEntries = null;
		quantitationEntry = null;
	}

	public void testCalculateConcentration_1() {

		assertEquals(1, quantitationEntries.size());
	}

	public void testCalculateConcentration_2() {

		assertNotNull(quantitationEntry);
	}

	public void testCalculateConcentration_3() {

		assertEquals("Styrene", quantitationEntry.getName());
	}

	public void testCalculateConcentration_4() {

		assertEquals("Styrene-Butadiene", quantitationEntry.getChemicalClass());
	}

	public void testCalculateConcentration_5() {

		assertEquals(0.029999999999999992d, quantitationEntry.getConcentration());
	}

	public void testCalculateConcentration_6() {

		assertEquals("mg/ml", quantitationEntry.getConcentrationUnit());
	}

	public void testCalculateConcentration_7() {

		assertEquals("", quantitationEntry.getDescription());
	}

	public void testCalculateConcentration_8() {

		assertEquals(ISignal.TOTAL_INTENSITY, quantitationEntry.getSignal());
	}
}
