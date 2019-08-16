/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.dialogs;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DialogSupport {

	public static void showGUIDialog() {

		if(PreferenceSupplier.isShowGUIDialog()) {
			Shell shell = Display.getCurrent().getActiveShell();
			String title = "NIST GUI";
			String message = "The NIST-DB will be opened in GUI mode. Please use the NIST Menu > File > Open and chose file for spectra/structures import (NIST Test *.MSP). Select the file: '" + PreferenceSupplier.MSP_EXPORT_FILE_NAME + "'. It is stored in the NIST-MSSEARCH folder. Note: The results will be not reimported in OpenChrom. Please close the NIST-DB after use.";
			OpenGUIDialog dialog = new OpenGUIDialog(shell, title, message);
			dialog.open();
		}
	}
}
