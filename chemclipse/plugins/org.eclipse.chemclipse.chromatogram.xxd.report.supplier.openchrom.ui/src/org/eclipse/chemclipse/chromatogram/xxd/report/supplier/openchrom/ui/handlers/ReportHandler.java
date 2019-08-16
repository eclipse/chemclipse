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
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.ui.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.ui.internal.handlers.ReportRunnable;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
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

public class ReportHandler implements EventHandler {

	private static final Logger logger = Logger.getLogger(ReportHandler.class);
	private static IChromatogramSelection chromatogramSelection;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		/*
		 * Get the actual cursor, create a new wait cursor and show the wait
		 * cursor.<br/> Show the origin cursor when finished.<br/> Use the
		 * Display.asyncExec instances to show the messages properly.<br/> I
		 * really don't like the GUI stuff, maybe there is a smarter way to
		 * inform the user.<br/> So, i thought also on Eclipse "jobs", but there
		 * will be problems, when the operation method updates the
		 * chromatogramSelection.<br/> A progress bar would be also applicable,
		 * but the IProgressMonitorDialog (monitor.beginTask(TASK_NAME,
		 * IProgressMonitor.UNKNOWN) didn't showed a progress under linux.
		 */
		/*
		 * Run the report
		 */
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				/*
				 * Get the export file.
				 */
				String defaultFileName = chromatogram.getName() + ".txt";
				File file = getFileFromFileDialog(defaultFileName);
				if(file != null) {
					final Display display = Display.getCurrent();
					ReportRunnable runnable = new ReportRunnable(file, chromatogram);
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
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
					StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: The report has been successfully created.");
				}
			}
		}
	}

	private File getFileFromFileDialog(String defaultFileName) {

		Shell shell = Display.getCurrent().getActiveShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setText("OpenChrom Report");
		fileDialog.setFileName(defaultFileName);
		fileDialog.setFilterExtensions(new String[]{"*.txt"});
		fileDialog.setFilterNames(new String[]{"OpenChrom Report (*.txt)"});
		String fileName = fileDialog.open();
		if(fileName == null || fileName.equals("")) {
			return null;
		} else {
			return new File(fileName);
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
