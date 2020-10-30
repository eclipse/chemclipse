/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DeleteAllPeakIntegrationsHandler implements EventHandler {

	private static IChromatogramSelection<?, ?> chromatogramSelection;

	@SuppressWarnings("rawtypes")
	@Execute
	public void execute(UISynchronize uiSynchronize, final @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		if(chromatogramSelection != null) {
			/*
			 * Remove all detected peaks.
			 */
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
				messageBox.setText("Delete All Peak Integrations");
				messageBox.setMessage("Do you really want to delete all peak integration values?");
				int decision = messageBox.open();
				if(SWT.YES == decision) {
					/*
					 * Delete all peak integrations.
					 */
					if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
						/*
						 * MSD
						 */
						ChromatogramSelectionMSD chromatogramSelectionMSD = (ChromatogramSelectionMSD)chromatogramSelection;
						IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
						for(IChromatogramPeakMSD peak : chromatogramMSD.getPeaks()) {
							peak.removeAllIntegrationEntries();
						}
						chromatogramSelectionMSD.update(true);
					} else if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
						/*
						 * FID
						 */
						ChromatogramSelectionCSD chromatogramSelectionFID = (ChromatogramSelectionCSD)chromatogramSelection;
						IChromatogramCSD chromatogramFID = chromatogramSelectionFID.getChromatogramCSD();
						for(IChromatogramPeakCSD peak : chromatogramFID.getPeaks()) {
							peak.removeAllIntegrationEntries();
						}
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
