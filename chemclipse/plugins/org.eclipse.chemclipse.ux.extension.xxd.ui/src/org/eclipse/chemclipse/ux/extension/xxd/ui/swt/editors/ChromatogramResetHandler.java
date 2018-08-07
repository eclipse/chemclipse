/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.eavp.service.swtchart.core.ScrollableChart;
import org.eclipse.eavp.service.swtchart.menu.ResetChartHandler;
import org.eclipse.swt.widgets.Shell;

public class ChromatogramResetHandler extends ResetChartHandler {

	private ExtendedChromatogramUI extendedChromatogramUI;

	public ChromatogramResetHandler(ExtendedChromatogramUI extendedChromatogramUI) {
		this.extendedChromatogramUI = extendedChromatogramUI;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		super.execute(shell, scrollableChart);
		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null) {
			chromatogramSelection.reset(true);
		}
	}
}
