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

public class QuantitationCalculatorMSD_XIC_2_Test extends QuantitationCalculator_XIC_TestCase {

	/*
	 * UseTIC: false
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
		quantitationCompound.setUseTIC(false);
		quantitationCompound.setCalibrationMethod(CalibrationMethod.LINEAR);
		quantitationCompound.calculateSignalTablesFromPeaks();
		//
		calculator = new QuantitationCalculatorMSD();
		quantitationCompound.setUseCrossZero(false);
		quantitationEntries = calculator.calculateQuantitationResults(getReferencePeakMSD_XIC_X(), quantitationCompound);
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

		assertEquals(8, quantitationEntries.size());
	}

	public void testCalculateConcentration0_1() {

		quantitationEntry = quantitationEntries.get(0);
		assertEquals(0.020000000000000424d, quantitationEntry.getConcentration());
	}

	public void testCalculateConcentration0_2() {

		quantitationEntry = quantitationEntries.get(0);
		assertEquals(50.0d, quantitationEntry.getSignal());
	}

	public void testCalculateConcentration1_1() {

		quantitationEntry = quantitationEntries.get(6);
		assertEquals(0.019999999999999737d, quantitationEntry.getConcentration());
	}

	public void testCalculateConcentration1_2() {

		quantitationEntry = quantitationEntries.get(6);
		assertEquals(104.0d, quantitationEntry.getSignal());
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

	public void testCalculateConcentration_6() {

		assertEquals("mg/ml", quantitationEntry.getConcentrationUnit());
	}

	public void testCalculateConcentration_7() {

		assertEquals("The integrated area '1.4E5' is < min response '4.1E5'.", quantitationEntry.getDescription());
	}
}
