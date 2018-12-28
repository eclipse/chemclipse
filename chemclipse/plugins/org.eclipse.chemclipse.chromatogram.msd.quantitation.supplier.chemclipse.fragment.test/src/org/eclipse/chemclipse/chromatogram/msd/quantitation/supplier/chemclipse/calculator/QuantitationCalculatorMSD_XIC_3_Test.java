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

import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignals;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.calculator.IQuantitationCalculatorMSD;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.calculator.QuantitationCalculatorMSD;

public class QuantitationCalculatorMSD_XIC_3_Test extends QuantitationCalculator_XIC_TestCase {

	/*
	 * UseTIC: false (-> m/z 103, 104)
	 * CalibrationMethod: LINEAR
	 * isZeroCrossing: true
	 */
	private IQuantitationCalculatorMSD calculator;
	private List<IQuantitationEntryMSD> quantitationEntries;
	private IQuantitationEntryMSD quantitationEntry1;
	private IQuantitationEntryMSD quantitationEntry2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
		IQuantitationCompound quantitationCompound = getQuantitationCompound();
		List<IQuantitationPeak> quantitationPeaks = getQuantitationPeaks();
		//
		quantitationCompound.setUseTIC(false);
		quantitationCompound.setCalibrationMethod(CalibrationMethod.LINEAR);
		quantitationCompound.calculateQuantitationSignalsAndConcentrationResponseEntries(quantitationPeaks);
		//
		IQuantitationSignals quantitationSignals = quantitationCompound.getQuantitationSignals();
		quantitationSignals.deselectAllSignals();
		quantitationSignals.selectSignal(104.0d);
		quantitationSignals.selectSignal(103.0d);
		//
		calculator = new QuantitationCalculatorMSD();
		quantitationCompound.setUseCrossZero(true);
		quantitationEntries = calculator.calculateQuantitationResults(getReferencePeakMSD_XIC_X(), quantitationCompound);
		//
		quantitationEntry1 = quantitationEntries.get(0);
		quantitationEntry2 = quantitationEntries.get(1);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		calculator = null;
		quantitationEntries = null;
		quantitationEntry1 = null;
		quantitationEntry2 = null;
	}

	public void testCalculateConcentration_1() {

		assertEquals(2, quantitationEntries.size());
	}

	/*
	 * Entry 1
	 */
	public void testCalculateConcentration11() {

		assertNotNull(quantitationEntry1);
	}

	public void testCalculateConcentration12() {

		assertEquals(0.02d, quantitationEntry1.getConcentration());
	}

	public void testCalculateConcentration13() {

		assertEquals(103.0d, quantitationEntry1.getIon());
	}

	/*
	 * Entry 2
	 */
	public void testCalculateConcentration21() {

		assertNotNull(quantitationEntry2);
	}

	public void testCalculateConcentration22() {

		assertEquals(0.01999999999999998d, quantitationEntry2.getConcentration());
	}

	public void testCalculateConcentration23() {

		assertEquals(104.0d, quantitationEntry2.getIon());
	}

	/*
	 * Miscellaneous Entry 1
	 */
	public void testCalculateConcentration1_1() {

		assertEquals("Styrene", quantitationEntry1.getName());
	}

	public void testCalculateConcentration1_2() {

		assertEquals("Styrene-Butadiene", quantitationEntry1.getChemicalClass());
	}

	public void testCalculateConcentration1_3() {

		assertEquals("mg/ml", quantitationEntry1.getConcentrationUnit());
	}

	public void testCalculateConcentration1_4() {

		assertEquals("", quantitationEntry1.getDescription());
	}
}
