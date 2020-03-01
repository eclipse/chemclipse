/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.marker.AbstractBaseChartPaintListener;
import org.eclipse.swtchart.extensions.marker.IBaseChartPaintListener;

public class PeakTracesOffsetListener extends AbstractBaseChartPaintListener implements IBaseChartPaintListener {

	private int offsetRetentionTime = 0;

	public PeakTracesOffsetListener(BaseChart baseChart) {
		super(baseChart);
	}

	public void setOffsetRetentionTime(int offsetRetentionTime) {

		this.offsetRetentionTime = offsetRetentionTime;
	}

	@Override
	public void paintControl(PaintEvent e) {

		if(offsetRetentionTime > 0) {
			BaseChart baseChart = getBaseChart();
			double rangeX = baseChart.getMaxX() - baseChart.getMinX() + 1;
			int width = e.width;
			int partOffset = (int)(width / rangeX * offsetRetentionTime);
			if(partOffset > 0) {
				/*
				 * Style
				 */
				e.gc.setForeground(getForegroundColor());
				e.gc.setLineStyle(SWT.LINE_DASHDOT);
				/*
				 * Left and right offset
				 */
				int leftOffset = partOffset;
				e.gc.drawLine(leftOffset, 0, leftOffset, e.height);
				int rightOffset = width - partOffset;
				e.gc.drawLine(rightOffset, 0, rightOffset, e.height);
			}
		}
	}
}
