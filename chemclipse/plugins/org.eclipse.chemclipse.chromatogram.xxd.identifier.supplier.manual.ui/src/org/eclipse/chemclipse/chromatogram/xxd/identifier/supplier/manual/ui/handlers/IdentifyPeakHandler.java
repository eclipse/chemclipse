/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.manual.ui.handlers;

import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.manual.ui.internal.wizards.IdentificationWizard;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.notifier.ChromatogramSelectionCSDUpdateNotifier;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class IdentifyPeakHandler implements EventHandler {

	private static IChromatogramSelection chromatogramSelection;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		if(chromatogramSelection != null) {
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				final IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
				IChromatogramPeakMSD peakMSD = chromatogramSelectionMSD.getSelectedPeak();
				if(peakMSD != null) {
					IdentificationWizard wizard = new IdentificationWizard(peakMSD);
					WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
					if(dialog.open() == Dialog.OK) {
						/*
						 * Identification result has been added.
						 */
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {

								ChromatogramSelectionMSDUpdateNotifier.fireUpdateChange(chromatogramSelectionMSD, true);
							}
						});
						StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: Peak identified manually");
					}
				}
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				final IChromatogramSelectionCSD chromatogramSelectionFID = (IChromatogramSelectionCSD)chromatogramSelection;
				IChromatogramPeakCSD peakFID = chromatogramSelectionFID.getSelectedPeak();
				if(peakFID != null) {
					IdentificationWizard wizard = new IdentificationWizard(peakFID);
					WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
					if(dialog.open() == Dialog.OK) {
						/*
						 * Identification result has been added.
						 */
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {

								ChromatogramSelectionCSDUpdateNotifier.fireUpdateChange(chromatogramSelectionFID, true);
							}
						});
						StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: Peak identified manually");
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
