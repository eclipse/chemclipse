/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.listener;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swtchart.ICustomPaintListener;

public class BoxSelectionPaintListener implements ICustomPaintListener {

	public static final String HIGHLIGHT_BOX_LEFT = "HIGHLIGHT_BOX_LEFT";
	public static final String HIGHLIGHT_BOX_RIGHT = "HIGHLIGHT_BOX_RIGHT";
	public static final String HIGHLIGHT_BOX_NONE = "HIGHLIGHT_BOX_NONE";
	//
	private int x1;
	private int x2;
	private String highlightBox = HIGHLIGHT_BOX_NONE;

	@Override
	public void paintControl(PaintEvent e) {

		Color foreground = e.gc.getForeground();
		Color background = e.gc.getBackground();
		Pattern pattern = e.gc.getBackgroundPattern();
		//
		int height = e.height;
		e.gc.setForeground(Colors.BLACK);
		//
		Pattern patternSelected = new Pattern(e.display, 0, e.height, e.width, e.height, Colors.WHITE, 0x44, Colors.WHITE, 0x44);
		Pattern patternNormal = new Pattern(e.display, 0, e.height, e.width, e.height, Colors.WHITE, 0xDD, Colors.WHITE, 0xDD);
		/*
		 * Left Box
		 */
		if(x1 > 0) {
			int width = x1;
			if(highlightBox.equals(HIGHLIGHT_BOX_LEFT)) {
				e.gc.setBackgroundPattern(patternSelected);
			} else {
				e.gc.setBackgroundPattern(patternNormal);
			}
			e.gc.fillRectangle(0, 0, width, height);
			e.gc.drawLine(x1, 0, x1, height);
		}
		/*
		 * Right Box
		 */
		if(x2 > 0) {
			int width = e.width - x2;
			if(highlightBox.equals(HIGHLIGHT_BOX_RIGHT)) {
				e.gc.setBackgroundPattern(patternSelected);
			} else {
				e.gc.setBackgroundPattern(patternNormal);
			}
			e.gc.fillRectangle(x2, 0, width, height);
			e.gc.drawLine(x2, 0, x2, height);
		}
		//
		e.gc.setForeground(foreground);
		e.gc.setBackground(background);
		e.gc.setBackgroundPattern(pattern);
		/*
		 * Dispose the patterns
		 */
		patternNormal.dispose();
		patternSelected.dispose();
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

	public void setHighlightBox(String highlightBox) {

		this.highlightBox = highlightBox;
	}
}
