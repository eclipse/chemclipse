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
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.ICustomSelectionHandler;
import org.eclipse.swt.widgets.Event;
import org.swtchart.Range;

public class ChromatogramSelectionHandler implements ICustomSelectionHandler {

	private ExtendedChromatogramUI extendedChromatogramUI;

	public ChromatogramSelectionHandler(ExtendedChromatogramUI extendedChromatogramUI) {
		this.extendedChromatogramUI = extendedChromatogramUI;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handleUserSelection(Event event) {

		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null) {
			/*
			 * Get the range.
			 */
			BaseChart baseChart = extendedChromatogramUI.getChromatogramChart().getBaseChart();
			Range rangeX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
			Range rangeY = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
			//
			int startRetentionTime = (int)rangeX.lower;
			int stopRetentionTime = (int)rangeX.upper;
			float startAbundance = (float)rangeY.lower;
			float stopAbundance = (float)rangeY.upper;
			//
			extendedChromatogramUI.setChromatogramSelectionRange(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
		}
	}
}
