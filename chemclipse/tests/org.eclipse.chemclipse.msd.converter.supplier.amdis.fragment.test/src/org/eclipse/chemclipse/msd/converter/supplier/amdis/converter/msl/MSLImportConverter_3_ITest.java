/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnPackaging;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.identifier.IColumnIndexMarker;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.ImportConverterMslTestCase;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;

public class MSLImportConverter_3_ITest extends ImportConverterMslTestCase {

	private IVendorLibraryMassSpectrum massSpectrum;
	private ILibraryInformation libraryInformation;

	@Override
	protected void setUp() throws Exception {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_DB_4));
		super.setUp();
		massSpectrum = (IVendorLibraryMassSpectrum)massSpectra.getMassSpectrum(1);
		libraryInformation = massSpectrum.getLibraryInformation();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testImport_1() {

		assertEquals("MassSpectra", 1, massSpectra.size());
	}

	public void testImport_2() {

		assertNotNull(massSpectrum);
		assertNotNull(libraryInformation);
	}

	public void testImport_3() {

		assertEquals("Benzoic acid, 2-hydroxy-, ethyl ester", libraryInformation.getName());
		assertEquals("Demo for CAS Numbers, Synonyms", libraryInformation.getComments());
		assertEquals("Database", massSpectrum.getSource());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0, massSpectrum.getRelativeRetentionTime());
		assertEquals(400.7f, massSpectrum.getRetentionIndex());
		assertEquals(10, massSpectrum.getNumberOfIons());
		//
		assertEquals(120.0d, massSpectrum.getBasePeak());
		assertEquals(1000.0f, massSpectrum.getBasePeakAbundance());
		//
		assertEquals(27.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(71.0f, massSpectrum.getLowestIon().getAbundance());
		//
		assertEquals(166.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(346.0f, massSpectrum.getHighestIon().getAbundance());
	}

	public void testImport_4() {

		List<String> casNumbers = libraryInformation.getCasNumbers();
		//
		assertEquals(2, casNumbers.size());
		assertTrue(casNumbers.contains("118-61-6"));
		assertTrue(casNumbers.contains("1476043-16-9"));
	}

	public void testImport_5() {

		Set<String> synonyms = libraryInformation.getSynonyms();
		//
		assertEquals(7, synonyms.size());
		assertTrue(synonyms.contains("o-(Ethoxycarbonyl)phenol"));
		assertTrue(synonyms.contains("Salotan"));
		assertTrue(synonyms.contains("Salicylic ether"));
		assertTrue(synonyms.contains("Salicylic acid, ethyl ester"));
		assertTrue(synonyms.contains("Sal ethyl"));
		assertTrue(synonyms.contains("NSC 8209"));
		assertTrue(synonyms.contains("Mesotol"));
	}

	public void testImport_6() {

		assertEquals(libraryInformation.getInChI(), "1S/C9H10O3/c1-2-12-9(11)7-5-3-4-6-8(7)10/h3-6,10H,2H2,1H3");
	}

	public void testImport_7() {

		assertEquals(libraryInformation.getInChIKey(), "GYCKQBWUSACYIF-UHFFFAOYSA-N");
	}

	public void testImport_8() {

		List<IColumnIndexMarker> columnIndexMarkers = libraryInformation.getColumnIndexMarkers();
		assertEquals(5, columnIndexMarkers.size()); // 1 is set by default
	}

	public void testImport_9() {

		List<IColumnIndexMarker> columnIndexMarkers = libraryInformation.getColumnIndexMarkers();
		//
		IColumnIndexMarker columnIndexMarker = columnIndexMarkers.get(1);
		ISeparationColumn separationColumn = columnIndexMarker.getSeparationColumn();
		assertEquals(1832.0f, columnIndexMarker.getRetentionIndex());
		assertEquals("FFAP", separationColumn.getName());
		assertEquals(SeparationColumnType.POLAR, separationColumn.getSeparationColumnType());
		assertEquals(SeparationColumnPackaging.CAPILLARY, separationColumn.getSeparationColumnPackaging());
		assertEquals("", separationColumn.getCalculationType());
		assertEquals("30 m", separationColumn.getLength());
		assertEquals("0.32 mm", separationColumn.getDiameter());
		assertEquals("FFAP", separationColumn.getPhase());
		assertEquals("", separationColumn.getThickness());
	}

	public void testImport_10() {

		List<IColumnIndexMarker> columnIndexMarkers = libraryInformation.getColumnIndexMarkers();
		//
		IColumnIndexMarker columnIndexMarker = columnIndexMarkers.get(2);
		ISeparationColumn separationColumn = columnIndexMarker.getSeparationColumn();
		assertEquals(1828.0f, columnIndexMarker.getRetentionIndex());
		assertEquals("Supelcowax 10", separationColumn.getName());
		assertEquals(SeparationColumnType.POLAR, separationColumn.getSeparationColumnType());
		assertEquals(SeparationColumnPackaging.CAPILLARY, separationColumn.getSeparationColumnPackaging());
		assertEquals("", separationColumn.getCalculationType());
		assertEquals("60 m", separationColumn.getLength());
		assertEquals("0.25 mm", separationColumn.getDiameter());
		assertEquals("Supelcowax 10", separationColumn.getPhase());
		assertEquals("", separationColumn.getThickness());
	}

	public void testImport11() {

		List<IColumnIndexMarker> columnIndexMarkers = libraryInformation.getColumnIndexMarkers();
		//
		IColumnIndexMarker columnIndexMarker = columnIndexMarkers.get(3);
		ISeparationColumn separationColumn = columnIndexMarker.getSeparationColumn();
		assertEquals(1280.0f, columnIndexMarker.getRetentionIndex());
		assertEquals("HP-5", separationColumn.getName());
		assertEquals(SeparationColumnType.NON_POLAR, separationColumn.getSeparationColumnType());
		assertEquals(SeparationColumnPackaging.CAPILLARY, separationColumn.getSeparationColumnPackaging());
		assertEquals("", separationColumn.getCalculationType());
		assertEquals("50 m", separationColumn.getLength());
		assertEquals("0.3 mm", separationColumn.getDiameter());
		assertEquals("HP-5", separationColumn.getPhase());
		assertEquals("", separationColumn.getThickness());
	}

	public void testImport12() {

		List<IColumnIndexMarker> columnIndexMarkers = libraryInformation.getColumnIndexMarkers();
		//
		IColumnIndexMarker columnIndexMarker = columnIndexMarkers.get(4);
		ISeparationColumn separationColumn = columnIndexMarker.getSeparationColumn();
		assertEquals(1281.0f, columnIndexMarker.getRetentionIndex());
		assertEquals("DB5", separationColumn.getName());
		assertEquals(SeparationColumnType.NON_POLAR, separationColumn.getSeparationColumnType());
		assertEquals(SeparationColumnPackaging.CAPILLARY, separationColumn.getSeparationColumnPackaging());
		assertEquals("", separationColumn.getCalculationType());
		assertEquals("", separationColumn.getLength());
		assertEquals("", separationColumn.getDiameter());
		assertEquals("", separationColumn.getPhase());
		assertEquals("", separationColumn.getThickness());
	}

	public void testImport_11() {

		assertEquals("PubChem", libraryInformation.getContributor());
	}

	public void testImport_12() {

		assertEquals("C9H10O3", libraryInformation.getFormula());
	}

	public void testImport_13() {

		assertEquals(166.0d, libraryInformation.getMolWeight());
	}

	public void testImport_14() {

		assertEquals(166.17d, libraryInformation.getExactMass());
	}
}