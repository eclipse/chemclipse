/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class TargetBuilder {

	private static final Logger logger = Logger.getLogger(TargetBuilder.class);
	private static final String UNKNOWN = "???";
	private static final float MAX_FACTOR = 100.0f;
	private IonAbundanceComparator ionAbundanceComparator;

	public TargetBuilder() {
		ionAbundanceComparator = new IonAbundanceComparator(SortOrder.DESC);
	}

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

	public void setPeakTargetUnknown(IPeakMSD peakMSD, String identifier) {

		try {
			IScanMSD unknown = peakMSD.getExtractedMassSpectrum();
			ILibraryInformation libraryInformation = getLibraryInformationUnknown(unknown.getIons());
			IComparisonResult comparisonResult = getComparisonResultUnknown();
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
		IIdentificationTarget identificationEntry = null;
		try {
			identificationEntry = new IdentificationTarget(libraryInformation, comparisonResult);
			identificationEntry.setIdentifier(identifier);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
		return identificationEntry;
	}

	public void setMassSpectrumTargetUnknown(IScanMSD unknown, String identifier) {

		try {
			ILibraryInformation libraryInformation = getLibraryInformationUnknown(unknown.getIons());
			IComparisonResult comparisonResult = getComparisonResultUnknown();
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
			libraryInformation.setName(libraryInformationReference.getName());
			libraryInformation.setCasNumber(libraryInformationReference.getCasNumber());
			libraryInformation.setMiscellaneous(libraryInformationReference.getComments());
			libraryInformation.setSmiles(libraryInformationReference.getSmiles());
			libraryInformation.setReferenceIdentifier(libraryInformationReference.getReferenceIdentifier());
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

	private ILibraryInformation getLibraryInformationUnknown(List<IIon> ions) {

		ions = new ArrayList<>(ions);
		ILibraryInformation libraryInformation = new LibraryInformation();
		Collections.sort(ions, ionAbundanceComparator);
		StringBuilder builder = new StringBuilder();
		builder.append("Unknown [");
		int size = (ions.size() >= 5) ? 5 : ions.size();
		for(int i = 0; i < size; i++) {
			builder.append((int)ions.get(i).getIon());
			if(i < size - 1) {
				builder.append(",");
			}
		}
		builder.append("]");
		libraryInformation.setName(builder.toString());
		return libraryInformation;
	}

	private IComparisonResult getComparisonResultUnknown() {

		return new ComparisonResult(MAX_FACTOR, MAX_FACTOR, MAX_FACTOR, MAX_FACTOR);
	}
}
