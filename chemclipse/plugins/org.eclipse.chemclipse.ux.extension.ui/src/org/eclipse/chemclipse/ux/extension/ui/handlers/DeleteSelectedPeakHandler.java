/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.handlers;

import javax.inject.Named;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DeleteSelectedPeakHandler implements EventHandler {

	private static IChromatogramSelection chromatogramSelection;

	@SuppressWarnings("rawtypes")
	@Execute
	public void execute(UISynchronize uiSynchronize, final @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		if(chromatogramSelection != null) {
			/*
			 * Remove the selected peak.
			 */
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
					/*
					 * MSD
					 */
					ChromatogramSelectionMSD chromatogramSelectionMSD = (ChromatogramSelectionMSD)chromatogramSelection;
					IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
					IChromatogramPeakMSD peak = chromatogramSelectionMSD.getSelectedPeak();
					if(peak != null) {
						chromatogramMSD.removePeak(peak);
						chromatogramSelectionMSD.setSelectedPeak(null);
						chromatogramSelectionMSD.update(true);
					}
				} else if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
					/*
					 * FID
					 */
					ChromatogramSelectionCSD chromatogramSelectionFID = (ChromatogramSelectionCSD)chromatogramSelection;
					IChromatogramCSD chromatogramFID = chromatogramSelectionFID.getChromatogramCSD();
					IChromatogramPeakCSD peak = chromatogramSelectionFID.getSelectedPeak();
					if(peak != null) {
						chromatogramFID.removePeak(peak);
						chromatogramSelectionFID.setSelectedPeak(null);
						chromatogramSelectionFID.update(true);
					}
				}
			}
		}
	}

	@Override
	public void handleEvent(Event event) {

		if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionMSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		} else if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionCSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		} else if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionWSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		} else {
			chromatogramSelection = null;
		}
	}
}
