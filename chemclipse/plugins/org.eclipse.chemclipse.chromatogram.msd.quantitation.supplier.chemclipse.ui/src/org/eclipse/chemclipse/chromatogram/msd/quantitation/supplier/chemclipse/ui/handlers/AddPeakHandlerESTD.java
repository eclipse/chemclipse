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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.handlers;

import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.io.DatabaseSupport;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.wizards.AddPeakWizardESTD;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class AddPeakHandlerESTD implements EventHandler {

	private static IChromatogramSelectionMSD chromatogramSelection;

	@Execute
	public void execute(Shell shell, @Named(IServiceConstants.ACTIVE_PART) MPart part) {

		if(chromatogramSelection != null) {
			IChromatogramPeakMSD chromatogramPeakMSD = chromatogramSelection.getSelectedPeak();
			if(chromatogramPeakMSD != null) {
				/*
				 * Open a wizard to get relevant information.
				 */
				DatabaseSupport databaseSupport = new DatabaseSupport();
				IQuantitationDatabase quantitationDatabase = databaseSupport.load();
				if(quantitationDatabase != null && quantitationDatabase.size() >= 0) {
					AddPeakWizardESTD wizard = new AddPeakWizardESTD(quantitationDatabase, chromatogramPeakMSD);
					WizardDialog dialog = new WizardDialog(shell, wizard);
					if(dialog.open() == Dialog.OK) {
						StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: The peak has been successfully added to the quantitation table.");
					}
					/*
					 * Save
					 */
					databaseSupport.save(quantitationDatabase);
				} else {
					MessageDialog.openWarning(shell, "Quantitation", "Please select a quantitation table previously.");
					StatusLineLogger.setInfo(InfoType.MESSAGE, "There is no quantitation table available.");
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
}
