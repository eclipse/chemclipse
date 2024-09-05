/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mmass.converter;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.model.core.IMassSpectrumPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.supplier.mmass.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IStandaloneMassSpectrum;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

public class ProteinIdentification_ITest extends TestCase {

	private IStandaloneMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_PROTEIN_IDENTIFICATION));
		MassSpectrumImportConverter converter = new MassSpectrumImportConverter();
		IProcessingInfo<IMassSpectra> processingInfo = converter.convert(importFile, new NullProgressMonitor());
		massSpectrum = (IStandaloneMassSpectrum)processingInfo.getProcessingResult().getMassSpectrum(1);
	}

	@Test
	public void testDescription() {

		assertEquals("Bruker BiFlex IV MALDI-TOF MS", massSpectrum.getInstrument());
		assertEquals("Martin Strohalm (+420) 29644 2631 Institute of Microbiology", massSpectrum.getOperator());
		assertEquals("This sample is Lysozyme C reduced by DTT, alkylated by IAA and digested by Trypsin. Try to use MASCOT or PROFOUND tools to identify this protein. Use predefined Lysozyme C sequence and Protein Digest tool to generate peptides and match them on this data. Generate Analysis Report as well.", massSpectrum.getDescription());
	}

	@Test
	public void testSpectrum() {

		assertEquals(71561, massSpectrum.getNumberOfIons());
		assertEquals(993.4036865234375d, massSpectrum.getBasePeak());
		assertEquals(3634.0f, massSpectrum.getBasePeakAbundance());
	}

	@Test
	public void testPeaks() {

		List<IMassSpectrumPeak> peaks = massSpectrum.getPeaks();
		assertEquals(38, peaks.size());
		IMassSpectrumPeak firstPeak = peaks.get(0);
		assertEquals(568.274371d, firstPeak.getIon());
		assertEquals(261.089388d, firstPeak.getAbundance());
		assertEquals(11.571d, firstPeak.getSignalToNoise());
	}

	@Test
	public void testAnnotations() {

		List<IMassSpectrumPeak> peaks = massSpectrum.getPeaks();
		IMassSpectrumPeak basePeak = peaks.get(8);
		IIdentificationTarget firstEntry = basePeak.getTargets().iterator().next();
		assertEquals("mMass annotation", firstEntry.getIdentifier());
		ILibraryInformation info = firstEntry.getLibraryInformation();
		assertEquals("My BasePeak", info.getName());
		assertEquals(993.010000d, info.getMolWeight());
	}
}
