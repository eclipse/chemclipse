/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.identifier.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.csd.identifier.ui.runnables.PeakIdentifierRunnable;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DeleteUnidentifiedPeaksHandler implements EventHandler {

	private static final Logger logger = Logger.getLogger(DeleteUnidentifiedPeaksHandler.class);
	private static IChromatogramSelectionCSD chromatogramSelection;

	@Execute
	public void execute(UISynchronize uiSynchronize, final @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		if(chromatogramSelection != null) {
			/*
			 * Remove all detected peaks.
			 */
			IChromatogramCSD chromatogram = chromatogramSelection.getChromatogramCSD();
			if(chromatogram != null) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
				messageBox.setText("Delete All Unidentified Peaks");
				messageBox.setMessage("Do you really want to delete all unidentified peaks?");
				int decision = messageBox.open();
				if(SWT.YES == decision) {
					/*
					 * Delete all unidentified peaks.
					 */
					StatusLineLogger.setInfo(InfoType.MESSAGE, "Start delete peaks without identifications.");
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
					try {
						/*
						 * Use true, true ... instead of false, true ... if the progress bar
						 * should be shown in action.
						 */
						monitor.run(true, true, new PeakIdentifierRunnable(chromatogramSelection));
					} catch(InvocationTargetException e) {
						logger.warn(e);
					} catch(InterruptedException e) {
						logger.warn(e);
					}
					StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: Unidentified Peak(s) deleted");
				}
			}
		}
	}

	@Override
	public void handleEvent(Event event) {

		if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionCSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		} else {
			chromatogramSelection = null;
		}
	}
}
