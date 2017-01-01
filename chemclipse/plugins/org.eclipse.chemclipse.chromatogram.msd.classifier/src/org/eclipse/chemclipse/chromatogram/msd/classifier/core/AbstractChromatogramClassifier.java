/*******************************************************************************
 * Copyright (c) 2011, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.ChromatogramSelectionException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.ClassifierSettingsException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

/**
 * @author eselmeister
 */
public abstract class AbstractChromatogramClassifier implements IChromatogramClassifier {

	public void validate(IChromatogramSelectionMSD chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings) throws ChromatogramSelectionException, ClassifierSettingsException {

		validateChromatogramSelection(chromatogramSelection);
		validateClassifierSettings(chromatogramClassifierSettings);
	}

	// --------------------------------------------private methods
	/**
	 * Validates that chromatogram selection and the stored chromatogram are not
	 * null.
	 * 
	 * @param chromatogramSelection
	 * @throws ChromatogramSelectionException
	 */
	private void validateChromatogramSelection(IChromatogramSelectionMSD chromatogramSelection) throws ChromatogramSelectionException {

		if(chromatogramSelection == null) {
			throw new ChromatogramSelectionException("The chromatogram selection must not be null.");
		}
		if(chromatogramSelection.getChromatogram() == null) {
			throw new ChromatogramSelectionException("The chromatogram must not be null.");
		}
	}

	/**
	 * Validates that the classifier settings are not null.
	 * 
	 * @param chromatogramClassifierSettings
	 * @throws ClassifierSettingsException
	 */
	private void validateClassifierSettings(IChromatogramClassifierSettings chromatogramClassifierSettings) throws ClassifierSettingsException {

		if(chromatogramClassifierSettings == null) {
			throw new ClassifierSettingsException("The classifier settings must not be null.");
		}
	}
	// --------------------------------------------private methods
}
