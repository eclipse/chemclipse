/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.marker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.ICustomPaintListener;

/**
 * @author Dr. Philip Wenig
 * 
 */
public abstract class AbstractMarker implements ICustomPaintListener, IMarker {

	private String marker = "X";
	private Color foregroundColor;
	private Color backgroundColor;

	public AbstractMarker() {
		foregroundColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		backgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	}

	public AbstractMarker(String marker) {
		this();
		this.marker = marker;
	}

	@Override
	public void setMarker(String marker) {

		this.marker = marker;
	}

	@Override
	public String getMarker() {

		return marker;
	}

	@Override
	public Color getBackgroundColor() {

		return backgroundColor;
	}

	@Override
	public Color getForegroundColor() {

		return foregroundColor;
	}
}
