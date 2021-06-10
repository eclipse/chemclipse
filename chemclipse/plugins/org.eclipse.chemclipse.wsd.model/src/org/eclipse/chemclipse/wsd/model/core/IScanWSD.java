/*******************************************************************************
 * Copyright (c) 2013, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core;

import java.util.List;
import java.util.Optional;

import org.eclipse.chemclipse.model.core.IScan;
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
	Optional<IScanSignalWSD> getScanSignal(double wavelength);

	void deleteScanSignals();

	void addScanSignal(IScanSignalWSD scanSignalWSD);

	void removeScanSignal(IScanSignalWSD scanSignalWSD);

	int getNumberOfScanSignals();

	List<IScanSignalWSD> getScanSignals();

	void removeScanSignal(int scan);

	IExtractedWavelengthSignal getExtractedWavelengthSignal();

	IExtractedWavelengthSignal getExtractedWavelengthSignal(double startIon, double stopIon);

	Optional<IExtractedSingleWavelengthSignal> getExtractedSingleWavelengthSignal(double wavelength);

	boolean hasScanSignals();

	IWavelengthBounds getWavelengthBounds();
}
