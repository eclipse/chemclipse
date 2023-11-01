/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnPackaging;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ChromatogramComparisonResult;
import org.eclipse.chemclipse.model.identifier.ColumnIndexMarker;
import org.eclipse.chemclipse.model.identifier.FlavorMarker;
import org.eclipse.chemclipse.model.identifier.IColumnIndexMarker;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IFlavorMarker;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IOdorThreshold;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.identifier.OdorThreshold;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.targets.DisplayOption;
import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.LibraryField;

public class ReaderIO_1501 extends AbstractIO_1501 {

	private static final Logger logger = Logger.getLogger(ReaderIO_1501.class);

	public ISeparationColumn readSeparationColumn(DataInputStream dataInputStream) throws IOException {

		String name = readString(dataInputStream);
		SeparationColumnType separationColumnType = readSeparationColumnType(dataInputStream);
		SeparationColumnPackaging separationColumnPackaging = readSeparationColumnPackaging(dataInputStream);
		String calculationType = readString(dataInputStream);
		String length = readString(dataInputStream);
		String diameter = readString(dataInputStream);
		String phase = readString(dataInputStream);
		String thickness = readString(dataInputStream);
		//
		ISeparationColumn separationColumn = new SeparationColumn(name, separationColumnType, length, diameter, phase);
		separationColumn.setSeparationColumnPackaging(separationColumnPackaging);
		separationColumn.setCalculationType(calculationType);
		separationColumn.setThickness(thickness);
		//
		return separationColumn;
	}

	public void readIdentificationTargets(DataInputStream dataInputStream, boolean closeStream, ITargetSupplier targetSupplier) throws IOException {

		List<IIdentificationTarget> identificationTargets = readIdentificationTargets(dataInputStream, closeStream);
		targetSupplier.getTargets().addAll(identificationTargets);
	}

