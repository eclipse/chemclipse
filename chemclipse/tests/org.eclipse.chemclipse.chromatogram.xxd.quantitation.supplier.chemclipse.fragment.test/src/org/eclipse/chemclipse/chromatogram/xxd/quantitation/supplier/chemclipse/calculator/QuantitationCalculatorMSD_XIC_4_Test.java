/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.calculator;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.internal.calculator.IQuantitationCalculatorMSD;
import org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.internal.calculator.QuantitationCalculatorMSD;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignals;

public class QuantitationCalculatorMSD_XIC_4_Test extends QuantitationCalculator_XIC_TestCase {

	/*
	 * UseTIC: false (-> m/z 103, 104)
	 * CalibrationMethod: LINEAR
	 * isZeroCrossing: false
	 */
	private IQuantitationCalculatorMSD calculator;
	private List<IQuantitationEntry> quantitationEntries;
	private IQuantitationEntry quantitationEntry1;
	private IQuantitationEntry quantitationEntry2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
		IQuantitationCompound quantitationCompound = getQuantitationCompound();
		quantitationCompound.getQuantitationPeaks().addAll(getQuantitationPeaks());
		//
		quantitationCompound.setUseTIC(false);
		quantitationCompound.setCalibrationMethod(CalibrationMethod.LINEAR);
		quantitationCompound.calculateSignalTablesFromPeaks();
		//
		IQuantitationSignals quantitationSignals = quantitationCompound.getQuantitationSignals();
		quantitationSignals.deselectAllSignals();
		quantitationSignals.selectSignal(104.0d);
		quantitationSignals.selectSignal(103.0d);
		//
		calculator = new QuantitationCalculatorMSD();
		quantitationCompound.setUseCrossZero(false);
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

		assertEquals(0.020000000000000146d, quantitationEntry1.getConcentration());
	}

	public void testCalculateConcentration13() {

		assertEquals(103.0d, quantitationEntry1.getSignal());
	}

	/*
	 * Entry 2
	 */
	public void testCalculateConcentration21() {

		assertNotNull(quantitationEntry2);
	}

	public void testCalculateConcentration22() {

		assertEquals(0.019999999999999737d, quantitationEntry2.getConcentration());
	}

	public void testCalculateConcentration23() {

		assertEquals(104.0d, quantitationEntry2.getSignal());
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

		assertEquals("The integrated area '1.7E5' is < min response '5.0E5'.", quantitationEntry1.getDescription());
	}
}
