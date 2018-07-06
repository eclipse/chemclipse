/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.handlers;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

public class InfoSpectraHandler {

	@Execute
	void execute() {

		MessageBox messageBox = new MessageBox(DisplayUtils.getShell(), SWT.ICON_INFORMATION);
		messageBox.setText("Spectra");
		messageBox.setMessage("Add your commands to handle spectra data here.");
		messageBox.open();
	}
}
