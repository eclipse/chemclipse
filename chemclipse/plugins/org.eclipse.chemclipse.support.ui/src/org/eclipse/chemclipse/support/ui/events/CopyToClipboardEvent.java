/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.events;

import org.eclipse.chemclipse.support.ui.internal.provider.CopyToClipboardProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Display;

public class CopyToClipboardEvent implements IKeyEventProcessor {

	public static final int KEY_CODE_C = 99;
	private CopyToClipboardProvider copyToClipboardProvider = new CopyToClipboardProvider();

	@Override
	public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

		if(e.stateMask == SWT.CTRL && e.keyCode == KEY_CODE_C) {
			Clipboard clipboard = new Clipboard(Display.getDefault());
			copyToClipboardProvider.copyToClipboard(clipboard, extendedTableViewer);
		}
	}
}
