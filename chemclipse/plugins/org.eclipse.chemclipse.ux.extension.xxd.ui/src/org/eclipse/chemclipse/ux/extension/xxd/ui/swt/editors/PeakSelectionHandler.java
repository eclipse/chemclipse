/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.events.AbstractHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;

public class PeakSelectionHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

	private ExtendedChromatogramUI extendedChromatogramUI;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();

	public PeakSelectionHandler(ExtendedChromatogramUI extendedChromatogramUI) {
		this.extendedChromatogramUI = extendedChromatogramUI;
	}

	@Override
	public int getEvent() {

		return BaseChart.EVENT_MOUSE_DOUBLE_CLICK;
	}

	@Override
	public int getButton() {

		return BaseChart.BUTTON_LEFT;
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
				IPeak peak = peaks.get(0);
				chromatogramSelection.setSelectedPeak(peak);
				boolean moveRetentionTimeOnPeakSelection = preferenceStore.getBoolean(PreferenceConstants.P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION);
				if(moveRetentionTimeOnPeakSelection) {
					chromatogramDataSupport.adjustChromatogramSelection(peak, chromatogramSelection);
				}
				//
				extendedChromatogramUI.updateSelection();
				IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
				eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, peak);
			}
		}
	}
}
