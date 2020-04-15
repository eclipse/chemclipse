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

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.support.AnalysisSegment;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.support.CalculationType;
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

		assertEquals("Size", 6, combinedMassSpectrumCalculator.size());
	}

	public void testGetCombinedMassSpectrumCalculator_2() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 18;
		float abundance = 200.0f;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals("Abundance", abundance, massSpectrum.getIon(ion).getAbundance());
	}

	public void testGetCombinedMassSpectrumCalculator_3() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 28;
		float abundance = 320.0f;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals("Abundance", abundance, massSpectrum.getIon(ion).getAbundance());
	}

	public void testGetCombinedMassSpectrumCalculator_4() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 43;
		float abundance = 400.0f;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals("Abundance", abundance, massSpectrum.getIon(ion).getAbundance());
	}

	public void testGetCombinedMassSpectrumCalculator_5() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 103;
		float abundance = 5000.0f;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals("Abundance", abundance, massSpectrum.getIon(ion).getAbundance());
	}

	public void testGetCombinedMassSpectrumCalculator_6() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 104;
		float abundance = 20500.0f;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals("Abundance", abundance, massSpectrum.getIon(ion).getAbundance());
	}

	public void testGetCombinedMassSpectrumCalculator_7() throws AbundanceLimitExceededException, IonLimitExceededException {

		int ion = 155;
		float abundance = 18000.0f;
		ICombinedMassSpectrum massSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		assertEquals("Abundance", abundance, massSpectrum.getIon(ion).getAbundance());
	}

	public void testNormalizeMassSpectrumCalculator_1() {

		ICombinedMassSpectrum noiseMassSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		noiseMassSpectrum.normalize(1000.0f);
		assertEquals("Size", 6, noiseMassSpectrum.getNumberOfIons());
	}

	public void testNormalizeMassSpectrumCalculator_2() throws AbundanceLimitExceededException, IonLimitExceededException {

		ICombinedMassSpectrum noiseMassSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		noiseMassSpectrum.normalize(1000.0f);
		assertEquals("N18", 9.756098f, noiseMassSpectrum.getIon(18).getAbundance());
	}

	public void testNormalizeMassSpectrumCalculator_3() throws AbundanceLimitExceededException, IonLimitExceededException {

		ICombinedMassSpectrum noiseMassSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		noiseMassSpectrum.normalize(1000.0f);
		assertEquals("N28", 15.609756f, noiseMassSpectrum.getIon(28).getAbundance());
	}

	public void testNormalizeMassSpectrumCalculator_4() throws AbundanceLimitExceededException, IonLimitExceededException {

		ICombinedMassSpectrum noiseMassSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		noiseMassSpectrum.normalize(1000.0f);
		assertEquals("N43", 19.512196f, noiseMassSpectrum.getIon(43).getAbundance());
	}

	public void testNormalizeMassSpectrumCalculator_5() throws AbundanceLimitExceededException, IonLimitExceededException {

		ICombinedMassSpectrum noiseMassSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		noiseMassSpectrum.normalize(1000.0f);
		assertEquals("N103", 243.90244f, noiseMassSpectrum.getIon(103).getAbundance());
	}

	public void testNormalizeMassSpectrumCalculator_6() throws AbundanceLimitExceededException, IonLimitExceededException {

		ICombinedMassSpectrum noiseMassSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		noiseMassSpectrum.normalize(1000.0f);
		assertEquals("N104", 1000.0f, noiseMassSpectrum.getIon(104).getAbundance());
	}

	public void testNormalizeMassSpectrumCalculator_7() throws AbundanceLimitExceededException, IonLimitExceededException {

		ICombinedMassSpectrum noiseMassSpectrum = combinedMassSpectrumCalculator.createMassSpectrum(CalculationType.SUM);
		noiseMassSpectrum.normalize(1000.0f);
		assertEquals("N155", 878.04877f, noiseMassSpectrum.getIon(155).getAbundance());
	}
}
