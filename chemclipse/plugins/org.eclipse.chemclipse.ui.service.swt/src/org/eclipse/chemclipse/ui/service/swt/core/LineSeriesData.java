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
package org.eclipse.chemclipse.ui.service.swt.core;

import org.eclipse.swt.graphics.Color;
import org.swtchart.ILineSeries.PlotSymbolType;

public class LineSeriesData extends AbstractSeriesData implements ILineSeriesData {

	private boolean enableArea;
	private PlotSymbolType symbolType;
	private int symbolSize;
	private Color lineColor;
	private int lineWidth;

	@Override
	public boolean isEnableArea() {

		return enableArea;
	}

	@Override
	public void setEnableArea(boolean enableArea) {

		this.enableArea = enableArea;
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
	public Color getLineColor() {

		return lineColor;
	}

	@Override
	public void setLineColor(Color lineColor) {

		this.lineColor = lineColor;
	}

	@Override
	public int getLineWidth() {

		return lineWidth;
	}

	@Override
	public void setLineWidth(int lineWidth) {

		this.lineWidth = lineWidth;
	}
}
