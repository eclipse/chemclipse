/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.wizards;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.IQuantitationCompoundDocument;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.QuantitationCompoundAlreadyExistsException;
import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;

public class AddAllPeaksWizard extends Wizard {

	private static final Logger logger = Logger.getLogger(AddAllPeaksWizard.class);
	private AddAllPeakPage page;
	private List<IChromatogramPeakMSD> peaks;

	public AddAllPeaksWizard(List<IChromatogramPeakMSD> peaks) {
		setNeedsProgressMonitor(true);
		setWindowTitle("Add All Peak to the Quantitation Table");
		this.peaks = peaks;
	}

	@Override
	public void addPages() {

		page = new AddAllPeakPage("Do you like to add the peaks to the quantitation table?\r\nThey will be identified by their best target.\r\nIf a target already exists in the table, it will be merged.");
		addPage(page);
	}

	@Override
	public boolean performFinish() {

		removeErrorMessage();
		double concentration = page.getConcentration();
		String concentrationUnit = page.getConcentrationUnit();
		if(peaks == null || peaks.size() == 0) {
			showErrorMessage("There is no peak list available.");
			return false;
		} else if(concentrationUnit == null || concentrationUnit.equals("")) {
			showErrorMessage("Please select a concentration unit for compounds that will be created.");
			return false;
		} else if(concentration <= 0) {
			showErrorMessage("Please select a valid concentration.");
			return false;
		} else {
			/*
			 * Library Info
			 */
			try {
				IQuantDatabases databasesQuant = new QuantDatabases();
				IQuantDatabase database = databasesQuant.getQuantDatabase();
				for(IChromatogramPeakMSD peak : peaks) {
					//
					String name = getPeakTargetName(peak);
					if(name == null) {
						logger.warn("The peak has no target. It has been not added to the quantitation table: " + peak);
					} else {
						/*
						 * Name is not null.
						 */
						if(database.isQuantitationCompoundAlreadyAvailable(name)) {
							/*
							 * Merge the quantitation peak
							 */
							IQuantitationCompoundDocument document = database.getQuantitationCompoundDocument(name);
							IQuantitationPeakMSD quantitationPeakMSD = new QuantitationPeakMSD(peak, concentration, document.getConcentrationUnit());
							database.createQuantitationPeakDocument(quantitationPeakMSD, document.getDocumentId());
						} else {
							/*
							 * Add a new peak
							 */
							try {
								int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
								String chemicalClass = page.getChemicalClass();
								//
								IQuantitationCompoundMSD quantitationCompoundMSD = new QuantitationCompoundMSD(name, concentrationUnit, retentionTime);
								quantitationCompoundMSD.setChemicalClass(chemicalClass);
								quantitationCompoundMSD.getRetentionTimeWindow().setAllowedNegativeDeviation(1500); // Default
								quantitationCompoundMSD.getRetentionTimeWindow().setAllowedPositiveDeviation(1500); // Default
								long id = database.createQuantitationCompoundDocument(quantitationCompoundMSD);
								//
								IQuantitationPeakMSD quantitationPeakMSD = new QuantitationPeakMSD(peak, concentration, concentrationUnit);
								database.createQuantitationPeakDocument(quantitationPeakMSD, id);
								//
							} catch(QuantitationCompoundAlreadyExistsException e) {
								logger.warn(e);
							}
						}
					}
				}
				return true;
			} catch(NoDatabaseAvailableException e1) {
				logger.warn(e1);
				showErrorMessage("Please select a quantitation table previously.");
				return false;
			}
		}
	}

	/**
	 * Returns the best matching peak target name or null if there is none.
	 * 
	 * @param chromatogramPeakMSD
	 * @return String
	 */
	private String getPeakTargetName(IChromatogramPeakMSD chromatogramPeakMSD) {

		ILibraryInformation libraryInformation = null;
		float bestMatchFactor = 0.0f;
		//
		if(chromatogramPeakMSD != null) {
			//
			List<IPeakTarget> peakTargets = chromatogramPeakMSD.getTargets();
			if(peakTargets.size() > 0) {
				/*
				 * Get the name of the stored identification entry.
				 */
				for(IPeakTarget peakTarget : peakTargets) {
					if(peakTarget instanceof IPeakTarget) {
						IPeakTarget peakIdentificationEntry = (IPeakTarget)peakTarget;
						float actualMatchFactor = peakIdentificationEntry.getComparisonResult().getMatchFactor();
						if(actualMatchFactor > bestMatchFactor) {
							/*
							 * Best match.
							 */
							bestMatchFactor = actualMatchFactor;
							libraryInformation = peakIdentificationEntry.getLibraryInformation();
						}
					}
				}
			}
		}
		//
		if(libraryInformation == null) {
			return null;
		} else {
			return libraryInformation.getName();
		}
	}

	private void showErrorMessage(String message) {

		page.setErrorMessage(message);
	}

	private void removeErrorMessage() {

		page.setErrorMessage(null);
	}
}
