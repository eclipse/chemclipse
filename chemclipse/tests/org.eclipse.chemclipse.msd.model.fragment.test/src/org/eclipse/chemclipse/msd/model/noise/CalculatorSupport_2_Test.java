/*******************************************************************************
 * Copyright (c) 2010, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.noise;

import org.eclipse.chemclipse.model.support.AnalysisSegment;
import org.eclipse.chemclipse.msd.model.support.CombinedMassSpectrumCalculator;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignals;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

import junit.framework.TestCase;

public class CalculatorSupport_2_Test extends TestCase {

	private CalculatorSupport calculatorSupport;
	private AnalysisSegment analysisSegment;
	private IExtractedIonSignals extractedIonSignals;
	private IExtractedIonSignal extractedIonSignal;
	private CombinedMassSpectrumCalculator combinedMassSpectrumCalculator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		calculatorSupport = new CalculatorSupport();
		analysisSegment = new AnalysisSegment(1, 3) {

			@Override
			public int getStartRetentionTime() {

				return 0;
			}

			@Override
			public int getStopRetentionTime() {

				return 0;
			}
		};
		extractedIonSignals = new ExtractedIonSignals(1, 3);
		/*
		 * Scan #1
		 */
		extractedIonSignal = new ExtractedIonSignal(43, 104);
		extractedIonSignal.setAbundance(104, 500.0f);
		extractedIonSignal.setAbundance(103, 2500.0f);
		extractedIonSignal.setAbundance(43, 120.0f);
		extractedIonSignals.add(extractedIonSignal);
		/*
		 * Scan #2
		 */
		extractedIonSignal = new ExtractedIonSignal(28, 160);
		extractedIonSignal.setAbundance(155, 18000.0f);
		extractedIonSignal.setAbundance(103, 2500.0f);
		extractedIonSignal.setAbundance(28, 320.0f);
		extractedIonSignals.add(extractedIonSignal);
		/*
		 * Scan #3
		 */
		extractedIonSignal = new ExtractedIonSignal(18, 104);
		extractedIonSignal.setAbundance(18, 200.0f);
		extractedIonSignal.setAbundance(43, 280.0f);
		extractedIonSignal.setAbundance(104, 20000.0f);
		extractedIonSignals.add(extractedIonSignal);
		combinedMassSpectrumCalculator = calculatorSupport.getCombinedMassSpectrumCalculator(analysisSegment, extractedIonSignals);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetCombinedMassSpectrumCalculator_1() {

		assertEquals("Size", 6, combinedMassSpectrumCalculator.getValues().size());
	}

	public void testGetCombinedMassSpectrumCalculator_2() {

		int ion = 18;
		double abundance = 200.0;
		assertEquals("Abundance", abundance, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testGetCombinedMassSpectrumCalculator_3() {

		int ion = 28;
		double abundance = 320.0;
		assertEquals("Abundance", abundance, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testGetCombinedMassSpectrumCalculator_4() {

		int ion = 43;
		double abundance = 400.0;
		assertEquals("Abundance", abundance, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testGetCombinedMassSpectrumCalculator_5() {

		int ion = 103;
		double abundance = 5000.0;
		assertEquals("Abundance", abundance, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testGetCombinedMassSpectrumCalculator_6() {

		int ion = 104;
		double abundance = 20500.0;
		assertEquals("Abundance", abundance, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testGetCombinedMassSpectrumCalculator_7() {

		int ion = 155;
		double abundance = 18000.0;
		assertEquals("Abundance", abundance, combinedMassSpectrumCalculator.getAbundance(ion));
	}

	public void testNormalizeMassSpectrumCalculator_1() {

		combinedMassSpectrumCalculator.normalize(1000.0f);
		assertEquals("Size", 6, combinedMassSpectrumCalculator.getValues().size());
	}

	public void testNormalizeMassSpectrumCalculator_2() {

		combinedMassSpectrumCalculator.normalize(1000.0f);
		assertEquals("N18", 9.75609756097561, combinedMassSpectrumCalculator.getAbundance(18));
	}

	public void testNormalizeMassSpectrumCalculator_3() {

		combinedMassSpectrumCalculator.normalize(1000.0f);
		assertEquals("N28", 15.609756097560975, combinedMassSpectrumCalculator.getAbundance(28));
	}

	public void testNormalizeMassSpectrumCalculator_4() {

		combinedMassSpectrumCalculator.normalize(1000.0f);
		assertEquals("N43", 19.51219512195122, combinedMassSpectrumCalculator.getAbundance(43));
	}

	public void testNormalizeMassSpectrumCalculator_5() {

		combinedMassSpectrumCalculator.normalize(1000.0f);
		assertEquals("N103", 243.90243902439025, combinedMassSpectrumCalculator.getAbundance(103));
	}

	public void testNormalizeMassSpectrumCalculator_6() {

		combinedMassSpectrumCalculator.normalize(1000.0f);
		assertEquals("N104", 1000.0, combinedMassSpectrumCalculator.getAbundance(104));
	}

	public void testNormalizeMassSpectrumCalculator_7() {

		combinedMassSpectrumCalculator.normalize(1000.0f);
		assertEquals("N155", 878.048780487805, combinedMassSpectrumCalculator.getAbundance(155));
	}
}
