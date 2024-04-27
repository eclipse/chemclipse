/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {

		super(configurer);
		configurer.addEditorAreaTransfer(FileTransfer.getInstance());
		configurer.configureEditorAreaDropListener(new EditorAreaDropAdapter(configurer.getWindow()));
	}

	@Override
	public void preWindowOpen() {

		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowProgressIndicator(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {

		return new ApplicationActionBarAdvisor(configurer);
	}
}
