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
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.manual.ui.internal.wizards;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IPeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.IPeakLibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.jface.wizard.Wizard;

public class IdentificationWizard extends Wizard {

	private static final Logger logger = Logger.getLogger(IdentificationWizard.class);
	//
	private static final float FACTOR = 100.0f;
	//
	private IdentificationPage identificationPage;
	private IPeak peak;

	public IdentificationWizard(IPeak peak) {
		setNeedsProgressMonitor(true);
		setWindowTitle("Peak Identification");
		this.peak = peak;
	}

	@Override
	public void addPages() {

		identificationPage = new IdentificationPage("Manual Peak Identification");
		addPage(identificationPage);
	}

	@Override
	public boolean performFinish() {

		removeErrorMessage();
		String name = identificationPage.getIdentificationName();
		if(peak == null) {
			showErrorMessage("There is no peak selected.");
			return false;
		} else if(name == null || name.equals("")) {
			showErrorMessage("Please select a name to identify the peak.");
			return false;
		} else {
			/*
			 * Library Info
			 */
			String casNumber = identificationPage.getCasNumber();
			String comments = identificationPage.getComments();
			String formula = identificationPage.getFormula();
			double molWeight = identificationPage.getMolWeight();
			//
			IPeakLibraryInformation libraryInformation = new PeakLibraryInformation();
			libraryInformation.setName(name);
			libraryInformation.setCasNumber(casNumber);
			libraryInformation.setComments(comments);
			libraryInformation.setFormula(formula);
			libraryInformation.setMolWeight(molWeight);
			/*
			 * Comparison Result
			 */
			try {
				IPeakComparisonResult comparisonResult = new PeakComparisonResult(FACTOR, FACTOR, FACTOR, FACTOR, FACTOR);
				IPeakTarget peakTarget = new PeakTarget(libraryInformation, comparisonResult);
				peakTarget.setIdentifier("Manual Peak Identifier");
				if(peak instanceof IChromatogramPeakMSD) {
					((IChromatogramPeakMSD)peak).addTarget(peakTarget);
					return true;
				} else if(peak instanceof IChromatogramPeakCSD) {
					((IChromatogramPeakCSD)peak).addTarget(peakTarget);
					return true;
				} else {
					showErrorMessage("The peak is no instance of IChromatogramPeakMSD or IChromatogramPeakCSD.");
					return false;
				}
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
				return false;
			}
		}
	}

	private void showErrorMessage(String message) {

		identificationPage.setErrorMessage(message);
	}

	private void removeErrorMessage() {

		identificationPage.setErrorMessage(null);
	}
}
