/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.signals;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public interface ITotalScanSignalExtractor {

	/**
	 * Returns the total ion signals of the parent chromatogram.<br/>
	 * It can be used to draw the TIC (total ion chromatogram).
	 * Only positive values will be returned.
	 * 
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalScanSignals();

	/**
	 * Returns the total ion signals of the parent chromatogram.<br/>
	 * It can be used to draw the TIC (total ion chromatogram).
	 * Set validatePositive to false if also negative values are allowed.
	 * 
	 * @param validatePositive
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalScanSignals(boolean validatePositive);

	/**
	 * Returns the total ion signals of the parent chromatogram between the
	 * selected start and stop scan.<br/>
	 * The start and stop scan are included.<br/>
	 * It can be used to draw the TIC (total ion chromatogram).
	 * Only positive values will be returned.
	 * 
	 * @param startScan
	 * @param stopScan
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalScanSignals(int startScan, int stopScan);

	/**
	 * Returns the total ion signals of the parent chromatogram between the
	 * selected start and stop scan.<br/>
	 * The start and stop scan are included.<br/>
	 * It can be used to draw the TIC (total ion chromatogram).
	 * Set validatePositive to false if also negative values are allowed.
	 * 
	 * @param startScan
	 * @param stopScan
	 * @param validatePositive
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalScanSignals(int startScan, int stopScan, boolean validatePositive);

	ITotalScanSignals getTotalScanSignals(int startScan, int stopScan, boolean validatePositive, boolean condenseCycleNumberScans);

	/**
	 * Returns the total scan signals given by the selection.
	 * 
	 * @param chromatogramSelection
	 * @return {@link ITotalScanSignal}
	 */
	
	ITotalScanSignals getTotalScanSignals(IChromatogramSelection<?, ?>chromatogramSelection);

	
	ITotalScanSignals getTotalScanSignals(IChromatogram<?> chromatogram, boolean validatePositive, boolean condenseCycleNumberScans);

	/**
	 * Returns the total scan signals given by the selection.
	 * Set validatePositive to false if also negative values are allowed.
	 * 
	 * @param chromatogramSelection
	 * @return {@link ITotalScanSignal}
	 */
	
	ITotalScanSignals getTotalScanSignals(IChromatogramSelection<?, ?>chromatogramSelection, boolean validatePositive);

	
	ITotalScanSignals getTotalScanSignals(IChromatogramSelection<?, ?>chromatogramSelection, boolean validatePositive, boolean condenseCycleNumberScans);
}
