/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.NoQuantitationTableAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.events.IChemClipseQuantitationEvents;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.preferences.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.wizards.AddPeakToQuantitationTableWizard;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class AddSelectedPeakToQuantitationTableHandler implements EventHandler {

	private static final Logger logger = Logger.getLogger(AddSelectedPeakToQuantitationTableHandler.class);
	private static IChromatogramSelectionMSD chromatogramSelection;
	@Inject
	IEventBroker eventBroker;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		/*
		 * Try to select and show the perspective and view.
		 */
		PerspectiveSwitchHandler.focusPerspectiveAndView(IPerspectiveAndViewIds.PERSPECTIVE_ID, IPerspectiveAndViewIds.QUANTITATION_COMPUNDS_VIEW);
		//
		if(chromatogramSelection != null) {
			IChromatogramPeakMSD chromatogramPeakMSD = chromatogramSelection.getSelectedPeak();
			if(chromatogramPeakMSD != null) {
				/*
				 * Open a wizard to get relevant information.
				 */
				try {
					IQuantDatabase database = QuantDatabases.getQuantDatabase();
					AddPeakToQuantitationTableWizard wizard = new AddPeakToQuantitationTableWizard(database, chromatogramPeakMSD);
					WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
					if(dialog.open() == Dialog.OK) {
						/*
						 * Send a message to inform e.g. the QuantitationCompoundsUI.
						 */
						if(eventBroker != null) {
							eventBroker.send(IChemClipseQuantitationEvents.TOPIC_QUANTITATION_TABLE_UPDATE, PreferenceSupplier.getSelectedQuantitationTable());
						}
						//
						StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: The peak has been successfully added to the quantitation table.");
					}
				} catch(NoQuantitationTableAvailableException e) {
					MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Quantitation", "Please select a quantitation table previously.");
					StatusLineLogger.setInfo(InfoType.MESSAGE, "There is no quantitation table available.");
					logger.warn(e);
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
