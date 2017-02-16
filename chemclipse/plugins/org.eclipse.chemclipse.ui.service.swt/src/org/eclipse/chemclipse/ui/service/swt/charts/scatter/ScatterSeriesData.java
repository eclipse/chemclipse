/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ui.service.swt.charts.scatter;

import org.eclipse.chemclipse.ui.service.swt.charts.AbstractChartSeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.ISeriesData;

public class ScatterSeriesData extends AbstractChartSeriesData implements IScatterSeriesData {

	private IScatterSeriesSettings scatterSeriesSettings;

	public ScatterSeriesData(ISeriesData seriesData) {
		super(seriesData);
		this.scatterSeriesSettings = new ScatterSeriesSettings();
	}

	@Override
	public IScatterSeriesSettings getScatterSeriesSettings() {

		return scatterSeriesSettings;
	}
}
