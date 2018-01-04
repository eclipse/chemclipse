/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class SWTUtil {

	public static void setDialogSize(Dialog dialog, int width, int height) {

		Point computedSize = dialog.getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		width = Math.max(computedSize.x, width);
		height = Math.max(computedSize.y, height);
		dialog.getShell().setSize(width, height);
	}

	public void asyncRun(Runnable r) {

		SafeRunnable sr = new SafeRunnable() {

			@Override
			public void run() throws Exception {

				r.run();
			}
		};
	}

	/**
	 * Make error dialog which prints stack trace of the throwable
	 *
	 * @param title
	 *            title of the dialog
	 * @param msg
	 *            message of the dialog
	 * @param t
	 *            throwable diaog prints out
	 * @param shell
	 *            parent shell of this dialog
	 * @author Ho Namkoong {@literal <ho.namkoong@samsung.com>} (S-Core)
	 */
	public static void errorDialogWithStackTrace(String title, String msg, Throwable t, String pluginId, Shell shell) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		final String trace = sw.toString();
		List<Status> childStatuses = new ArrayList<>();
		for(String line : trace.split("\n")) {
			childStatuses.add(new Status(IStatus.ERROR, pluginId, line));
		}
		MultiStatus ms = new MultiStatus(pluginId, IStatus.ERROR, childStatuses.toArray(new Status[]{}), t.getLocalizedMessage(), t);
		ErrorDialog.openError(shell, "Error Dialog", msg, ms);
	}
}
