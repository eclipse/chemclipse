/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.internal.core.support;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIon;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.WncIon;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.settings.ClassifierSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public class Calculator_2_ITest extends ChromatogramTestCase {

	private Calculator calculator;
	private IWncIons wncIons;
	private IWncIon wncIon;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		calculator = new Calculator();
		ClassifierSettings classifierSettings = new ClassifierSettings();
		wncIons = classifierSettings.getWNCIons();
		wncIons.add(new WncIon(18, "water"));
		wncIons.add(new WncIon(28, "nitrogen"));
		wncIons.add(new WncIon(32, "carbon dioxide"));
		wncIons.add(new WncIon(84, "solvent tailing"));
		wncIons.add(new WncIon(207, "column bleed"));
		IChromatogramSelectionMSD chromatogramSelection = getChromatogramSelection();
		wncIons = calculator.calculateIonPercentages(chromatogramSelection, classifierSettings);
	}

	@Override
	protected void tearDown() throws Exception {

		calculator = null;
		wncIons = null;
		//
		System.gc();
		//
		super.tearDown();
	}

	public void testCalculatedWncIons_1() {

		wncIon = wncIons.getWNCIon(18);
		assertEquals(18, wncIon.getIon());
		assertEquals("water", wncIon.getName());
		assertEquals(21.002375405306804d, wncIon.getPercentageMaxIntensity());
		assertEquals(1.977177063160481d, wncIon.getPercentageSumIntensity());
	}

	public void testCalculatedWncIons_2() {

		wncIon = wncIons.getWNCIon(28);
		assertEquals(28, wncIon.getIon());
		assertEquals("nitrogen", wncIon.getName());
		assertEquals(99.99999999999999d, wncIon.getPercentageMaxIntensity());
		assertEquals(9.414064004688226d, wncIon.getPercentageSumIntensity());
	}

	public void testCalculatedWncIons_3() {

		wncIon = wncIons.getWNCIon(32);
		assertEquals(32, wncIon.getIon());
		assertEquals("carbon dioxide", wncIon.getName());
		assertEquals(24.744202936490822d, wncIon.getPercentageMaxIntensity());
		assertEquals(2.3294351018911894d, wncIon.getPercentageSumIntensity());
	}

	public void testCalculatedWncIons_4() {

		wncIon = wncIons.getWNCIon(84);
		assertEquals(84, wncIon.getIon());
		assertEquals("solvent tailing", wncIon.getName());
		assertEquals(4.285515151830384d, wncIon.getPercentageMaxIntensity());
		assertEquals(0.40344113932392417d, wncIon.getPercentageSumIntensity());
	}

	public void testCalculatedWncIons_5() {

		wncIon = wncIons.getWNCIon(207);
		assertEquals(207, wncIon.getIon());
		assertEquals("column bleed", wncIon.getName());
		assertEquals(6.292490758325699d, wncIon.getPercentageMaxIntensity());
		assertEquals(0.5923791074778729d, wncIon.getPercentageSumIntensity());
	}
}
