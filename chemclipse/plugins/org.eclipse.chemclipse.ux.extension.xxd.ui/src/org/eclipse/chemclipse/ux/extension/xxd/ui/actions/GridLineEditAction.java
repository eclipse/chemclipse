/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.actions;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChartGridSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI;
import org.eclipse.jface.action.Action;
import org.eclipse.swtchart.extensions.core.IChartSettings;

public class GridLineEditAction extends Action {

	private ChartGridSupport chartGridSupport = new ChartGridSupport();
	private ExtendedChromatogramUI extendedChromatogramUI;

	public GridLineEditAction(ExtendedChromatogramUI extendedChromatogramUI) {

		super("GridLine", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_GRID, IApplicationImage.SIZE_16x16));
		setToolTipText("Toggle the chart grid lines.");
		this.extendedChromatogramUI = extendedChromatogramUI;
	}

	@Override
	public void run() {

		ChromatogramChart chromatogramChart = extendedChromatogramUI.getChromatogramChart();
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		boolean isGridDisplayed = chartGridSupport.isGridDisplayed(chartSettings);
		chartGridSupport.showGrid(chartSettings, !isGridDisplayed);
		chromatogramChart.applySettings(chartSettings);
	}
}