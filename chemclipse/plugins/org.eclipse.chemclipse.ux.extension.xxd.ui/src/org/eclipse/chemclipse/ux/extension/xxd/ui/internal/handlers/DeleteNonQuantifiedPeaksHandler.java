/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@SuppressWarnings("rawtypes")
public class DeleteNonQuantifiedPeaksHandler implements EventHandler {

	private static IChromatogramSelection chromatogramSelection;
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();

	@Execute
	public void execute(UISynchronize uiSynchronize, final @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		if(chromatogramSelection != null) {
			/*
			 * Remove all detected peaks.
			 */
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
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
					//
					chromatogramSelection.update(true);
				}
			}
		}
	}

	@Override
	public void handleEvent(Event event) {

		if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelection)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		} else if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelection)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		} else if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelection)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		}
	}

	private void deleteNonQuantifiedPeaks(IChromatogram chromatogram) {

		List<? extends IPeak> peaks = chromatogramDataSupport.getPeaks(chromatogram);
		List<IPeak> peaksToDelete = new ArrayList<IPeak>();
		/*
		 * Parse each peak in the chromatogram.
		 */
		for(IPeak peak : peaks) {
			/*
			 * If the peak has no identification record,
			 * mark the peak for the deletion queue.
			 */
			if(peak.getQuantitationEntries().size() == 0) {
				peaksToDelete.add(peak);
			}
		}
		/*
		 * Delete the marked peaks
		 */
		if(chromatogram instanceof IChromatogramMSD) {
			IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
			for(IPeak peak : peaksToDelete) {
				if(peak instanceof IChromatogramPeakMSD) {
					chromatogramMSD.removePeak((IChromatogramPeakMSD)peak);
				}
			}
		} else if(chromatogram instanceof IChromatogramCSD) {
			IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
			for(IPeak peak : peaksToDelete) {
				if(peak instanceof IChromatogramPeakCSD) {
					chromatogramCSD.removePeak((IChromatogramPeakCSD)peak);
				}
			}
		} else if(chromatogram instanceof IChromatogramWSD) {
			// IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
			// for(IPeak peak : peaksToDelete) {
			// if(peak instanceof IChromatogramPeakWSD) {
			// chromatogramWSD.removePeak((IChromatogramPeakWSD)peak);
			// }
			// }
		}
	}
}
