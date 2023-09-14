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
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.columns.SeparationColumnPackaging;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.identifier.ColumnIndexMarker;
import org.eclipse.chemclipse.model.identifier.IColumnIndexMarker;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.database.IDatabaseExportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.ImportConverterMslTestCase;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.VendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.core.runtime.NullProgressMonitor;

public class MSLExportConverter_2_ITest extends ImportConverterMslTestCase {

	private IDatabaseExportConverter exportConverter;
	private File exportFile;
	private IVendorLibraryMassSpectrum scanMSD;
	private IMassSpectra scansMSD;
	//
	private IVendorLibraryMassSpectrum massSpectrum;
	private ILibraryInformation libraryInformation;

	@Override
	protected void setUp() throws Exception {

		/*
		 * Export
		 */
		exportConverter = new MSLDatabaseExportConverter();
		exportFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTDIR_EXPORT) + File.separator + TestPathHelper.TESTFILE_EXPORT_DB_2_MSL);
		scanMSD = new VendorLibraryMassSpectrum();
		ILibraryInformation libraryInformationMS = scanMSD.getLibraryInformation();
		libraryInformationMS.setName("Benzoic acid, 2-hydroxy-, ethyl ester");
		libraryInformationMS.addCasNumber("118-61-6");
		libraryInformationMS.addCasNumber("1476043-16-9");
		libraryInformationMS.getSynonyms().add("o-(Ethoxycarbonyl)phenol");
		libraryInformationMS.getSynonyms().add("Salotan");
		libraryInformationMS.getSynonyms().add("Salicylic ether");
		libraryInformationMS.getSynonyms().add("Salicylic acid, ethyl ester");
		libraryInformationMS.getSynonyms().add("Sal ethyl");
		libraryInformationMS.getSynonyms().add("NSC 8209");
		libraryInformationMS.getSynonyms().add("Mesotol");
		libraryInformationMS.setInChI("1S/C9H10O3/c1-2-12-9(11)7-5-3-4-6-8(7)10/h3-6,10H,2H2,1H3");
		libraryInformationMS.setInChIKey("GYCKQBWUSACYIF-UHFFFAOYSA-N");
		scanMSD.setRetentionTime(0);
		scanMSD.setRelativeRetentionTime(0);
		scanMSD.setRetentionIndex(400.7f);
		libraryInformationMS.add(createColumnIndexMarker(1832.0f, "FFAP", SeparationColumnType.POLAR, SeparationColumnPackaging.CAPILLARY, "", "30 m", "0.32 mm", "FFAP", ""));
		libraryInformationMS.add(createColumnIndexMarker(1828.0f, "Supelcowax 10", SeparationColumnType.POLAR, SeparationColumnPackaging.CAPILLARY, "", "60 m", "0.25 mm", "Supelcowax 10", ""));
		libraryInformationMS.add(createColumnIndexMarker(1280.0f, "HP-5", SeparationColumnType.NON_POLAR, SeparationColumnPackaging.CAPILLARY, "", "50 m", "0.3 mm", "HP-5", ""));
		libraryInformationMS.add(createColumnIndexMarker(1281.0f, "DB5", SeparationColumnType.NON_POLAR, SeparationColumnPackaging.CAPILLARY, "", "", "", "", ""));
		libraryInformationMS.setContributor("PubChem");
		libraryInformationMS.setFormula("C9H10O3");
		libraryInformationMS.setMolWeight(166.0d);
		libraryInformationMS.setExactMass(166.17d);
		libraryInformationMS.setComments("Demo for CAS Numbers, Synonyms");
		scanMSD.setSource("Database");
		scanMSD.addIon(new Ion(120.0f, 1000.0f));
		scanMSD.addIon(new Ion(92.0f, 401.0f));
		scanMSD.addIon(new Ion(166.0f, 346.0f));
		scanMSD.addIon(new Ion(121.0f, 314.0f));
		scanMSD.addIon(new Ion(39.0f, 147.0f));
		scanMSD.addIon(new Ion(65.0f, 137.0f));
		scanMSD.addIon(new Ion(93.0f, 91.0f));
		scanMSD.addIon(new Ion(64.0f, 85.0f));
		scanMSD.addIon(new Ion(27.0f, 71.0f));
		scanMSD.addIon(new Ion(63.0f, 67.0f));
		scansMSD = new MassSpectra();
		scansMSD.addMassSpectrum(scanMSD);
		exportConverter.convert(exportFile, scansMSD, false, new NullProgressMonitor());
		//
		importFile = exportFile;
		super.setUp();
		/*
		 * Import
		 */
		massSpectrum = (IVendorLibraryMassSpectrum)massSpectra.getMassSpectrum(1);
		libraryInformation = massSpectrum.getLibraryInformation();
	}

	@Override
	protected void tearDown() throws Exception {

		exportConverter = null;
		scansMSD = null;
		scanMSD = null;
		exportFile = null;
		super.tearDown();
	}

	public void testImport_1() {

		assertEquals(1, massSpectra.size());
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

	private IColumnIndexMarker createColumnIndexMarker(float retentionIndex, String name, SeparationColumnType separationColumnType, SeparationColumnPackaging separationColumnPackaging, String calculationType, String length, String diameter, String phase, String thickness) {

		ISeparationColumn separationColumn = SeparationColumnFactory.getSeparationColumn(name, length, diameter, phase);
		separationColumn.setSeparationColumnType(separationColumnType);
		separationColumn.setSeparationColumnPackaging(separationColumnPackaging);
		separationColumn.setCalculationType(calculationType);
		separationColumn.setThickness(thickness);
		//
		return new ColumnIndexMarker(separationColumn, retentionIndex);
	}
}