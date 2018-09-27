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

import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

/**
 * @deprecated Use {@link IExtractedSingleWavelengthSignalExtractor} instead
 *
 * @see {@link IExtractedSingleWavelengthSignals}
 * @see {@link IExtractedSingleWavelengthSignal}
 * 
 */
@Deprecated
public interface IExtractedWavelengthSignalExtractor {

	IExtractedWavelengthSignals getExtractedWavelengthSignals(float startWavelength, float stopWavelength);

	IExtractedWavelengthSignals getExtractedWavelengthSignals();

	IExtractedWavelengthSignals getExtractedWavelengthSignals(IChromatogramSelectionWSD chromatogramSelection);

	IExtractedWavelengthSignals getExtractedWavelengthSignals(int startScan, int stopScan);
}
