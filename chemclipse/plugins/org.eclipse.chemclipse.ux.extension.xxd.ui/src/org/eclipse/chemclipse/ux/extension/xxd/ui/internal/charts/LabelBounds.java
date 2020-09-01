/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.Transform;

public class LabelBounds {

	public static final boolean DEBUG_LOG = false;
	public static final boolean DEBUG_FENCES = false;
	//
	private final float[] pointArray = new float[10];
	private final int[] transformedPoints = new int[10];
	private final int width;
	private final int height;
	private final GC gc;
	private Region region;

	public LabelBounds(GC gc, TargetLabel targetLabel) {

		this.gc = gc;
		Point labelSize = gc.textExtent(targetLabel.getLabel());
		width = labelSize.x;
		height = labelSize.y;
	}

	public boolean intersects(LabelBounds other) {

		if(other != null && other.region != null) {
			for(int i = 0; i < transformedPoints.length; i += 2) {
				if(other.region.contains(transformedPoints[i], transformedPoints[i + 1]) || region.contains(other.transformedPoints[i], other.transformedPoints[i + 1])) {
					return true;
				}
			}
		}
		return false;
	}

	public void dispose() {

		if(region != null) {
			region.dispose();
		}
		region = null;
	}

	public void setTransform(Transform transform) {

		// p0
		pointArray[0] = 0;
		pointArray[1] = 0;
		// p1
		pointArray[2] = width;
		pointArray[3] = 0;
		// p2
		pointArray[4] = width;
		pointArray[5] = height;
		// p3
		pointArray[6] = 0;
		pointArray[7] = height;
		// pc
		pointArray[8] = 0;
		pointArray[9] = height / 2f;
		transform.transform(pointArray);
		for(int i = 0; i < transformedPoints.length; i++) {
			transformedPoints[i] = Math.round(pointArray[i]);
		}
		if(region != null) {
			region.dispose();
		}
		region = new Region(gc.getDevice());
		region.add(transformedPoints);
	}

	public int getWidth() {

		return width;
	}

	public int getHeight() {

		return height;
	}

	public float getCx() {

		return pointArray[8];
	}

	public float getCy() {

		return pointArray[9];
	}

	public float getTopX() {

		return pointArray[2];
	}

	public float getTopY() {

		return pointArray[3];
	}

	public float getBottomX() {

		return pointArray[6];
	}

	public float getBottomY() {

		return pointArray[7];
	}

	public float getRightX() {

		return pointArray[4];
	}

	public float getLeftX() {

		return pointArray[0];
	}

	public float getLeftY() {

		return pointArray[1];
	}

	public float offsetX(LabelBounds other) {

		float w;
		if(transformedPoints[1] == transformedPoints[3] || transformedPoints[0] == transformedPoints[2]) {
			w = getRightX();
		} else {
			float x1 = pointArray[6];
			float y1 = pointArray[7];
			float x2 = pointArray[4];
			float y2 = pointArray[5];
			float dx = x2 - x1;
			float m = (y2 - y1) / dx;
			float b = (x2 * y1 - x1 * y2) / dx;
			w = Math.min((other.getLeftY() - b) / m, getRightX());
		}
		if(DEBUG_LOG) {
			System.out.println("w=" + w + " rx=" + getRightX() + ", olx=" + other.getLeftX());
		}
		return w - other.getLeftX();
	}

	public float offsetY(LabelBounds other) {

		float h;
		if(other.getCx() > getTopX() || transformedPoints[0] == transformedPoints[2]) {
			h = getTopY();
		} else {
			float x = other.getBottomX();
			float x1 = pointArray[0];
			float y1 = pointArray[1];
			float x2 = pointArray[2];
			float y2 = pointArray[3];
			float dx = x2 - x1;
			float m = (y2 - y1) / dx;
			float b = (x2 * y1 - x1 * y2) / dx;
			h = Math.min(Math.max(m * x + b, getTopY()), getBottomY());
		}
		if(DEBUG_LOG) {
			System.out.println("h=" + h + ", by = " + getBottomY());
		}
		return getBottomY() - h + (other.getCy() - getCy());
	}

	public void paintBounds() {

		gc.setTransform(null);
		Color old_fg = gc.getForeground();
		Font old_font = gc.getFont();
		try {
			Font font = new Font(gc.getDevice(), PreferenceConstants.DEF_CHROMATOGRAM_PEAK_LABEL_FONT_NAME, 8, PreferenceConstants.DEF_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE);
			gc.setFont(font);
			gc.setLineStyle(SWT.LINE_DASH);
			gc.drawPolygon(transformedPoints);
			paintPoint(0, SWT.COLOR_BLUE);
			paintPoint(1, SWT.COLOR_RED);
			paintPoint(2, SWT.COLOR_GREEN);
			paintPoint(3, SWT.COLOR_GRAY);
			paintPoint(4, SWT.COLOR_BLACK);
			gc.setLineStyle(SWT.LINE_SOLID);
			if(DEBUG_FENCES) {
				Path path = new Path(gc.getDevice());
				int sx = transformedPoints[0] - 100;
				for(int x = sx; x < transformedPoints[2] + 100; x += 10) {
					float x1 = pointArray[4];
					float y1 = pointArray[5];
					float x2 = pointArray[6];
					float y2 = pointArray[7];
					float dx = x2 - x1;
					float m = (y2 - y1) / dx;
					float b = (x2 * y1 - x1 * y2) / dx;
					float y = m * x + b;
					System.out.println("f(" + x + ")=" + y);
					if(sx == x) {
						path.moveTo(x, y);
					} else {
						path.lineTo(x, y);
					}
					gc.drawLine(x, (int)y - 10, x, (int)y);
				}
				gc.drawPath(path);
				path.dispose();
			}
			if(DEBUG_FENCES) {
				Path path = new Path(gc.getDevice());
				int sx = transformedPoints[0] - 100;
				for(int x = sx; x < transformedPoints[2] + 100; x += 10) {
					float x1 = pointArray[4];
					float y1 = pointArray[5];
					float x2 = pointArray[6];
					float y2 = pointArray[7];
					float dx = x2 - x1;
					float m = (y2 - y1) / dx;
					float b = (x2 * y1 - x1 * y2) / dx;
					float h = Math.min(Math.max(m * x + b, getTopY()), getBottomY());
					if(x == sx) {
						path.moveTo(x, h);
					} else {
						path.lineTo(x, h);
					}
					gc.drawLine(x, (int)getBottomY(), x, (int)h);
				}
				gc.drawPath(path);
				path.dispose();
			}
			font.dispose();
		} finally {
			gc.setFont(old_font);
			gc.setForeground(old_fg);
		}
	}

	private void paintPoint(int p, int color) {

		int i = p * 2;
		int x = transformedPoints[i];
		int y = transformedPoints[i + 1];
		int lw = 5;
		gc.setForeground(gc.getDevice().getSystemColor(color));
		String string;
		if(p == 4) {
			string = "pc";
		} else {
			string = "p" + p;
		}
		Point s = gc.stringExtent(string);
		if(p == 0) {
			gc.drawString(string, x - s.x - 5, y - s.y - 5, true);
		} else if(p == 1) {
			gc.drawString(string, x, y - s.y - 5, true);
		} else if(p == 2) {
			gc.drawString(string, x, y, true);
		} else {
			gc.drawString(string, x - s.x, y, true);
		}
		gc.drawLine(x + lw, y + lw, x - lw, y - lw);
		gc.drawLine(x - lw, y + lw, x + lw, y - lw);
	}
}
