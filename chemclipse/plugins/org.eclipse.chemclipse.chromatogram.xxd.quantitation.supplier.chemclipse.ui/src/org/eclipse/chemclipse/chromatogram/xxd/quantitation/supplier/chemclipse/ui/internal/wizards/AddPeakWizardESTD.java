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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.wizard.Wizard;

public class AddPeakWizardESTD extends Wizard {

	private static final Logger logger = Logger.getLogger(AddPeakWizardESTD.class);
	//
	private QuantitationDocumentPageESTD quantitationDocumentPage;
	private IQuantitationDatabase quantitationDatabase;
	private IChromatogramPeakMSD chromatogramPeakMSD;
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public AddPeakWizardESTD(IQuantitationDatabase quantitationDatabase, IChromatogramPeakMSD chromatogramPeakMSD) {

		setNeedsProgressMonitor(true);
		setWindowTitle("Add Peak (ESTD)");
		this.quantitationDatabase = quantitationDatabase;
		this.chromatogramPeakMSD = chromatogramPeakMSD;
	}

	@Override
	public void addPages() {

		List<String> peakTargetNames = getPeakTargetNames(chromatogramPeakMSD);
		quantitationDocumentPage = new QuantitationDocumentPageESTD("Quantitation Document", peakTargetNames, quantitationDatabase);
		addPage(quantitationDocumentPage);
	}

	@Override
	public boolean performFinish() {

		removeErrorMessage();
		if(quantitationDatabase == null) {
			showErrorMessage("Please select a valid quantitation database.");
			return false;
		} else {
			/*
			 * The database must be not null.
			 */
			if(quantitationDocumentPage.buttonMerge.getSelection()) {
				return checkAndMergeQuantitationDocument();
			} else {
				return checkAndCreateNewQuantitationDocument();
			}
		}
	}

	private boolean checkAndMergeQuantitationDocument() {

		/*
		 * Merge the peak with the existing quantitation document.
		 */
		String name = quantitationDocumentPage.comboQuantitationCompoundNames.getText().trim();
		IQuantitationCompound quantitationCompoundMSD = quantitationDatabase.getQuantitationCompound(name);
		if(quantitationCompoundMSD != null) {
			try {
				double concentration = decimalFormat.parse(quantitationDocumentPage.textConcentrationMerge.getText().trim()).doubleValue();
				if(concentration > 0) {
					String concentrationUnit = quantitationCompoundMSD.getConcentrationUnit();
					IQuantitationPeak quantitationPeakMSD = new QuantitationPeakMSD(chromatogramPeakMSD, concentration, concentrationUnit);
					quantitationCompoundMSD.getQuantitationPeaks().add(quantitationPeakMSD);
					return true;
				} else {
					showErrorMessage("Please select a valid concentration.");
				}
			} catch(ParseException e) {
				logger.warn(e);
				showErrorMessage("Please type in a valid concentration.");
			}
		} else {
			showErrorMessage("An existing quantitation compound must be selected.");
		}
		return false;
	}

	private boolean checkAndCreateNewQuantitationDocument() {

		/*
		 * Create a new quantitation document.
		 */
		String name = quantitationDocumentPage.comboPeakTargetNames.getText().trim();
		if(name == null || name.equals("")) {
			showErrorMessage("Please select a quantitation compound name.");
		} else {
			/*
			 * Selected document
			 */
			boolean documentStillExists = quantitationDatabase.containsQuantitationCompund(name);
			if(documentStillExists) {
				showErrorMessage("The quantitation compound still exists in the database.");
			} else {
				/*
				 * Concentration Unit
				 */
				String concentrationUnit = quantitationDocumentPage.textConcentrationUnitCreate.getText().trim();
				if(concentrationUnit == null || concentrationUnit.equals("")) {
					showErrorMessage("Please select a concentration unit.");
				} else {
					/*
					 * Concentration
					 */
					try {
						double concentration = decimalFormat.parse(quantitationDocumentPage.textConcentrationCreate.getText().trim()).doubleValue();
						if(concentration >= 0) {
							// >= 0 to allow to add a background/noise peak
							QuantitationCompoundSupport quantitationCompoundSupport = new QuantitationCompoundSupport();
							String chemicalClass = quantitationDocumentPage.textChemicalClassCreate.getText().trim();
							IQuantitationCompound quantitationCompound = quantitationCompoundSupport.create(chromatogramPeakMSD, name, concentration, concentrationUnit, chemicalClass);
							quantitationDatabase.add(quantitationCompound);
							return true;
						} else {
							showErrorMessage("Please select a valid concentration.");
						}
					} catch(ParseException e1) {
						logger.warn(e1);
						showErrorMessage("Please type in a valid concentration.");
					}
				}
			}
		}
		return false;
	}

	private void showErrorMessage(String message) {

		quantitationDocumentPage.setErrorMessage(message);
	}

	private void removeErrorMessage() {

		quantitationDocumentPage.setErrorMessage(null);
	}

	/**
	 * Returns the peak target names.
	 * 
	 * @param chromatogramPeakMSD
	 * @return List<String>
	 */
	private List<String> getPeakTargetNames(IPeak peak) {

		List<String> peakTargetNames = new ArrayList<String>();
		if(peak != null) {
			Set<IIdentificationTarget> peakTargets = peak.getTargets();
			if(peakTargets.size() > 0) {
				/*
				 * Get the name of the stored identification entry.
				 */
				for(IIdentificationTarget peakTarget : peakTargets) {
					if(peakTarget instanceof IIdentificationTarget) {
						IIdentificationTarget peakIdentificationEntry = (IIdentificationTarget)peakTarget;
						String name = peakIdentificationEntry.getLibraryInformation().getName();
						peakTargetNames.add(name);
					}
				}
			}
		}
		return peakTargetNames;
	}
}
