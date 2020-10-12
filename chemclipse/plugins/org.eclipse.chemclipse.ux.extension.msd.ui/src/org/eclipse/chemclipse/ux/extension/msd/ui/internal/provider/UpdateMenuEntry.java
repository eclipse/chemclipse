/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider;

import org.eclipse.chemclipse.ux.extension.msd.ui.swt.IMassSpectrumChart;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class UpdateMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

	@Override
	public String getCategory() {

		return "Mass Spectrum";
	}

	@Override
	public String getName() {

		return "Update";
	}

	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		if(scrollableChart instanceof IMassSpectrumChart) {
			IMassSpectrumChart massSpectrumChart = (IMassSpectrumChart)scrollableChart;
			massSpectrumChart.update();
		}
	}
}