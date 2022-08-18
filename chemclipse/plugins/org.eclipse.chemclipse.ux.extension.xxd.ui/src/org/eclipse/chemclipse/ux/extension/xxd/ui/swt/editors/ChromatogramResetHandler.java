/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.ResetChartHandler;

public class ChromatogramResetHandler extends ResetChartHandler {

	private ExtendedChromatogramUI extendedChromatogramUI;

	public ChromatogramResetHandler(ExtendedChromatogramUI extendedChromatogramUI) {

		this.extendedChromatogramUI = extendedChromatogramUI;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		super.execute(shell, scrollableChart);
		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null) {
			List<? extends IPeak> selectedPeaks = new ArrayList<>(chromatogramSelection.getSelectedPeaks());
			chromatogramSelection.reset(true);
			chromatogramSelection.setSelectedPeaks(selectedPeaks);
		}
	}
}
