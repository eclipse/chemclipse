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

import org.eclipse.chemclipse.ui.service.swt.charts.AbstractPointSeriesSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.ColorFormatSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public class LineSeriesSettings extends AbstractPointSeriesSettings implements ILineSeriesSettings {

	private int antialias;
	private boolean enableArea;
	private Color lineColor;
	private int lineWidth;
	private boolean enableStack;
	private boolean enableStep;

	public LineSeriesSettings() {
		antialias = SWT.DEFAULT;
		enableArea = true;
		lineColor = ColorFormatSupport.COLOR_RED;
		lineWidth = 1;
		enableStack = false;
		enableStep = false;
	}

	@Override
	public int getAntialias() {

		return antialias;
	}

	@Override
	public void setAntialias(int antialias) {

		this.antialias = antialias;
	}

	@Override
	public boolean isEnableArea() {

		return enableArea;
	}

	@Override
	public void setEnableArea(boolean enableArea) {

		this.enableArea = enableArea;
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

	@Override
	public boolean isEnableStack() {

		return enableStack;
	}

	@Override
	public void setEnableStack(boolean enableStack) {

		this.enableStack = enableStack;
	}

	@Override
	public boolean isEnableStep() {

		return enableStep;
	}

	@Override
	public void setEnableStep(boolean enableStep) {

		this.enableStep = enableStep;
	}
}
