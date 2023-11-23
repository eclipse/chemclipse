/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.massbank.io;

import java.io.File;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.supplier.massbank.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.massbank.model.Chromatography;
import org.eclipse.chemclipse.msd.converter.supplier.massbank.model.VendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.Polarity;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

public class MassBankReader_1_ITest extends TestCase {

	private MassBankReader reader;
	private File file;
	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		reader = new MassBankReader();
		file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CE000001));
		massSpectra = reader.read(file, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	@Test
	public void testMassSpectra() {

		assertEquals(1, massSpectra.size());
	}

	@Test
	public void testMassSpectrum() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		assertEquals(12, massSpectrum.getNumberOfIons());
		assertEquals(689.931152d, massSpectrum.getHighestIon().getIon());
		assertEquals(1071.2466f, massSpectrum.getLowestAbundance().getAbundance()); // TODO: lost some precision here
		if(massSpectrum instanceof VendorLibraryMassSpectrum vendorLibraryMassSpectrum) {
			ILibraryInformation libraryInformation = vendorLibraryMassSpectrum.getLibraryInformation();
			assertEquals("C37H67NO13", libraryInformation.getFormula());
			assertEquals("Natural Product; Antibiotic", libraryInformation.getCompoundClass());
			assertEquals("CC[C@@H]1[C@@]([C@@H]([C@H](C(=O)[C@@H](C[C@@]([C@@H]([C@H]([C@@H]([C@H](C(=O)O1)C)O[C@H]2C[C@@]([C@H]([C@@H](O2)C)O)(C)OC)C)O[C@H]3[C@@H]([C@H](C[C@H](O3)C)N(C)C)O)(C)O)C)C)O)(C)O", libraryInformation.getSmiles());
			assertEquals("InChI=1S/C37H67NO13/c1-14-25-37(10,45)30(41)20(4)27(39)18(2)16-35(8,44)32(51-34-28(40)24(38(11)12)15-19(3)47-34)21(5)29(22(6)33(43)49-25)50-26-17-36(9,46-13)31(42)23(7)48-26/h18-26,28-32,34,40-42,44-45H,14-17H2,1-13H3/t18-,19-,20+,21+,22-,23+,24+,25-,26+,28-,29+,30-,31+,32-,34+,35-,36-,37-/m1/s1", libraryInformation.getInChI());
			assertEquals("LTQ Orbitrap XL, Thermo Scientfic; HP-1100 HPLC, Agilent", vendorLibraryMassSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_INSTRUMENT_NAME));
			assertEquals("LC-ESI-ITFT", vendorLibraryMassSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_INSTRUMENT_TYPE));
			assertEquals((short)2, vendorLibraryMassSpectrum.getMassSpectrometer());
			assertEquals(Polarity.POSITIVE, vendorLibraryMassSpectrum.getPolarity());
			assertEquals("CID", vendorLibraryMassSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_FRAGMENTATION_METHOD));
			assertEquals("ESI", vendorLibraryMassSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_IONIZATION_MODE));
			Chromatography chromatography = vendorLibraryMassSpectrum.getChromatography();
			assertEquals("Symmetry C18 Column, Waters", chromatography.getColumnName());
			assertEquals("0min:5%, 24min:95%, 28min:95%, 28.1:5% (acetonitrile)", chromatography.getFlowGradient());
			assertEquals("0.3 ml/min", chromatography.getFlowRate());
			assertEquals(673366, vendorLibraryMassSpectrum.getRetentionTime());
			assertEquals("CH3CN(0.1%HCOOH)/ H2O(0.1%HCOOH)", chromatography.getSolvent());
			assertEquals(734.46852d, vendorLibraryMassSpectrum.getPrecursorIon());
			assertEquals("[M+H]+", vendorLibraryMassSpectrum.getPrecursorType());
		}
	}
}
