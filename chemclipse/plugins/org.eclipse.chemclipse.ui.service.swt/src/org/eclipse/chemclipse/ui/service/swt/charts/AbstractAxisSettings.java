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

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractAxisSettings implements IAxisSettings {

	private String title;
	private DecimalFormat decimalFormat;
	private boolean enableLogScale;
	private boolean enableCategory;
	private Color color;
	private boolean visible;

	public AbstractAxisSettings(String title) {
		this.title = title;
		decimalFormat = new DecimalFormat();
		enableLogScale = false;
		color = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		visible = true;
	}

	@Override
	public String getTitle() {

		return title;
	}

	@Override
	public void setTitle(String title) {

		this.title = title;
	}

	@Override
	public DecimalFormat getDecimalFormat() {

		return decimalFormat;
	}

	@Override
	public void setDecimalFormat(DecimalFormat decimalFormat) {

		this.decimalFormat = decimalFormat;
	}

	@Override
	public boolean isEnableLogScale() {

		return enableLogScale;
	}

	@Override
	public void setEnableLogScale(boolean enableLogScale) {

		this.enableLogScale = enableLogScale;
	}

	@Override
	public boolean isEnableCategory() {

		return enableCategory;
	}

	@Override
	public Color getColor() {

		return color;
	}

	@Override
	public void setColor(Color color) {

		this.color = color;
	}

	@Override
	public boolean isVisible() {

		return visible;
	}

	@Override
	public void setVisible(boolean visible) {

		this.visible = visible;
	}
}
