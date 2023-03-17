/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
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

public class ResetColumnWidthHandler extends AbstractTableMenuEntry implements ITableMenuEntry {

	@Override
	public String getCategory() {

		return SupportMessages.columns;
	}

	@Override
	public String getName() {

		return SupportMessages.resetColumnWidth;
	}

	@Override
	public void execute(ExtendedTableViewer extendedTableViewer) {

		extendedTableViewer.resetColumnWidth();
	}
}