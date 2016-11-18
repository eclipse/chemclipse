/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.swt;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.swtchart.ICustomPaintListener;

public class ScanSelectionPaintListener implements ICustomPaintListener {

	private int x1;
	private int x2;

	@Override
	public void paintControl(PaintEvent e) {

		Color foreground = e.gc.getForeground();
		Color background = e.gc.getBackground();
		Pattern pattern = e.gc.getBackgroundPattern();
		e.gc.setForeground(Colors.BLACK);
		e.gc.setBackgroundPattern(new Pattern(e.display, 0, e.height, e.width, e.height, Colors.WHITE, 0xBB, Colors.WHITE, 0xBB));
		int height = e.height;
		/*
		 * Left Box
		 */
		if(x1 > 0) {
			int width = x1;
			e.gc.fillRectangle(0, 0, width, height);
			e.gc.drawLine(x1, 0, x1, height);
		}
		/*
		 * Right Box
		 */
		if(x2 > 0) {
			int width = e.width - x2;
			e.gc.fillRectangle(x2, 0, width, height);
			e.gc.drawLine(x2, 0, x2, height);
		}
		//
		e.gc.setForeground(foreground);
		e.gc.setBackground(background);
		e.gc.setBackgroundPattern(pattern);
	}

	@Override
	public boolean drawBehindSeries() {

		return false;
	}

	/**
	 * Resets the start and end point.
	 */
	public void reset() {

		x1 = 0;
		x2 = 0;
	}

	public void setX1(int x1) {

		this.x1 = x1;
	}

	public void setX2(int x2) {

		this.x2 = x2;
	}
}