	public void readTargetDisplaySettings(DataInputStream dataInputStream, ITargetDisplaySettings targetDisplaySettings) throws IOException {

		targetDisplaySettings.setShowPeakLabels(dataInputStream.readBoolean());
		targetDisplaySettings.setShowScanLabels(dataInputStream.readBoolean());
		targetDisplaySettings.setCollisionDetectionDepth(dataInputStream.readInt());
		targetDisplaySettings.setRotation(dataInputStream.readInt());
		targetDisplaySettings.setLibraryField(readLibraryField(dataInputStream));
		targetDisplaySettings.setDisplayOption(readDisplayOption(dataInputStream));
		//
		Map<String, Boolean> visibilityMap = new HashMap<>();
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			String key = readString(dataInputStream);
			boolean value = dataInputStream.readBoolean();
			visibilityMap.put(key, value);
		}
		//
		targetDisplaySettings.putAll(visibilityMap);
	}

	private List<IIdentificationTarget> readIdentificationTargets(DataInputStream dataInputStream, boolean closeStream) throws IOException {

		List<IIdentificationTarget> identificationTargets = new ArrayList<>();
		//
		int numberOfTargets = dataInputStream.readInt();
		for(int i = 1; i <= numberOfTargets; i++) {
			//
			String identifier = readString(dataInputStream);
			boolean manuallyVerified = dataInputStream.readBoolean();
			//
			int retentionTime = dataInputStream.readInt();
			float retentionIndex = dataInputStream.readFloat();
			List<String> casNumbers = readCasNumbers(dataInputStream);
			String comments = readString(dataInputStream);
			String referenceIdentifier = readString(dataInputStream);
			String miscellaneous = readString(dataInputStream);
			String database = readString(dataInputStream);
			int databaseIndex = dataInputStream.readInt();
			String contributor = readString(dataInputStream);
			String name = readString(dataInputStream);
			Set<String> synonyms = readSynonyms(dataInputStream);
			String formula = readString(dataInputStream);
			String smiles = readString(dataInputStream);
			String inChI = readString(dataInputStream);
			String inChIKey = readString(dataInputStream);
			double molWeight = dataInputStream.readDouble();
			double exactMass = dataInputStream.readDouble();
			String moleculeStructure = readString(dataInputStream);
			List<IColumnIndexMarker> columnIndexMarkers = readColumnIndexMarkers(dataInputStream);
			List<IFlavorMarker> flavorMarkers = readFlavorMarkers(dataInputStream);
			/*
			 * IComparisonResult
			 */
			float matchFactor = dataInputStream.readFloat();
			float matchFactorDirect = dataInputStream.readFloat();
			float reverseMatchFactor = dataInputStream.readFloat();
			float reverseMatchFactorDirect = dataInputStream.readFloat();
			float probability = dataInputStream.readFloat();
			boolean isMatch = dataInputStream.readBoolean();
			float inLibFactor = dataInputStream.readFloat();
			/*
			 * Compile
			 */
			ILibraryInformation libraryInformation = new LibraryInformation();
			libraryInformation.setRetentionTime(retentionTime);
			libraryInformation.setRetentionIndex(retentionIndex);
			assignCasNumbers(libraryInformation, casNumbers);
			libraryInformation.setComments(comments);
			libraryInformation.setReferenceIdentifier(referenceIdentifier);
			libraryInformation.setMiscellaneous(miscellaneous);
			libraryInformation.setDatabase(database);
			libraryInformation.setDatabaseIndex(databaseIndex);
			libraryInformation.setContributor(contributor);
			libraryInformation.setName(name);
			libraryInformation.setSynonyms(synonyms);
			libraryInformation.setFormula(formula);
			libraryInformation.setSmiles(smiles);
			libraryInformation.setInChI(inChI);
			libraryInformation.setInChIKey(inChIKey);
			libraryInformation.setMolWeight(molWeight);
			libraryInformation.setExactMass(exactMass);
			libraryInformation.setMoleculeStructure(moleculeStructure);
			assignColumnIndexMarker(libraryInformation, columnIndexMarkers);
			assignFlavorMarker(libraryInformation, flavorMarkers);
			//
			IComparisonResult comparisonResult = new ChromatogramComparisonResult(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
			comparisonResult.setMatch(isMatch);
			comparisonResult.setInLibFactor(inLibFactor);
			//
			try {
				IIdentificationTarget identificationTarget = new IdentificationTarget(libraryInformation, comparisonResult);
				identificationTarget.setIdentifier(identifier);
				identificationTarget.setVerified(manuallyVerified);
				identificationTargets.add(identificationTarget);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
		//
		return identificationTargets;
	}

	private void assignCasNumbers(ILibraryInformation libraryInformation, List<String> casNumbers) {

		for(String casNumber : casNumbers) {
			libraryInformation.addCasNumber(casNumber);
		}
	}

	private void assignColumnIndexMarker(ILibraryInformation libraryInformation, List<IColumnIndexMarker> columnIndexMarkers) {

		for(IColumnIndexMarker columnIndexMarker : columnIndexMarkers) {
			libraryInformation.add(columnIndexMarker);
		}
	}

	private void assignFlavorMarker(ILibraryInformation libraryInformation, List<IFlavorMarker> flavorMarkers) {

		for(IFlavorMarker flavorMarker : flavorMarkers) {
			libraryInformation.add(flavorMarker);
		}
	}

	private List<String> readCasNumbers(DataInputStream dataInputStream) throws IOException {

		List<String> casNumbers = new ArrayList<>();
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			casNumbers.add(readString(dataInputStream));
		}
		//
		return casNumbers;
	}

	private Set<String> readSynonyms(DataInputStream dataInputStream) throws IOException {

		Set<String> synonyms = new HashSet<>();
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			synonyms.add(readString(dataInputStream));
		}
		//
		return synonyms;
	}

	private List<IColumnIndexMarker> readColumnIndexMarkers(DataInputStream dataInputStream) throws IOException {

		List<IColumnIndexMarker> columnIndexMarkers = new ArrayList<>();
		//
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			float retentionIndex = dataInputStream.readFloat();
			ISeparationColumn separationColumn = readSeparationColumn(dataInputStream);
			IColumnIndexMarker columnIndexMarker = new ColumnIndexMarker(separationColumn, retentionIndex);
			columnIndexMarkers.add(columnIndexMarker);
		}
		//
		return columnIndexMarkers;
	}

	private List<IFlavorMarker> readFlavorMarkers(DataInputStream dataInputStream) throws IOException {

		List<IFlavorMarker> flavorMarkers = new ArrayList<>();
		//
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			boolean manuallyVerified = dataInputStream.readBoolean();
			String literatureReference = readString(dataInputStream);
			String matrix = readString(dataInputStream);
			String odor = readString(dataInputStream);
			String samplePreparation = readString(dataInputStream);
			String solvent = readString(dataInputStream);
			//
			IFlavorMarker flavorMarker = new FlavorMarker(odor, matrix, solvent);
			flavorMarker.setManuallyVerified(manuallyVerified);
			flavorMarker.setLiteratureReference(literatureReference);
			flavorMarker.setSamplePreparation(samplePreparation);
			readOdorThresholds(dataInputStream, flavorMarker);
			flavorMarkers.add(flavorMarker);
		}
		//
		return flavorMarkers;
	}

	private void readOdorThresholds(DataInputStream dataInputStream, IFlavorMarker flavorMarker) throws IOException {

		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			String content = readString(dataInputStream);
			String unit = readString(dataInputStream);
			IOdorThreshold odorThreshold = new OdorThreshold(content, unit);
			flavorMarker.add(odorThreshold);
		}
	}

	private SeparationColumnType readSeparationColumnType(DataInputStream dataInputStream) {

		try {
			return SeparationColumnType.valueOf(readString(dataInputStream));
		} catch(IOException e) {
			return SeparationColumnType.DEFAULT;
		}
	}

	private SeparationColumnPackaging readSeparationColumnPackaging(DataInputStream dataInputStream) {

		try {
			return SeparationColumnPackaging.valueOf(readString(dataInputStream));
		} catch(IOException e) {
			return SeparationColumnPackaging.CAPILLARY;
		}
	}

	private LibraryField readLibraryField(DataInputStream dataInputStream) {

		try {
			return LibraryField.valueOf(readString(dataInputStream));
		} catch(Exception e) {
			return LibraryField.NAME;
		}
	}

	private DisplayOption readDisplayOption(DataInputStream dataInputStream) {

		try {
			return DisplayOption.valueOf(readString(dataInputStream));
		} catch(Exception e) {
			return DisplayOption.STANDARD;
		}
	}
}