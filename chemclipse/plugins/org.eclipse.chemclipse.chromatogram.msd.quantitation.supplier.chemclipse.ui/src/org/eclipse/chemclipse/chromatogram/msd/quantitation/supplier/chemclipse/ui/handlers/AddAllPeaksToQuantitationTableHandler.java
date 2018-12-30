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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.wizards.AddAllPeaksWizard;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class AddAllPeaksToQuantitationTableHandler implements EventHandler {

	private static IChromatogramSelectionMSD chromatogramSelection;
	@Inject
	IEventBroker eventBroker;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		if(chromatogramSelection != null) {
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
			if(chromatogram != null) {
				List<IChromatogramPeakMSD> peaks = chromatogram.getPeaks(chromatogramSelection);
				if(peaks != null) {
					AddAllPeaksWizard wizard = new AddAllPeaksWizard(peaks);
					WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
					if(dialog.open() == Dialog.OK) {
						StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: The peaks have been added to the quantitation table.");
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
}
