/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.IMouseSupport;
import org.eclipse.swtchart.extensions.events.AbstractHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;

public class PeakSelectionHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

	private ExtendedChromatogramUI extendedChromatogramUI;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public PeakSelectionHandler(ExtendedChromatogramUI extendedChromatogramUI) {

		this.extendedChromatogramUI = extendedChromatogramUI;
	}

	@Override
	public int getEvent() {

		return IMouseSupport.EVENT_MOUSE_DOUBLE_CLICK;
	}

	@Override
	public int getButton() {

		return IMouseSupport.MOUSE_BUTTON_LEFT;
	}

	@Override
	public int getStateMask() {

		return SWT.ALT;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void handleEvent(BaseChart baseChart, Event event) {

		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			int retentionTime = (int)baseChart.getSelectedPrimaryAxisValue(event.x, IExtendedChart.X_AXIS);
			int retentionTimeDelta = preferenceStore.getInt(PreferenceConstants.P_DELTA_MILLISECONDS_PEAK_SELECTION);
			int startRetentionTime = retentionTime - retentionTimeDelta;
			int stopRetentiontime = retentionTime + retentionTimeDelta;
			List<IPeak> peaks = chromatogram.getPeaks(startRetentionTime, stopRetentiontime);
			if(peaks != null && peaks.size() > 0) {
				/*
				 * Fire an update.
				 */
				IPeak peak = selectNearestPeak(peaks, retentionTime);
				if(peak != null) {
					//
					chromatogramSelection.setSelectedPeak(peak);
					extendedChromatogramUI.updateSelectedPeak();
					//
					boolean moveRetentionTimeOnPeakSelection = preferenceStore.getBoolean(PreferenceConstants.P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION);
					if(moveRetentionTimeOnPeakSelection) {
						ChromatogramDataSupport.adjustChromatogramSelection(peak, chromatogramSelection);
					}
					//
					extendedChromatogramUI.updateSelection();
					//
					UpdateNotifierUI.update(event.display, peak);
					IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets());
					UpdateNotifierUI.update(event.display, identificationTarget);
				}
			}
		}
	}

	private IPeak selectNearestPeak(List<IPeak> peaks, int retentionTime) {

		IPeak nearestPeak = null;
		for(IPeak peak : peaks) {
			if(nearestPeak == null) {
				nearestPeak = peak;
			} else {
				int deltaNearest = Math.abs(retentionTime - nearestPeak.getPeakModel().getRetentionTimeAtPeakMaximum());
				int deltaCurrent = Math.abs(retentionTime - peak.getPeakModel().getRetentionTimeAtPeakMaximum());
				if(deltaCurrent <= deltaNearest) {
					nearestPeak = peak;
				}
			}
		}
		return nearestPeak;
	}
}
