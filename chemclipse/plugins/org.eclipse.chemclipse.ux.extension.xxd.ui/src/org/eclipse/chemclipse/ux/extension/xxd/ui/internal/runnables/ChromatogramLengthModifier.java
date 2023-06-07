/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramLengthModifier implements IRunnableWithProgress {

	private IChromatogramSelection<?, ?> chromatogramSelection;
	private int scanDelay;
	private int chromatogramLength;

	public ChromatogramLengthModifier(IChromatogramSelection<?, ?> chromatogramSelection, int scanDelay, int chromatogramLength) {

		this.chromatogramSelection = chromatogramSelection;
		this.scanDelay = scanDelay;
		this.chromatogramLength = chromatogramLength;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(ExtensionMessages.chromatogramLengthModified, IProgressMonitor.UNKNOWN);
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			int scanRange = chromatogram.getNumberOfScans() - 1;
			if(scanRange > 0) {
				/*
				 * Remove the peaks, etc.
				 */
				chromatogramSelection.setSelectedPeak(null);
				chromatogramSelection.setSelectedScan(null);
				chromatogramSelection.setSelectedIdentifiedScan(null);
				//
				chromatogram.getBaselineModel().removeBaseline();
				chromatogram.removeAllBackgroundIntegrationEntries();
				chromatogram.removeAllChromatogramIntegrationEntries();
				chromatogram.removeAllMeasurementResults();
				chromatogram.removeAllPeaks();
				chromatogram.getTargets().clear();
				/*
				 * Calculate the new range
				 */
				float retentionTimeRange = chromatogramLength - scanDelay;
				int scanInterval = Math.round(retentionTimeRange / scanRange);
				//
				chromatogram.setScanDelay(scanDelay);
				chromatogram.setScanInterval(scanInterval);
				chromatogram.recalculateRetentionTimes();
				/*
				 * Modify the chromatogram selection.
				 */
				int startRetentionTime = scanDelay;
				if(chromatogramSelection.getStartRetentionTime() < startRetentionTime) {
					chromatogramSelection.setStartRetentionTime(startRetentionTime);
				}
				//
				int stopRetentionTime = chromatogram.getStopRetentionTime();
				if(chromatogramSelection.getStopRetentionTime() > stopRetentionTime) {
					chromatogramSelection.setStopRetentionTime(stopRetentionTime);
				}
			}
		} finally {
			monitor.done();
		}
	}
}
