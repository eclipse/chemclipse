/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.processing;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifier;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface ILibraryServiceProcessingInfo extends IProcessingInfo {

	/**
	 * Gets the processed {@link IMassSpectra mass spectra}.
	 * </p>
	 * Typically these spectra are identical to the anonymous mass spectra which
	 * where input to the identification process. If all input spectra are
	 * returned or only the subset of identified spectra is matter of the
	 * {@link IMassSpectrumIdentifier} instance.
	 * 
	 * @return the processed {@link IMassSpectra mass spectra}
	 * @throws TypeCastException
	 * 
	 * @see IMassSpectrumIdentifier
	 * @see AbstractMassSpectrumIdentifier
	 */
	IMassSpectra getMassSpectra() throws TypeCastException;

	/**
	 * Sets the processed {@link IMassSpectra mass spectra}.
	 * 
	 * @param massSpectra
	 *            the processed {@link IMassSpectra mass spectra}
	 */
	void setMassSpectra(IMassSpectra massSpectra);
}
