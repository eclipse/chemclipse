/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.chromatogram;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettingsMSD;
import org.eclipse.chemclipse.model.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public abstract class AbstractChromatogramIdentifier implements IChromatogramIdentifier {

	/**
	 * Tests if the chromatogram selection is a valid instance.
	 * 
	 * @param chromatogramSelection
	 * @throws ValueMustNotBeNullException
	 */
	public void validateChromatogramSelection(IChromatogramSelectionMSD chromatogramSelection) throws ValueMustNotBeNullException {

		if(chromatogramSelection == null) {
			throw new ValueMustNotBeNullException("The chromatogram selection must not be null.");
		}
		if(chromatogramSelection.getChromatogram() == null) {
			throw new ValueMustNotBeNullException("The chromatogram must not be null.");
		}
	}

	/**
	 * Throws an exception if the settings are null.
	 * 
	 * @param identifierSettings
	 * @throws ValueMustNotBeNullException
	 */
	public void validateSettings(IIdentifierSettingsMSD identifierSettings) throws ValueMustNotBeNullException {

		if(identifierSettings == null) {
			throw new ValueMustNotBeNullException("The identifier settings must not be null.");
		}
	}
}
