/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt.dialogs;

import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.IFileDialog;
import org.eclipse.swt.internal.win32.OS;

public class WindowsFileDialog {

	public static void clearInitialDirectoryWorkaround() {

		/*
		 * TODO: This is a workaround for Windows setting the default directory on its own.
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=577190
		 */
		long[] addresses = new long[1];
		if(COM.CoCreateInstance(COM.CLSID_FileOpenDialog, 0, COM.CLSCTX_INPROC_SERVER, COM.IID_IFileOpenDialog, addresses) == OS.S_OK) {
			IFileDialog fileDialog = new IFileDialog(addresses[0]);
			fileDialog.ClearClientData();
			fileDialog.Release();
		}
	}
}