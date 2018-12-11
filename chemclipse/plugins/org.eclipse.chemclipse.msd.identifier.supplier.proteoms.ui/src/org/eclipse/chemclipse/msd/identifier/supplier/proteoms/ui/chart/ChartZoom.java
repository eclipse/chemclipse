/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.chart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.Range;

public class ChartZoom implements DisposeListener, MouseWheelListener, MouseListener {

	private final float step;
	private Chart chart;

	/**
	 * @param step
	 *            The zoom step expressed as a factor of the current range. Must
	 *            be between 0 and 1.
	 */
	public ChartZoom(Chart chart, float step) {
		this.chart = chart;
		if(step <= 0f || step >= 1) {
			throw new IllegalArgumentException("Parameter step must be in the range of 0 to 1.");
		}
		this.step = step;
		chart.getPlotArea().addMouseWheelListener(this);
		chart.getPlotArea().addMouseListener(this);
	}

	@Override
	public void mouseScrolled(MouseEvent e) {

		Point p = chart.getPlotArea().toControl(chart.toDisplay(e.x, e.y));
		IAxis[] axes;
		int dc;
		if((e.stateMask & SWT.CTRL) != 0) {
			axes = chart.getAxisSet().getYAxes();
			dc = p.y;
		} else {
			axes = chart.getAxisSet().getXAxes();
			dc = p.x;
		}
		for(IAxis axis : axes) {
			Range range = axis.getRange();
			double x = axis.getDataCoordinate(dc);
			if(e.count > 0) {
				// TODO maybe multiply step with count? Hard to explain in
				// Javadoc, but probably better
				range.lower = step * x + (1 - step) * range.lower;
				range.upper = step * x + (1 - step) * range.upper;
			} else {
				range.lower = -step * x + (1 + step) * range.lower;
				range.upper = -step * x + (1 + step) * range.upper;
			}
			if(axis.isCategoryEnabled()) {
				range.lower = Math.round(range.lower);
				range.upper = Math.round(range.upper);
				if(e.count < 0) {
					Range oldRange = axis.getRange();
					if(oldRange.lower == range.lower && oldRange.upper == range.upper) {
						range.lower--;
						range.upper++;
					}
				}
			}
			axis.setRange(range);
		}
		chart.redraw();
	}

	@Override
	public void widgetDisposed(DisposeEvent arg0) {

		if(chart != null) {
			chart.getPlotArea().removeMouseWheelListener(this);
			chart.getPlotArea().removeMouseListener(this);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0) {

		chart.getAxisSet().adjustRange();
		chart.redraw();
		// log.debug("Un zoom");
	}

	@Override
	public void mouseDown(MouseEvent arg0) {

	}

	@Override
	public void mouseUp(MouseEvent arg0) {

	}
}
