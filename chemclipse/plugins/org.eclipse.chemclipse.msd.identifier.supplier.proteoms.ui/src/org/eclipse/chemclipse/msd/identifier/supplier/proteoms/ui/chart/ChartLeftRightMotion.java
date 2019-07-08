/*******************************************************************************
 * Copyright (c) 2016, 2019 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 * Christoph Läubrich - adjust to new chart API
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.chart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IPlotArea;

public class ChartLeftRightMotion implements DisposeListener, KeyListener {

	private Chart chart;

	public ChartLeftRightMotion(Chart chart) {
		this.chart = chart;
		IPlotArea plotArea = chart.getPlotArea();
		if(plotArea instanceof Control) {
			((Control)plotArea).addKeyListener(this);
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent arg0) {

		if(chart != null) {
			IPlotArea plotArea = chart.getPlotArea();
			if(plotArea instanceof Control) {
				((Control)plotArea).removeKeyListener(this);
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {

		if(event.keyCode == SWT.ARROW_LEFT) {
			for(IAxis axis : chart.getAxisSet().getXAxes()) {
				axis.scrollUp();
			}
		} else if(event.keyCode == SWT.ARROW_RIGHT) {
			for(IAxis axis : chart.getAxisSet().getXAxes()) {
				axis.scrollDown();
			}
		}
		chart.redraw();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}
}
