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
package org.eclipse.chemclipse.ui.service.swt.charts.line;

import org.eclipse.chemclipse.ui.service.swt.charts.ISeriesSettings;
import org.eclipse.swt.graphics.Color;
import org.swtchart.ILineSeries.PlotSymbolType;

public interface ILineSeriesSettings extends ISeriesSettings {

	int getAntialias();

	/**
	 * SWT.DEFAULT, SWT.ON, SWT.OFF
	 * 
	 * @param antialias
	 */
	void setAntialias(int antialias);

	boolean isEnableArea();

	ILineSeriesSettings setEnableArea(boolean enableArea);

	PlotSymbolType getSymbolType();

	ILineSeriesSettings setSymbolType(PlotSymbolType symbolType);

	int getSymbolSize();

	ILineSeriesSettings setSymbolSize(int symbolSize);

	Color getLineColor();

	ILineSeriesSettings setLineColor(Color lineColor);

	int getLineWidth();

	ILineSeriesSettings setLineWidth(int lineWidth);

	boolean isEnableStack();

	ILineSeriesSettings setEnableStack(boolean enableStack);

	boolean isEnableStep();

	ILineSeriesSettings setEnableStep(boolean enableStep);
}