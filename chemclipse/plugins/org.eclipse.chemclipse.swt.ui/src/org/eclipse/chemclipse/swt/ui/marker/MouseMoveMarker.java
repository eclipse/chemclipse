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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.ICustomPaintListener;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class MouseMoveMarker implements ICustomPaintListener {

	private int xPosition = 0;
	private Color foregroundColor;

	public MouseMoveMarker() {
		foregroundColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	}

	public void setXPosition(int xPosition) {

		this.xPosition = xPosition;
	}

	@Override
	public void paintControl(PaintEvent e) {

		/*
		 * Plots a vertical line in the plot area at the x position of the mouse.
		 * It's used e.g. in the chromatogram editor.
		 */
		e.gc.setForeground(foregroundColor);
		e.gc.drawLine(xPosition, 0, xPosition, e.height);
	}

	@Override
	public boolean drawBehindSeries() {

		return false;
	}
}
