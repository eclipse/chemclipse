/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.ui.internal.wizards;

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.io.DatabaseSupport;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.jface.wizard.Wizard;

public class AddPeaksWizardESTD extends Wizard {

	private static final Logger logger = Logger.getLogger(AddPeaksWizardESTD.class);
	private AddPeaksPageESTD page;
	private List<IPeak> peaks;

	public AddPeaksWizardESTD(List<IPeak> peaks) {

		setNeedsProgressMonitor(true);
		setWindowTitle("Add Peaks (ESTD)");
		this.peaks = peaks;
	}

	@Override
	public void addPages() {

		page = new AddPeaksPageESTD("PeakPageESTD");
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
		} else if(concentration < 0) {
			// < 0 to allow to add a background/noise peak
			showErrorMessage("Please select a valid concentration.");
			return false;
		} else {
			/*
			 * Database
			 */
			DatabaseSupport databaseSupport = new DatabaseSupport();
			IQuantitationDatabase quantitationDatabase = databaseSupport.load();
			if(quantitationDatabase != null && quantitationDatabase.size() >= 0) {
				//
				for(IPeak peak : peaks) {
					//
					String name = getPeakTargetName(peak);
					if(name == null) {
						logger.warn("The peak has no target. It has been not added to the quantitation database: " + peak);
					} else {
						/*
						 * Name is not null.
						 */
						if(peak instanceof IPeakMSD) {
							QuantitationCompoundSupport quantitationCompoundSupport = new QuantitationCompoundSupport();
							if(quantitationDatabase.containsQuantitationCompund(name)) {
								/*
								 * Merge
								 */
								quantitationCompoundSupport.merge(quantitationDatabase, peak, name, concentration);
							} else {
								/*
								 * New
								 */
								String chemicalClass = page.getChemicalClass();
								IQuantitationCompound quantitationCompound = quantitationCompoundSupport.create(peak, name, concentration, concentrationUnit, chemicalClass);
								quantitationDatabase.add(quantitationCompound);
							}
						}
					}
				}
				/*
				 * Save
				 */
				databaseSupport.save(quantitationDatabase);
				return true;
			} else {
				showErrorMessage("Please select a quantitation table previously.");
				return false;
			}
		}
	}

	/**
	 * Returns the best matching peak target name or null if there is none.
	 * 
	 * @param peak
	 * @return String
	 */
	private String getPeakTargetName(IPeak peak) {

		ILibraryInformation libraryInformation = null;
		float bestMatchFactor = 0.0f;
		//
		if(peak != null) {
			//
			Set<IIdentificationTarget> peakTargets = peak.getTargets();
			if(peakTargets.size() > 0) {
				/*
				 * Get the name of the stored identification entry.
				 */
				for(IIdentificationTarget peakTarget : peakTargets) {
					if(peakTarget instanceof IIdentificationTarget) {
						IIdentificationTarget peakIdentificationEntry = (IIdentificationTarget)peakTarget;
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
