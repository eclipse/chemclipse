/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.internal.core.support;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.exceptions.ClassifierException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.internal.core.support.Calculator;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.WncIons;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public class Calculator_1_ITest extends ChromatogramTestCase {

	private Calculator calculator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		calculator = new Calculator();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCalculator_1() {

		IWncIons wncIons = new WncIons();
		IChromatogramSelectionMSD chromatogramSelection = getChromatogramSelection();
		try {
			wncIons = calculator.calculateIonPercentages(chromatogramSelection, wncIons);
		} catch(ClassifierException e) {
			assertTrue("ClassifierException shouldn't be thrown here.", false);
		}
	}
}
