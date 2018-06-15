/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.ui.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.ui.internal.handlers.DetectorByFileRunnable;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DetectorByFileHandler implements EventHandler {

	private static final Logger logger = Logger.getLogger(DetectorByFileHandler.class);
	private static IChromatogramSelectionMSD chromatogramSelection;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		/*
		 * Try to select and show the perspective and view.
		 */
		if(chromatogramSelection != null) {
			Shell shell = Display.getCurrent().getActiveShell();
			FileDialog fileDialog = new FileDialog(shell, SWT.READ_ONLY);
			fileDialog.setText("Import ELU file");
			fileDialog.setFileName("");
			fileDialog.setFilterExtensions(new String[]{"*.ELU"});
			fileDialog.setFilterNames(new String[]{"AMDIS ELU File"});
			String pathname = fileDialog.open();
			if(pathname != null) {
				/*
				 * Get the ELU file.
				 */
				File file = new File(pathname);
				DetectorByFileRunnable runnable = new DetectorByFileRunnable(chromatogramSelection, file);
				ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
				try {
					/*
					 * Use true, true ... instead of false, true ... if the progress bar
					 * should be shown in action.
					 */
					monitor.run(true, true, runnable);
				} catch(InvocationTargetException e) {
					logger.warn(e);
				} catch(InterruptedException e) {
					logger.warn(e);
				}
				StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: ELU Peaks imported");
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
