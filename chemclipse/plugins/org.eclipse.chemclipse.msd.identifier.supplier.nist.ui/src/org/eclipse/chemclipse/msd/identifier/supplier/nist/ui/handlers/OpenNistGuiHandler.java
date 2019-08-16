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
package org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.runnables.OpenNistGuiRunnable;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class OpenNistGuiHandler {

	private static final Logger logger = Logger.getLogger(OpenNistGuiHandler.class);

	@Execute
	public void execute() {

		final Display display = Display.getCurrent();
		StatusLineLogger.setInfo(InfoType.MESSAGE, "Open NIST-DB in GUI mode.");
		/*
		 * Do the operation.<br/> Open a progress monitor dialog.
		 */
		IRunnableWithProgress runnable = new OpenNistGuiRunnable();
		runOperation(display, runnable);
		StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: Open NIST-DB in GUI mode");
	}

	private void runOperation(Display display, IRunnableWithProgress runnable) {

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
	}
}
