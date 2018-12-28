/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntries;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.IEquation;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.CalibrationChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageQuantitation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class QuantResponseChartUI extends Composite {

	private static final Logger logger = Logger.getLogger(QuantResponseChartUI.class);
	//
	private CalibrationChart calibrationChart;

	public QuantResponseChartUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	@SuppressWarnings("rawtypes")
	public void update(IQuantitationCompound quantitationCompound) {

		calibrationChart.deleteSeries();
		if(quantitationCompound != null) {
			setQuantitationCompoundSeries(quantitationCompound);
		}
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		createCalibrationChart(composite);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		createSettingsButton(composite);
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePageQuantitation();
				preferencePage.setTitle("Quantitation");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						apply();
					} catch(Exception e1) {
						MessageDialog.openError(e.widget.getDisplay().getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void createCalibrationChart(Composite parent) {

		calibrationChart = new CalibrationChart(parent, SWT.NONE);
		calibrationChart.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void apply() {

		System.out.println("TODO: Appy settings");
	}

	@SuppressWarnings("rawtypes")
	private void setQuantitationCompoundSeries(IQuantitationCompound quantitationCompound) {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		ILineSeriesData lineSeriesDataPoints;
		ILineSeriesData lineSeriesDataEquation;
		//
		calibrationChart.setConcentrationLabel(quantitationCompound.getConcentrationUnit());
		boolean useCrossZero = quantitationCompound.isCrossZero();
		CalibrationMethod calibrationMethod = quantitationCompound.getCalibrationMethod();
		IConcentrationResponseEntries concentrationResponseEntries = quantitationCompound.getConcentrationResponseEntries();
		Set<Double> signals = concentrationResponseEntries.getSignalSet();
		for(double signal : signals) {
			/*
			 * Signals
			 */
			String label = Double.toString(signal);
			List<IConcentrationResponseEntry> signalEntries = concentrationResponseEntries.getList(signal);
			lineSeriesDataPoints = getLineSeriesData(signalEntries, label);
			if(lineSeriesDataPoints != null) {
				/*
				 * Data Points
				 */
				lineSeriesDataList.add(lineSeriesDataPoints);
				/*
				 * Equation
				 */
				IEquation equation = concentrationResponseEntries.getLinearEquation(signal, useCrossZero);
				Point pointMin = new Point(0, 0);
				double x = Arrays.stream(lineSeriesDataPoints.getSeriesData().getXSeries()).max().getAsDouble();
				double y = Arrays.stream(lineSeriesDataPoints.getSeriesData().getYSeries()).max().getAsDouble();
				double xMax = x + x / 20.0d;
				double yMax = y + y / 20.0d;
				Point pointMax = new Point(xMax, yMax);
				lineSeriesDataEquation = getLineSeriesData(signalEntries, equation, calibrationMethod, useCrossZero, pointMin, pointMax, label);
				if(lineSeriesDataEquation != null) {
					lineSeriesDataList.add(lineSeriesDataEquation);
				}
			}
		}
		//
		calibrationChart.addSeriesData(lineSeriesDataList, LineChart.NO_COMPRESSION);
	}

	private ILineSeriesData getLineSeriesData(List<IConcentrationResponseEntry> signalEntries, String label) {

		int size = signalEntries.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		for(int i = 0; i < size; i++) {
			IConcentrationResponseEntry concentrationResponseEntry = signalEntries.get(i);
			xSeries[i] = concentrationResponseEntry.getConcentration();
			ySeries[i] = concentrationResponseEntry.getResponse();
		}
		//
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setSymbolType(PlotSymbolType.CIRCLE);
		lineSeriesSettings.setSymbolSize(5);
		//
		return lineSeriesData;
	}

	private ILineSeriesData getLineSeriesData(List<IConcentrationResponseEntry> signalEntries, IEquation equation, CalibrationMethod calibrationMethod, boolean useCrossZero, Point pointMin, Point pointMax, String label) {

		ILineSeriesData lineSeriesData = null;
		switch(calibrationMethod) {
			case LINEAR:
				/*
				 * Equations may not have a negative slope.
				 * In this case, use Cross Zero = true.
				 */
				label += " eq";
				if(((LinearEquation)equation).getA() < 0) {
					lineSeriesData = getErrorEquationSeries(label);
				} else {
					lineSeriesData = calculateLinearEquationSeries(equation, useCrossZero, pointMin, pointMax, label);
				}
				break;
			case QUADRATIC:
				/*
				 * Quadratic
				 */
				label += " eq^2";
				try {
					lineSeriesData = calculateQuadraticEquationSeries(equation, useCrossZero, pointMin, pointMax, label);
				} catch(Exception e) {
					lineSeriesData = getErrorEquationSeries(label);
				}
				break;
			case AVERAGE:
				/*
				 * Average
				 */
				label += " avg";
				try {
					lineSeriesData = calculateAverageSeries(signalEntries, label);
				} catch(Exception e) {
					lineSeriesData = getErrorEquationSeries(label);
				}
				break;
			case ISTD:
				/*
				 * Not used here.
				 */
				logger.warn("ISTD shouldn't be used here.");
				break;
		}
		//
		return lineSeriesData;
	}

	private ILineSeriesData calculateLinearEquationSeries(IEquation equation, boolean useCrossZero, Point pointMin, Point pointMax, String label) {

		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		double x;
		double y;
		double xMax = pointMax.getX();
		double yMax = pointMax.getY();
		/*
		 * Take care the intersections of the equations with
		 * the border are inside the plotting area.
		 */
		x = equation.calculateX(yMax);
		if(x > xMax) {
			y = equation.calculateY(xMax);
			x = xMax;
		} else {
			y = equation.calculateY(x);
		}
		/*
		 * Set the x and y signals.
		 */
		if(useCrossZero) {
			xSeries[0] = 0;
			ySeries[0] = 0;
		} else {
			xSeries[0] = pointMin.getX();
			ySeries[0] = pointMin.getY();
		}
		xSeries[1] = x;
		ySeries[1] = y;
		//
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineStyle(LineStyle.SOLID);
		//
		return lineSeriesData;
	}

	private ILineSeriesData calculateQuadraticEquationSeries(IEquation equation, boolean useCrossZero, Point pointMin, Point pointMax, String label) {

		int steps = 50; // Calculate the y values for 50 steps
		double xStart = (useCrossZero) ? 0 : pointMin.getX();
		double xStep = (pointMax.getX() - xStart) / steps;
		/*
		 * Why do we use a List here?
		 * The calculation of the quadratic line has several constraints.
		 * Values lower than 0 shall not be used. Furthermore, the
		 * array index must be not exceeded, but we don't know how many
		 * values are valid previously. Hence, it's a bit more resource
		 * consuming to use a List, but it's more safe.
		 */
		List<IPoint> points = new ArrayList<IPoint>();
		for(double x = xStart; x < pointMax.getX(); x += xStep) {
			/*
			 * Calculate the quadratic line series.
			 */
			double y = equation.calculateY(x);
			if(y > 0) {
				points.add(new Point(x, y));
			}
		}
		//
		double[] xSeries = new double[points.size()];
		double[] ySeries = new double[points.size()];
		for(int index = 0; index < points.size(); index++) {
			IPoint point = points.get(index);
			xSeries[index] = point.getX();
			ySeries[index] = point.getY();
		}
		//
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineStyle(LineStyle.SOLID);
		//
		return lineSeriesData;
	}

	private ILineSeriesData calculateAverageSeries(List<IConcentrationResponseEntry> entries, String label) {

		double x = 0.0d;
		double y = 0.0d;
		int size = entries.size();
		if(size == 0) {
			return null;
		}
		/*
		 * Calculate the center.
		 */
		for(IConcentrationResponseEntry entry : entries) {
			x += entry.getConcentration();
			y += entry.getResponse();
		}
		//
		x /= size;
		y /= size;
		//
		double[] xSeries = new double[]{x};
		double[] ySeries = new double[]{y};
		//
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setSymbolType(PlotSymbolType.CROSS);
		//
		return lineSeriesData;
	}

	private ILineSeriesData getErrorEquationSeries(String label) {

		double[] xSeries = {0};
		double[] ySeries = {0};
		//
		return getLineSeriesData(xSeries, ySeries, label);
	}

	private ILineSeriesData getLineSeriesData(double[] xSeries, double[] ySeries, String label) {

		ISeriesData seriesData = new SeriesData(xSeries, ySeries, label);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineStyle(LineStyle.NONE);
		lineSeriesSettings.setLineWidth(1);
		lineSeriesSettings.setEnableArea(false);
		lineSeriesSettings.setSymbolType(PlotSymbolType.NONE);
		lineSeriesSettings.setSymbolColor(Colors.RED);
		lineSeriesSettings.setSymbolSize(1);
		//
		return lineSeriesData;
	}
}
