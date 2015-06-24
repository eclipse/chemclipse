/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DeleteNonQuantifiedPeaksHandler implements EventHandler {

	private static IChromatogramSelectionMSD chromatogramSelection;

	@Execute
	public void execute(UISynchronize uiSynchronize, final @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		if(chromatogramSelection != null) {
			/*
			 * Remove all detected peaks.
			 */
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
			if(chromatogram != null) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
				messageBox.setText("Delete Non Quantified Peaks");
				messageBox.setMessage("Do you really want to delete all non quantified peaks?");
				int decision = messageBox.open();
				if(SWT.YES == decision) {
					/*
					 * Delete all non quantified peaks.
					 */
					deleteNonQuantifiedPeaks(chromatogram);
					/*
					 * Try to update if the IChromatogramSelection is an
					 * instance of ChromatogramSelection.
					 */
					if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
						((ChromatogramSelectionMSD)chromatogramSelection).update(true);
					}
				}
			}
		}
	}

	@Override
	public void handleEvent(Event event) {

		if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionMSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		}
	}

	private void deleteNonQuantifiedPeaks(IChromatogramMSD chromatogram) {

		List<IChromatogramPeakMSD> peaks = chromatogram.getPeaks();
		List<IChromatogramPeakMSD> peaksToDelete = new ArrayList<IChromatogramPeakMSD>();
		/*
		 * Parse each peak in the chromatogram.
		 */
		for(IChromatogramPeakMSD chromatogramPeak : peaks) {
			/*
			 * If the peak has no identification record,
			 * mark the peak for the deletion queue.
			 */
			if(chromatogramPeak.getQuantitationEntries().size() == 0) {
				peaksToDelete.add(chromatogramPeak);
			}
		}
		/*
		 * Delete the marked peaks
		 */
		for(IChromatogramPeakMSD peak : peaksToDelete) {
			chromatogram.removePeak(peak);
		}
	}
}
