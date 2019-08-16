/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui;

import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {

		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public boolean preWindowShellClose() {

		if(ModelSupportAddon.saveDirtyParts()) {
			Display display = Display.getCurrent();
			Shell shell = display.getActiveShell();
			if(shell == null) {
				// see Bug 534346, try to find an alternative shell
				Shell[] shells = display.getShells();
				for(Shell other : shells) {
					if(other.isDisposed() || !other.isVisible()) {
						continue;
					}
					shell = other;
				}
			}
			if(shell == null) {
				// we can't ask the user then
				return true;
			}
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			messageBox.setText("Close the application");
			messageBox.setMessage("Do you want to close the application now?");
			if(messageBox.open() == SWT.YES) {
				return super.preWindowShellClose();
			} else {
				return false;
			}
		}
		return false;
	}
}