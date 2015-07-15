/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
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
	public void postWindowCreate() {

		/*
		 * This setting is needed to maximize the application window.
		 * There also a tag in the Application.e4xmi which seems not to be used.
		 * The maximize method seems to be not needed anymore.
		 * Display.getCurrent().getActiveShell().setMaximized(true);
		 */
		super.postWindowCreate();
	}

	@Override
	public boolean preWindowShellClose() {

		MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
		messageBox.setText("Close the application");
		messageBox.setMessage("Do you want to close the application now?");
		if(messageBox.open() == SWT.YES) {
			return super.preWindowShellClose();
		} else {
			return false;
		}
	}
}