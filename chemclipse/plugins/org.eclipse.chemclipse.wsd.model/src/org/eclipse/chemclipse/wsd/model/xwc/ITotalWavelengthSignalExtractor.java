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

import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

/**
 * @deprecated Use {@link IExtractedSingleWavelengthSignalExtractor} instead
 *
 * @see {@link ISingleWavelengthSignals}
 * @see {@link ISingleWavelengthSignal}
 * 
 */
@Deprecated
public interface ITotalWavelengthSignalExtractor extends ITotalScanSignalExtractor {

	ITotalScanSignals getTotalWavelengthSignals(IChromatogramSelectionWSD chromatogramSelection);
}
