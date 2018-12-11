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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.listener;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swtchart.ICustomPaintListener;

public class SplitSelectionPaintListener implements ICustomPaintListener {

	private static final String LABEL_PERPENDICULAR_DROP = "P";
	private static final String LABEL_TANGENT_SKIM = "T";
	//
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	//

	@Override
	public void paintControl(PaintEvent e) {

		Color foreground = e.gc.getForeground();
		Color background = e.gc.getBackground();
		e.gc.setForeground(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		if(x1 == x2) {
			if(x1 != 0) {
				Point position = getLabelPosition(e, x1, y1, LABEL_PERPENDICULAR_DROP);
				e.gc.drawText(LABEL_PERPENDICULAR_DROP, position.x, position.y);
				e.gc.drawLine(x1, y1, x1, e.height);
			}
		} else {
			if(x1 != 0) {
				Point position = getLabelPosition(e, x1, y1, LABEL_TANGENT_SKIM);
				e.gc.drawText(LABEL_TANGENT_SKIM, position.x, position.y);
				e.gc.drawLine(x1, y1, x1, e.height);
			}
			if(x2 != 0) {
				Point position = getLabelPosition(e, x2, y2, LABEL_TANGENT_SKIM);
				e.gc.drawText(LABEL_TANGENT_SKIM, position.x, position.y);
				e.gc.drawLine(x2, y2, x2, e.height);
			}
		}
		e.gc.setForeground(foreground);
		e.gc.setBackground(background);
	}

	private Point getLabelPosition(PaintEvent e, int x, int y, String label) {

		Point labelSize = e.gc.textExtent(label);
		int xPosition = x - (int)(labelSize.x / 2.0d);
		int yPosition = y - labelSize.y;
		return new Point(xPosition, yPosition);
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
		y1 = 0;
		x2 = 0;
		y2 = 0;
	}

	/**
	 * @param x1
	 */
	public void setX1(int x1) {

		this.x1 = x1;
	}

	/**
	 * @param y1
	 */
	public void setY1(int y1) {

		this.y1 = y1;
	}

	/**
	 * @param x2
	 */
	public void setX2(int x2) {

		this.x2 = x2;
	}

	/**
	 * @param y2
	 */
	public void setY2(int y2) {

		this.y2 = y2;
	}
}
