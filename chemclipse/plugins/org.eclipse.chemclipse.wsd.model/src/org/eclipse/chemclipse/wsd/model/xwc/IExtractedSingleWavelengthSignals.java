/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;

public interface IExtractedSingleWavelengthSignals extends ITotalScanSignals {

	@Override
	IChromatogramWSD getChromatogram();

	/**
	 * Adds an {@link IExtractedSingleWavelengthSignal} instance at the end of the stored
	 * 
	 * @param extractedWavelengthSignal
	 */
	void add(IExtractedSingleWavelengthSignal extractedWavelengthSignal);

	@Override
	IExtractedSingleWavelengthSignal getTotalScanSignal(int scan);

	@Override
	IExtractedSingleWavelengthSignals makeDeepCopy();

	float getWavelength();
}
