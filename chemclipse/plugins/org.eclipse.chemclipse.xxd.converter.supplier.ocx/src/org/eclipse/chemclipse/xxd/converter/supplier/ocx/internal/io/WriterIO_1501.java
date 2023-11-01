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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.identifier.IColumnIndexMarker;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IFlavorMarker;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IOdorThreshold;
import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;

public class WriterIO_1501 extends AbstractIO_1501 {

	public void writeSeparationColumn(DataOutputStream dataOutputStream, ISeparationColumn separationColumn) throws IOException {

		writeString(dataOutputStream, separationColumn.getName());
		writeString(dataOutputStream, separationColumn.getSeparationColumnType().name());
		writeString(dataOutputStream, separationColumn.getSeparationColumnPackaging().name());
		writeString(dataOutputStream, separationColumn.getCalculationType());
		writeString(dataOutputStream, separationColumn.getLength());
		writeString(dataOutputStream, separationColumn.getDiameter());
		writeString(dataOutputStream, separationColumn.getPhase());
		writeString(dataOutputStream, separationColumn.getThickness());
	}

	public void writeIdentificationTargets(DataOutputStream dataOutputStream, Set<IIdentificationTarget> identificationTargets) throws IOException {

		dataOutputStream.writeInt(identificationTargets.size());
		for(IIdentificationTarget identificationTarget : identificationTargets) {
			writeIdentificationTarget(dataOutputStream, identificationTarget);
		}
	}

	public void writeTargetDisplaySettings(DataOutputStream dataOutputStream, ITargetDisplaySettings targetDisplaySettings) throws IOException {

		dataOutputStream.writeBoolean(targetDisplaySettings.isShowPeakLabels());
		dataOutputStream.writeBoolean(targetDisplaySettings.isShowScanLabels());
		dataOutputStream.writeInt(targetDisplaySettings.getCollisionDetectionDepth());
		dataOutputStream.writeInt(targetDisplaySettings.getRotation());
		writeString(dataOutputStream, targetDisplaySettings.getLibraryField().name());
		writeString(dataOutputStream, targetDisplaySettings.getDisplayOption().name());
		//
		Map<String, Boolean> visibilityMap = targetDisplaySettings.getVisibilityMap();
		dataOutputStream.writeInt(visibilityMap.size());
		//
		for(Map.Entry<String, Boolean> entry : visibilityMap.entrySet()) {
			writeString(dataOutputStream, entry.getKey());
			dataOutputStream.writeBoolean(entry.getValue());
		}
	}

	private void writeIdentificationTarget(DataOutputStream dataOutputStream, IIdentificationTarget identificationTarget) throws IOException {

		ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
		IComparisonResult comparisonResult = identificationTarget.getComparisonResult();
		//
		writeString(dataOutputStream, identificationTarget.getIdentifier());
		dataOutputStream.writeBoolean(identificationTarget.isVerified());
		/*
		 * ILibraryInformation
		 */
		dataOutputStream.writeInt(libraryInformation.getRetentionTime());
		dataOutputStream.writeFloat(libraryInformation.getRetentionIndex());
		writeCasNumbers(dataOutputStream, libraryInformation);
		writeString(dataOutputStream, libraryInformation.getComments());
		writeString(dataOutputStream, libraryInformation.getReferenceIdentifier());
		writeString(dataOutputStream, libraryInformation.getMiscellaneous());
		writeString(dataOutputStream, libraryInformation.getDatabase());
		dataOutputStream.writeInt(libraryInformation.getDatabaseIndex());
		writeString(dataOutputStream, libraryInformation.getContributor());
		writeString(dataOutputStream, libraryInformation.getName());
		writeSynonyms(dataOutputStream, libraryInformation);
		writeString(dataOutputStream, libraryInformation.getFormula());
		writeString(dataOutputStream, libraryInformation.getSmiles());
		writeString(dataOutputStream, libraryInformation.getInChI());
		writeString(dataOutputStream, libraryInformation.getInChIKey());
		dataOutputStream.writeDouble(libraryInformation.getMolWeight());
		dataOutputStream.writeDouble(libraryInformation.getExactMass());
		writeString(dataOutputStream, libraryInformation.getMoleculeStructure());
		writeColumnIndexMarkers(dataOutputStream, libraryInformation);
		writeFlavorMarkers(dataOutputStream, libraryInformation);
		/*
		 * IComparisonResult
		 */
		dataOutputStream.writeFloat(comparisonResult.getMatchFactor());
		dataOutputStream.writeFloat(comparisonResult.getMatchFactorDirect());
		dataOutputStream.writeFloat(comparisonResult.getReverseMatchFactor());
		dataOutputStream.writeFloat(comparisonResult.getReverseMatchFactorDirect());
		dataOutputStream.writeFloat(comparisonResult.getProbability());
		dataOutputStream.writeBoolean(comparisonResult.isMatch());
		dataOutputStream.writeFloat(comparisonResult.getInLibFactor());
	}

	private void writeCasNumbers(DataOutputStream dataOutputStream, ILibraryInformation libraryInformation) throws IOException {

		List<String> casNumbers = libraryInformation.getCasNumbers();
		int size = casNumbers.size();
		dataOutputStream.writeInt(size);
		//
		for(String casNumber : casNumbers) {
			writeString(dataOutputStream, casNumber);
		}
	}

	private void writeSynonyms(DataOutputStream dataOutputStream, ILibraryInformation libraryInformation) throws IOException {

		Set<String> synonyms = libraryInformation.getSynonyms();
		int size = synonyms.size();
		dataOutputStream.writeInt(size);
		//
		for(String synonym : synonyms) {
			writeString(dataOutputStream, synonym);
		}
	}

	private void writeColumnIndexMarkers(DataOutputStream dataOutputStream, ILibraryInformation libraryInformation) throws IOException {

		List<IColumnIndexMarker> columnIndexMarkers = libraryInformation.getColumnIndexMarkers();
		int size = columnIndexMarkers.size();
		dataOutputStream.writeInt(size);
		//
		for(IColumnIndexMarker columnIndexMarker : columnIndexMarkers) {
			dataOutputStream.writeFloat(columnIndexMarker.getRetentionIndex());
			writeSeparationColumn(dataOutputStream, columnIndexMarker.getSeparationColumn());
		}
	}

	private void writeFlavorMarkers(DataOutputStream dataOutputStream, ILibraryInformation libraryInformation) throws IOException {

		List<IFlavorMarker> flavorMarkers = libraryInformation.getFlavorMarkers();
		int size = flavorMarkers.size();
		dataOutputStream.writeInt(size);
		//
		for(IFlavorMarker flavorMarker : flavorMarkers) {
			dataOutputStream.writeBoolean(flavorMarker.isManuallyVerified());
			writeString(dataOutputStream, flavorMarker.getLiteratureReference());
			writeString(dataOutputStream, flavorMarker.getMatrix());
			writeString(dataOutputStream, flavorMarker.getOdor());
			writeString(dataOutputStream, flavorMarker.getSamplePreparation());
			writeString(dataOutputStream, flavorMarker.getSolvent());
			writeOdorThresholds(dataOutputStream, flavorMarker.getOdorThresholds());
		}
	}

	private void writeOdorThresholds(DataOutputStream dataOutputStream, Set<IOdorThreshold> odorThresholds) throws IOException {

		int size = odorThresholds.size();
		dataOutputStream.writeInt(size);
		//
		for(IOdorThreshold odorThreshold : odorThresholds) {
			writeString(dataOutputStream, odorThreshold.getContent());
			writeString(dataOutputStream, odorThreshold.getUnit());
		}
	}
}