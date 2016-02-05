/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class QuitHandler {

	@Execute
	public void execute(IWorkbench workbench, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, EPartService partService) {

		if(MessageDialog.openConfirm(shell, "Confirmation", "Do you want to exit?")) {
			if(partService.getDirtyParts().size() > 0) {
				if(MessageDialog.openConfirm(shell, "Save All", "Some data has not been saved yet. Would you like to save it?")) {
					if(partService.saveAll(true)) {
						workbench.close();
					} else {
						if(MessageDialog.openConfirm(shell, "Exit", "Some parts have not been saved. Exit anyhow?")) {
							workbench.close();
						}
					}
				}
			} else {
				workbench.close();
			}
		}
	}
}
