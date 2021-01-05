/*******************************************************************************
 * Copyright (c) 2016, 2021 Dr. Janko Diminic, Dr. Philip Wenig.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.Peak;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.IBarSeries;
import org.eclipse.swtchart.IBarSeries.BarWidthStyle;
import org.eclipse.swtchart.ISeries.SeriesType;

public class ProteomsChart implements DisposeListener {

	private Chart chart;
	private Composite composite;
	private IBarSeries<?> barSeries;
	private ChartLabelDrawer chartLabelDrawer;
	private ChartLeftRightMotion chartLeftRightMotion;
	private ChartZoom chartZoom;
	private ArrayList<Peak> peaks;
	private boolean showIntensityPercent = false;
	private String xOStitle = "m/z";
	private String yOStitle = "Intensity";

	public ProteomsChart(Composite composite) {

		this.composite = composite;
		createBaseChart();
	}

	private void createBaseChart() {

		if(chart == null) {
			chart = new Chart(composite, SWT.NONE);
			chart.addDisposeListener(this);
			composite.setLayout(new FillLayout());
			chart.getTitle().setVisible(false);
			IAxisSet axisSet = chart.getAxisSet();
			IAxis x = axisSet.getXAxis(0);
			IAxis y = axisSet.getYAxis(0);
			x.getTitle().setText(getxOStitle());
			y.getTitle().setText(getyOStitle());
			barSeries = (IBarSeries<?>)chart.getSeriesSet().createSeries(SeriesType.BAR, "Series1");
			barSeries.setBarWidth(1);
			barSeries.setVisibleInLegend(false);
			barSeries.setBarWidthStyle(BarWidthStyle.FIXED);
			{ // helper
				// chartLabelDrawer = new ChartLabelDrawer(chart);
				chartLeftRightMotion = new ChartLeftRightMotion(chart);
				chartZoom = new ChartZoom(chart, 0.2f);
			}
		}
	}

	private double[] getDataX() {

		double[] d = new double[peaks.size()];
		for(int i = 0; i < d.length; i++) {
			d[i] = peaks.get(i).getMz();
		}
		return d;
	}

	private double[] getDataY() {

		double[] d = new double[peaks.size()];
		if(showIntensityPercent) {
			double maxIntensity = ChartUtil.findMaxintensity(peaks);
			for(int i = 0; i < d.length; i++) {
				d[i] = (peaks.get(i).getIntensity() / maxIntensity * 100);
			}
		} else {
			for(int i = 0; i < d.length; i++) {
				d[i] = peaks.get(i).getIntensity();
			}
		}
		return d;
	}

	/**
	 * Show peaks data on chart. Former data are removed.
	 * 
	 * @param peaks
	 *            List<Peak>
	 */
	public void showSpectrumPeaks(List<Peak> peaks) {

		this.peaks = new ArrayList<>(peaks);
		Collections.sort(this.peaks, new Comparator<Peak>() {

			@Override
			public int compare(Peak o1, Peak o2) {

				return Double.compare(o1.getMz(), o2.getMz());
			}
		});
		putDataToChart();
	}

	private void putDataToChart() {

		double[] dataX = getDataX();
		double[] dataY = getDataY();
		barSeries.setXSeries(dataX);
		barSeries.setYSeries(dataY);
		chart.getAxisSet().adjustRange();
		chart.redraw();
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {

		disposeQuietly(chartLabelDrawer, e);
		disposeQuietly(chartLeftRightMotion, e);
		disposeQuietly(chartZoom, e);
	}

	private void disposeQuietly(DisposeListener d, DisposeEvent e) {

		try {
			d.widgetDisposed(e);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	public boolean isShowIntensityPercent() {

		return showIntensityPercent;
	}

	public void setShowIntensityPercent(boolean showIntensityPercent) {

		this.showIntensityPercent = showIntensityPercent;
	}

	public String getxOStitle() {

		return xOStitle;
	}

	public void setxOStitle(String xOStitle) {

		this.xOStitle = xOStitle;
	}

	public String getyOStitle() {

		return yOStitle;
	}

	public void setyOStitle(String yOStitle) {

		this.yOStitle = yOStitle;
	}
}
