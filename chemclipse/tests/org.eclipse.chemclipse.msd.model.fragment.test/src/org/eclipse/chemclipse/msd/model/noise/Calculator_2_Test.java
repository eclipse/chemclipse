/*******************************************************************************
 * Copyright (c) 2010, 2019 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.implementation.CombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class Calculator_2_Test extends TestCase {

	private Calculator calculator;
	private List<ICombinedMassSpectrum> noiseMassSpectra;
	private ICombinedMassSpectrum noiseMassSpectrum;
	private IMarkedIons ionsToPreserve;
	private IExtractedIonSignal extractedIonSignal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		calculator = new Calculator();
		noiseMassSpectra = new ArrayList<ICombinedMassSpectrum>();
		/*
		 * Noise mass spectrum #1
		 */
		noiseMassSpectrum = new CombinedMassSpectrum();
		noiseMassSpectrum.addIon(new Ion(28.0f, 500.0f));
		noiseMassSpectrum.addIon(new Ion(48.0f, 750.0f));
		noiseMassSpectrum.addIon(new Ion(55.0f, 250.0f));
		noiseMassSpectrum.addIon(new Ion(103.0f, 1000.0f));
		noiseMassSpectrum.addIon(new Ion(178.0f, 200.0f));
		noiseMassSpectra.add(noiseMassSpectrum);
		/*
		 * Noise mass spectrum #2
		 */
		noiseMassSpectrum = new CombinedMassSpectrum();
		noiseMassSpectrum.addIon(new Ion(18.0f, 700.0f));
		noiseMassSpectrum.addIon(new Ion(55.0f, 250.0f));
		noiseMassSpectrum.addIon(new Ion(89.0f, 250.0f));
		noiseMassSpectrum.addIon(new Ion(104.0f, 15000.0f));
		noiseMassSpectrum.addIon(new Ion(155.0f, 20000.0f));
		noiseMassSpectra.add(noiseMassSpectrum);
		/*
		 * Noise mass spectrum #3
		 */
		noiseMassSpectrum = new CombinedMassSpectrum();
		noiseMassSpectrum.addIon(new Ion(43.0f, 800.0f));
		noiseMassSpectrum.addIon(new Ion(48.0f, 760.0f));
		noiseMassSpectrum.addIon(new Ion(52.0f, 1250.0f));
		noiseMassSpectrum.addIon(new Ion(104.0f, 15000.0f));
		noiseMassSpectrum.addIon(new Ion(201.0f, 8900.0f));
		noiseMassSpectra.add(noiseMassSpectrum);
		ionsToPreserve = new MarkedIons(MarkedTraceModus.INCLUDE);
		ionsToPreserve.add(new MarkedIon(103));
		ionsToPreserve.add(new MarkedIon(104));
		ionsToPreserve.add(new MarkedIon(201));
		noiseMassSpectrum = calculator.getNoiseMassSpectrum(noiseMassSpectra, ionsToPreserve, new NullProgressMonitor());
		extractedIonSignal = noiseMassSpectrum.getExtractedIonSignal();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetNoiseMassSpectrum_1() {

		assertNotNull(noiseMassSpectrum);
	}

	public void testGetNoiseMassSpectrum_2() {

		assertEquals("Size", 9, noiseMassSpectrum.getNumberOfIons());
	}

	public void testGetNoiseMassSpectrum_3() {

		assertEquals("Start Ion", 18, extractedIonSignal.getStartIon());
	}

	public void testGetNoiseMassSpectrum_4() {

		assertEquals("Stop Ion", 178, extractedIonSignal.getStopIon());
	}

	public void testGetNoiseMassSpectrumAbundance_1() {

		int ion = 18;
		assertEquals("Ion " + ion, 35.0f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_2() {

		int ion = 28;
		assertEquals("Ion " + ion, 25.0f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_3() {

		int ion = 43;
		assertEquals("Ion " + ion, 40.0f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_4() {

		int ion = 48;
		assertEquals("Ion " + ion, 75.5f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_5() {

		int ion = 52;
		assertEquals("Ion " + ion, 62.5f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_6() {

		int ion = 55;
		assertEquals("Ion " + ion, 25.0f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_7() {

		int ion = 89;
		assertEquals("Ion " + ion, 12.5f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_8() {

		int ion = 103;
		assertEquals("Ion " + ion, 0.0f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_9() {

		int ion = 104;
		assertEquals("Ion " + ion, 0.0f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_10() {

		int ion = 155;
		assertEquals("Ion " + ion, 1000.0f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_11() {

		int ion = 178;
		assertEquals("Ion " + ion, 10.0f, extractedIonSignal.getAbundance(ion));
	}

	public void testGetNoiseMassSpectrumAbundance_12() {

		int ion = 201;
		assertEquals("Ion " + ion, 0.0f, extractedIonSignal.getAbundance(ion));
	}
}
