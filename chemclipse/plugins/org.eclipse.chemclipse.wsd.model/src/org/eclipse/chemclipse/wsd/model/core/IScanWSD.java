/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add total signal except excluded
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedSingleWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;

public interface IScanWSD extends IScan {

	IScanSignalWSD getScanSignal(int scan);

	/**
	 * method return signal on exact wavelength
	 * 
	 * @param wavelength
	 * @return signal scan
	 */
	Optional<IScanSignalWSD> getScanSignal(float wavelength);

	void deleteScanSignals();

	void addScanSignal(IScanSignalWSD scanSignalWSD);

	void removeScanSignal(IScanSignalWSD scanSignalWSD);

	int getNumberOfScanSignals();

	List<IScanSignalWSD> getScanSignals();

	void removeScanSignal(int scan);

	void removeScanSignals(Set<Integer> wavelengths);

	IExtractedWavelengthSignal getExtractedWavelengthSignal();

	IExtractedWavelengthSignal getExtractedWavelengthSignal(float startWavelength, float stopWavelength);

	Optional<IExtractedSingleWavelengthSignal> getExtractedSingleWavelengthSignal(float wavelength);

	boolean hasScanSignals();

	IWavelengthBounds getWavelengthBounds();

	/**
	 * 
	 * @return total intensity count (TIC) excepted excluded wavelengths
	 */
	float getTotalSignal(IMarkedWavelengths excludedWavelenths);
}
