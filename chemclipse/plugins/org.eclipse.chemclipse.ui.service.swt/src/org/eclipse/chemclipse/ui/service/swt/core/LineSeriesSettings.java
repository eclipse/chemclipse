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

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.graphics.Color;
import org.swtchart.ILineSeries.PlotSymbolType;

public class LineSeriesSettings extends AbstractSeriesSettings implements ILineSeriesSettings {

	private boolean enableArea;
	private PlotSymbolType symbolType;
	private int symbolSize;
	private Color lineColor;
	private int lineWidth;
	private boolean enableStack;
	private boolean enableStep;

	public LineSeriesSettings() {
		enableArea = true;
		symbolType = PlotSymbolType.NONE;
		symbolSize = 8;
		lineColor = Colors.RED;
		lineWidth = 1;
		enableStack = false;
		enableStep = false;
	}

	@Override
	public boolean isEnableArea() {

		return enableArea;
	}

	@Override
	public ILineSeriesSettings setEnableArea(boolean enableArea) {

		this.enableArea = enableArea;
		return this;
	}

	@Override
	public PlotSymbolType getSymbolType() {

		return symbolType;
	}

	@Override
	public ILineSeriesSettings setSymbolType(PlotSymbolType symbolType) {

		this.symbolType = symbolType;
		return this;
	}

	@Override
	public int getSymbolSize() {

		return symbolSize;
	}

	@Override
	public ILineSeriesSettings setSymbolSize(int symbolSize) {

		this.symbolSize = symbolSize;
		return this;
	}

	@Override
	public Color getLineColor() {

		return lineColor;
	}

	@Override
	public ILineSeriesSettings setLineColor(Color lineColor) {

		this.lineColor = lineColor;
		return this;
	}

	@Override
	public int getLineWidth() {

		return lineWidth;
	}

	@Override
	public ILineSeriesSettings setLineWidth(int lineWidth) {

		this.lineWidth = lineWidth;
		return this;
	}

	@Override
	public boolean isEnableStack() {

		return enableStack;
	}

	@Override
	public ILineSeriesSettings setEnableStack(boolean enableStack) {

		this.enableStack = enableStack;
		return this;
	}

	@Override
	public boolean isEnableStep() {

		return enableStep;
	}

	@Override
	public ILineSeriesSettings setEnableStep(boolean enableStep) {

		this.enableStep = enableStep;
		return this;
	}
}
