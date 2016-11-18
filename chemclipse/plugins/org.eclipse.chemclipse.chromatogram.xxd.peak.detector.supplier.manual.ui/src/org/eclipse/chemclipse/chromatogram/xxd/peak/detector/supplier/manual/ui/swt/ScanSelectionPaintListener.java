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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Display;
import org.swtchart.ICustomPaintListener;

public class ScanSelectionPaintListener implements ICustomPaintListener {

	private int x1;
	private int x2;

	@Override
	public void paintControl(PaintEvent e) {

		e.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		int height = e.height;
		/*
		 * Left Box
		 */
		if(x1 > 0) {
			int width = x1;
			e.gc.fillRectangle(0, 0, width, height);
		}
		/*
		 * Right Box
		 */
		if(x2 > 0) {
			int width = e.width - x2;
			e.gc.fillRectangle(x2, 0, width, height);
		}
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
