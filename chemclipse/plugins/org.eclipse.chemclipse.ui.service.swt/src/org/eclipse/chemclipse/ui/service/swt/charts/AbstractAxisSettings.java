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
import org.swtchart.IAxis.Position;

public abstract class AbstractAxisSettings implements IAxisSettings {

	private String title;
	private DecimalFormat decimalFormat;
	private Color color;
	private boolean visible;
	private Position position;

	public AbstractAxisSettings(String title) {
		this.title = title;
		decimalFormat = new DecimalFormat();
		color = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		visible = true;
		position = Position.Primary;
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

	@Override
	public Position getPosition() {

		return position;
	}

	@Override
	public void setPosition(Position position) {

		this.position = position;
	}
}
