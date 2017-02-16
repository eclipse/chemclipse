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
package org.eclipse.chemclipse.ui.service.swt.charts;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.graphics.Color;
import org.swtchart.ILineSeries.PlotSymbolType;

public abstract class AbstractPointSeriesSettings extends AbstractSeriesSettings implements IPointSeriesSettings {

	private PlotSymbolType symbolType;
	private int symbolSize;
	private Color symbolColor;

	public AbstractPointSeriesSettings() {
		symbolType = PlotSymbolType.NONE;
		symbolSize = 8;
		symbolColor = Colors.BLACK;
	}

	@Override
	public PlotSymbolType getSymbolType() {

		return symbolType;
	}

	@Override
	public void setSymbolType(PlotSymbolType symbolType) {

		this.symbolType = symbolType;
	}

	@Override
	public int getSymbolSize() {

		return symbolSize;
	}

	@Override
	public void setSymbolSize(int symbolSize) {

		this.symbolSize = symbolSize;
	}

	@Override
	public Color getSymbolColor() {

		return symbolColor;
	}

	@Override
	public void setSymbolColor(Color symbolColor) {

		this.symbolColor = symbolColor;
	}
}
