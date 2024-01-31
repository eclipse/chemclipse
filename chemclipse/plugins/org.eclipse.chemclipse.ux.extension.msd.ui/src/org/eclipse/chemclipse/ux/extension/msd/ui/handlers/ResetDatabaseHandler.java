/*******************************************************************************
 * Copyright (c) 2012, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.handlers;

import jakarta.inject.Named;

import org.eclipse.chemclipse.chromatogram.msd.identifier.support.DatabasesCache;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ResetDatabaseHandler {

	@Execute
	public void execute(UISynchronize uiSynchronize, final @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
		messageBox.setText("Reset Database Cache (MSD)");
		messageBox.setMessage("Would you like to reset the database cache? It will be reloaded on demand.");
		int decision = messageBox.open();
		if(SWT.YES == decision) {
			DatabasesCache.resetCache();
		}
	}
}
