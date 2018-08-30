/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettingsMSD;
import org.eclipse.chemclipse.model.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public abstract class AbstractMassSpectrumIdentifier implements IMassSpectrumIdentifier {

	/**
	 * Validates that the peak is not null.<br/>
	 * If yes, an exception will be thrown.
	 * 
	 * @param peak
	 * @throws ValueMustNotBeNullException
	 */
	public void validateMassSpectrum(IScanMSD massSpectrum) throws ValueMustNotBeNullException {

		if(massSpectrum == null) {
			throw new ValueMustNotBeNullException("The mass spectrum must not be null.");
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
