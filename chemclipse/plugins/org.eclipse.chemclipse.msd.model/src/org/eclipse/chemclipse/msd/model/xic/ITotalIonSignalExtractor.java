/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;

public interface ITotalIonSignalExtractor extends ITotalScanSignalExtractor {

	/**
	 * Takes an {@link IChromatogramSelectionMSD} object as an argument and returns
	 * a {@link ITotalScanSignals} object in the range of the given start and
	 * stop retention times by the selection.<br/>
	 * If the selection is null, an empty {@link ITotalScanSignals} object will
	 * be returned.
	 * 
	 * @param chromatogramSelection
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalIonSignals(IChromatogramSelectionMSD chromatogramSelection);

	/**
	 * Returns the total ion signals of the parent chromatogram.<br/>
	 * The start and stop scan are included.<br/>
	 * All ions which are stored in excludedIons will not be
	 * considered int the total ion signal of each scan.<br/>
	 * If the exludedIons are null, all mass fragments will be taken
	 * into result.<br/>
	 * It can be used to draw the TIC (total ion chromatogram).
	 * 
	 * @param excludedIons
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalIonSignals(IMarkedIons excludedIons);

	/**
	 * Returns the total ion signals of the parent chromatogram between the
	 * selected start and stop scan.<br/>
	 * The start and stop scan are included.<br/>
	 * All ions which are stored in excludedIons will not be
	 * considered int the total ion signal of each scan.<br/>
	 * If the exludedIons are null, all mass fragments will be taken
	 * into result.<br/>
	 * It can be used to draw the TIC (total ion chromatogram).
	 * 
	 * @param startScan
	 * @param stopScan
	 * @param excludedIons
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalIonSignals(int startScan, int stopScan, IMarkedIons excludedIons);

	/**
	 * Takes an {@link IChromatogramSelectionMSD} object as an argument and returns
	 * a {@link ITotalScanSignals} object in the range of the given start and
	 * stop retention times by the selection.<br/>
	 * All ions stored in excludedIons will not be
	 * considered.<br/>
	 * If the exludedIons are null, all ions will be taken
	 * into result.<br/>
	 * If the selection is null, an empty {@link ITotalScanSignals} object will
	 * be returned.
	 * 
	 * @param chromatogramSelection
	 * @param excludedIons
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalIonSignals(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons excludedIons);
}
