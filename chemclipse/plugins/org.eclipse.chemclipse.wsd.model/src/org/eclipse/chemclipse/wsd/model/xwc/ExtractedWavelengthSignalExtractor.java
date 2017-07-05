/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

public class ExtractedWavelengthSignalExtractor implements IExtractedWavelengthSignalExtractor {

	@Override
	public IExtractedWavelengthSignals getExtractedWavelengthSignals(float startWavelength, float stopWavelength) {

		return null;
	}

	@Override
	public IExtractedWavelengthSignals getExtractedWavelengthSignals() {

		return null;
	}

	@Override
	public IExtractedWavelengthSignals getExtractedWavelengthSignals(IChromatogramSelectionWSD chromatogramSelection) {

		return null;
	}

	@Override
	public IExtractedWavelengthSignals getExtractedWavelengthSignals(int startScan, int stopScan) {

		return null;
	}
}
