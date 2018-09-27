/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.exceptions.NoExtractedWavelengthSignalStoredException;

/**
 * @deprecated Use {@link IExtractedSingleWavelengthSignals} instead
 *
 * @see {@link IExtractedSingleWavelengthSignal}
 * @see {@link IExtractedSingleWavelengthSignalExtractor}
 * 
 */
@Deprecated
public interface IExtractedWavelengthSignals {

	IChromatogramWSD getChromatogram();

	void add(IExtractedWavelengthSignal extractedWavelengthSignal);

	void add(int wavelength, float abundance, int retentionTime, boolean removePreviousAbundance);

	IExtractedWavelengthSignal getExtractedWavelengthSignal(int scan) throws NoExtractedWavelengthSignalStoredException;

	List<IExtractedWavelengthSignal> getExtractedWavelengthSignals();

	ITotalScanSignals getTotalWavelengthSignals(int wavelength);

	ITotalScanSignals getTotalWavelengthSignals();

	ITotalScanSignals getTotalWavelengthSignals(int wavelength, IScanRange scanRange);

	ITotalScanSignals getTotalWavelengthSignals(IScanRange scanRange);

	IScanWSD getScan(int scan);

	int getStartWavelength();

	int getStopWavelength();

	int size();

	int getStartScan();

	int getStopScan();

	IExtractedWavelengthSignals makeDeepCopyWithoutSignals();

	Set<Integer> getUsedWavelenghts();
}
