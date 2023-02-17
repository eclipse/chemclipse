/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.menu;

import org.eclipse.chemclipse.support.ui.internal.provider.CopyToClipboardProvider;
import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;

public class CopyClipboardHandler extends AbstractTableMenuEntry implements ITableMenuEntry {

	private CopyToClipboardProvider copyToClipboardProvider = new CopyToClipboardProvider();

	@Override
	public String getCategory() {

		return ""; // Must be empty to be placed on the main menu level.
	}

	@Override
	public String getName() {

		return SupportMessages.copyClipboard;
	}

	@Override
	public void execute(ExtendedTableViewer extendedTableViewer) {

		Clipboard clipboard = new Clipboard(Display.getDefault());
		copyToClipboardProvider.copyToClipboard(clipboard, extendedTableViewer);
	}
}
