/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.menu;

import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.dialogs.ClipboardSettingsDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

public class ClipboardSettingsHandler extends AbstractTableMenuEntry implements ITableMenuEntry {

	@Override
	public String getCategory() {

		return SupportMessages.exportTableSelection;
	}

	@Override
	public String getName() {

		return SupportMessages.clipboardSettings;
	}

	@Override
	public void execute(ExtendedTableViewer extendedTableViewer) {

		ClipboardSettingsDialog clipboardSettingsDialog = new ClipboardSettingsDialog(Display.getDefault().getActiveShell());
		clipboardSettingsDialog.setExtendedTableViewer(extendedTableViewer);
		if(clipboardSettingsDialog.open() == Dialog.OK) {
			extendedTableViewer.setCopyHeaderToClipboard(clipboardSettingsDialog.isCopyHeader());
			extendedTableViewer.setCopyValueDelimiterClipboard(clipboardSettingsDialog.getValueDelimiter());
			extendedTableViewer.setCopyColumnsToClipboard(clipboardSettingsDialog.getColumnsSelection());
		}
	}
}