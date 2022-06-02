/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.support;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.targets.TargetUnknownSettings;
import org.eclipse.chemclipse.model.targets.UnknownTargetBuilder;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class TargetBuilderMSD {

	private static final Logger logger = Logger.getLogger(TargetBuilderMSD.class);
	//
	private static final String UNKNOWN = "???";

	public IIdentificationTarget getPeakTarget(IScanMSD reference, IComparisonResult comparisonResult, String identifier) {

		return getPeakTarget(reference, comparisonResult, identifier, "");
	}

	/**
	 * Use e.g.
	 * identifier = File Identifier
	 * database = Test.msl
	 * 
	 * @param reference
	 * @param comparisonResult
	 * @param identifier
	 * @param database
	 * @return
	 */
	public IIdentificationTarget getPeakTarget(IScanMSD reference, IComparisonResult comparisonResult, String identifier, String database) {

		ILibraryInformation libraryInformation = new PeakLibraryInformation();
		initializeLibraryInformation(libraryInformation, reference);
		libraryInformation.setDatabase(database);
		//
		IIdentificationTarget peakTarget = null;
		try {
			peakTarget = new IdentificationTarget(libraryInformation, comparisonResult);
			peakTarget.setIdentifier(identifier);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
		return peakTarget;
	}

	public void setPeakTargetUnknown(IPeakMSD peakMSD, String identifier, TargetUnknownSettings targetUnknownSettings) {

		try {
			IScanMSD unknown = peakMSD.getExtractedMassSpectrum();
			String traces = extractTraces(unknown, targetUnknownSettings);
			ILibraryInformation libraryInformation = UnknownTargetBuilder.getLibraryInformationUnknown(unknown, targetUnknownSettings, traces);
			IComparisonResult comparisonResult = UnknownTargetBuilder.getComparisonResultUnknown(targetUnknownSettings.getMatchQuality());
			IIdentificationTarget peakTarget = new IdentificationTarget(libraryInformation, comparisonResult);
			peakTarget.setIdentifier(identifier);
			peakMSD.getTargets().add(peakTarget);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
	}

	public IIdentificationTarget getMassSpectrumTarget(IScanMSD reference, IComparisonResult comparisonResult, String identifier) {

		return getMassSpectrumTarget(reference, comparisonResult, identifier, "");
	}

	public IIdentificationTarget getMassSpectrumTarget(IScanMSD reference, IComparisonResult comparisonResult, String identifier, String database) {

		ILibraryInformation libraryInformation = new LibraryInformation();
		initializeLibraryInformation(libraryInformation, reference);
		libraryInformation.setDatabase(database);
		//
		IdentificationTarget identificationEntry = null;
		try {
			identificationEntry = new IdentificationTarget(libraryInformation, comparisonResult);
			identificationEntry.setIdentifier(identifier);
			identificationEntry.setLibraryScan(reference);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
		return identificationEntry;
	}

	public void setMassSpectrumTargetUnknown(IScanMSD unknown, String identifier, TargetUnknownSettings targetUnknownSettings) {

		try {
			String traces = extractTraces(unknown, targetUnknownSettings);
			ILibraryInformation libraryInformation = UnknownTargetBuilder.getLibraryInformationUnknown(unknown, targetUnknownSettings, traces);
			IComparisonResult comparisonResult = UnknownTargetBuilder.getComparisonResultUnknown(targetUnknownSettings.getMatchQuality());
			IIdentificationTarget massSpectrumTarget = new IdentificationTarget(libraryInformation, comparisonResult);
			massSpectrumTarget.setIdentifier(identifier);
			unknown.getTargets().add(massSpectrumTarget);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
	}

	private void initializeLibraryInformation(ILibraryInformation libraryInformation, IScanMSD reference) {

		if(reference instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)reference;
			ILibraryInformation libraryInformationReference = libraryMassSpectrum.getLibraryInformation();
			/*
			 * Reference
			 */
			libraryInformation.setCasNumber(libraryInformationReference.getCasNumber());
			libraryInformation.setComments(libraryInformationReference.getComments());
			libraryInformation.setContributor(libraryInformationReference.getContributor());
			libraryInformation.setDatabase(libraryInformationReference.getDatabase());
			libraryInformation.setFormula(libraryInformationReference.getFormula());
			libraryInformation.setInChI(libraryInformationReference.getInChI());
			libraryInformation.setMiscellaneous(libraryInformationReference.getMiscellaneous());
			libraryInformation.setMolWeight(libraryInformationReference.getMolWeight());
			libraryInformation.setName(libraryInformationReference.getName());
			libraryInformation.setReferenceIdentifier(libraryInformationReference.getReferenceIdentifier());
			libraryInformation.setSmiles(libraryInformationReference.getSmiles());
			libraryInformation.setSynonyms(libraryInformationReference.getSynonyms());
			libraryInformationReference.getClassifier().forEach(libraryInformation::addClassifier);
		} else {
			/*
			 * Unknown
			 */
			libraryInformation.setName(UNKNOWN);
			libraryInformation.setCasNumber(UNKNOWN);
			libraryInformation.setMiscellaneous(UNKNOWN);
		}
		//
		libraryInformation.setRetentionTime(reference.getRetentionTime());
		libraryInformation.setRetentionIndex(reference.getRetentionIndex());
	}

	private String extractTraces(IScanMSD unknown, TargetUnknownSettings targetUnknownSettings) {

		String traces = "";
		int numberMZ = targetUnknownSettings.getNumberTraces();
		if(numberMZ > 0) {
			/*
			 * m/z
			 */
			StringBuilder builder = new StringBuilder();
			List<IIon> ionsSorted = new ArrayList<>(unknown.getIons());
			Collections.sort(ionsSorted, (s1, s2) -> Float.compare(s2.getAbundance(), s1.getAbundance()));
			boolean includeIntensityPercent = targetUnknownSettings.isIncludeIntensityPercent();
			//
			int size = (ionsSorted.size() >= numberMZ) ? numberMZ : ionsSorted.size();
			double maxIntensity = !ionsSorted.isEmpty() ? ionsSorted.get(0).getAbundance() : 0;
			double factorMax = maxIntensity > 0 ? (100 / maxIntensity) : 0;
			DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0");
			/*
			 * Ions
			 */
			for(int i = 0; i < size; i++) {
				IIon ion = ionsSorted.get(i);
				builder.append(AbstractIon.getIon(ion.getIon()));
				if(includeIntensityPercent) {
					builder.append(UnknownTargetBuilder.DELIMITER_INTENSITY);
					double percent = factorMax * ion.getAbundance();
					builder.append(decimalFormat.format(percent));
				}
				/*
				 * Next entry available?
				 */
				if(i < size - 1) {
					builder.append(UnknownTargetBuilder.DELIMITER_TRACES);
				}
			}
			//
			traces = builder.toString();
		}
		//
		return traces;
	}
}
