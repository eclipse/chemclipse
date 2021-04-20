/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.TargetUnknownSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class TargetBuilder {

	private static final Logger logger = Logger.getLogger(TargetBuilder.class);
	//
	private static final String UNKNOWN = "???";
	private static final float MIN_FACTOR = 0.0f;
	private static final float MAX_FACTOR = 100.0f;
	//
	private final IonAbundanceComparator ionAbundanceComparator = new IonAbundanceComparator(SortOrder.DESC);

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
			ILibraryInformation libraryInformation = getLibraryInformationUnknown(unknown, targetUnknownSettings);
			IComparisonResult comparisonResult = getComparisonResultUnknown(targetUnknownSettings.getMatchQuality());
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
			ILibraryInformation libraryInformation = getLibraryInformationUnknown(unknown, targetUnknownSettings);
			IComparisonResult comparisonResult = getComparisonResultUnknown(targetUnknownSettings.getMatchQuality());
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

	private ILibraryInformation getLibraryInformationUnknown(IScanMSD unknown, TargetUnknownSettings targetUnknownSettings) {

		/*
		 * Unknown [57,71,43,85,41]
		 * Unknown [57,71,43,85,41 RI 782]
		 * Unknown [57,71,43,85,41 RT 4.34]
		 */
		List<IIon> ionsSorted = new ArrayList<>(unknown.getIons());
		ILibraryInformation libraryInformation = new LibraryInformation();
		Collections.sort(ionsSorted, ionAbundanceComparator);
		boolean includeIntensityPercent = targetUnknownSettings.isIncludeIntensityPercent();
		//
		StringBuilder builder = new StringBuilder();
		builder.append(targetUnknownSettings.getTargetName());
		builder.append(" ");
		builder.append(targetUnknownSettings.getMarkerStart());
		//
		int numberMZ = targetUnknownSettings.getNumberMZ();
		int size = (ionsSorted.size() >= numberMZ) ? numberMZ : ionsSorted.size();
		double maxIntensity = ionsSorted.size() > 0 ? ionsSorted.get(0).getAbundance() : 0;
		double factorMax = maxIntensity > 0 ? (100 / maxIntensity) : 0;
		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0");
		/*
		 * Ion(s)
		 */
		for(int i = 0; i < size; i++) {
			IIon ion = ionsSorted.get(i);
			builder.append(AbstractIon.getIon(ion.getIon()));
			if(includeIntensityPercent) {
				builder.append("|");
				double percent = factorMax * ion.getAbundance();
				builder.append(decimalFormat.format(percent));
			}
			/*
			 * Next entry available?
			 */
			if(i < size - 1) {
				builder.append(",");
			}
		}
		/*
		 * Retention Time
		 */
		if(targetUnknownSettings.isIncludeRetentionTime()) {
			builder.append(" ");
			builder.append("RT");
			builder.append(" ");
			double retentionTimeInMinutes = unknown.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR;
			builder.append(ValueFormat.getDecimalFormatEnglish("0.000").format(retentionTimeInMinutes));
		}
		/*
		 * Retention Index
		 */
		if(targetUnknownSettings.isIncludeRetentionIndex()) {
			builder.append(" ");
			builder.append("RI");
			builder.append(" ");
			float retentionIndex = unknown.getRetentionIndex();
			builder.append(ValueFormat.getDecimalFormatEnglish("0").format(retentionIndex));
		}
		//
		builder.append(targetUnknownSettings.getMarkerStop());
		libraryInformation.setName(builder.toString());
		//
		return libraryInformation;
	}

	private IComparisonResult getComparisonResultUnknown(float matchQuality) {

		if(matchQuality < MIN_FACTOR || matchQuality > MAX_FACTOR) {
			matchQuality = MAX_FACTOR;
		}
		return new ComparisonResult(matchQuality, 0.0f, 0.0f, 0.0f);
	}
}
