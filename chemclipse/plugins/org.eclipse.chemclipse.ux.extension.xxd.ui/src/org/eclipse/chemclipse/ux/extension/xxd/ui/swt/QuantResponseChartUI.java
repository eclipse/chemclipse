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
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.model.quantitation.IResponseSignals;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.IEquation;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.CalibrationChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageQuantitationAxes;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private Composite toolbarInfo;
	private Label labelInfo;
	private CalibrationChart calibrationChart;
	private IQuantitationCompound quantitationCompound;

	public QuantResponseChartUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void update(IQuantitationCompound quantitationCompound) {

		this.quantitationCompound = quantitationCompound;
		setQuantitationCompound();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		toolbarInfo = createToolbarInfo(composite);
		createCalibrationChart(composite);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createButtonToggleToolbarInfo(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfo = new Label(composite, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				calibrationChart.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createToggleLegendMarkerButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend marker.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_LEGEND_MARKER, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				calibrationChart.togglePositionLegendVisibility();
				calibrationChart.redraw();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chart");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePageQuantitationAxes()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void createCalibrationChart(Composite parent) {

		calibrationChart = new CalibrationChart(parent, SWT.NONE);
		calibrationChart.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void applySettings() {

		calibrationChart.modifyAxes(true);
		setQuantitationCompound();
	}

	private void reset() {

		setQuantitationCompound();
	}

	private void setQuantitationCompound() {

		labelInfo.setText("");
		calibrationChart.deleteSeries();
		//
		if(quantitationCompound != null) {
			//
			labelInfo.setText("f(x) = a+bx², R² = 0.999"); // TODO
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			ILineSeriesData lineSeriesDataPoints;
			ILineSeriesData lineSeriesDataEquation;
			//
			IColorScheme colors = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_CALIBRATION));
			calibrationChart.setConcentrationLabel(quantitationCompound.getConcentrationUnit());
			boolean useCrossZero = quantitationCompound.isCrossZero();
			CalibrationMethod calibrationMethod = quantitationCompound.getCalibrationMethod();
			IResponseSignals concentrationResponseEntries = quantitationCompound.getResponseSignals();
			Set<Double> signals = concentrationResponseEntries.getSignalSet();
			for(double signal : signals) {
				/*
				 * Signals
				 */
				Color color = colors.getColor();
				String label = Double.toString(signal);
				List<IResponseSignal> signalEntries = concentrationResponseEntries.getList(signal);
				lineSeriesDataPoints = getLineSeriesData(signalEntries, label, color);
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
					lineSeriesDataEquation = getLineSeriesData(signalEntries, equation, calibrationMethod, useCrossZero, pointMin, pointMax, label, color);
					if(lineSeriesDataEquation != null) {
						lineSeriesDataList.add(lineSeriesDataEquation);
					}
				}
				colors.incrementColor();
			}
			//
			calibrationChart.addSeriesData(lineSeriesDataList, LineChart.NO_COMPRESSION);
		}
	}

	private ILineSeriesData getLineSeriesData(List<IResponseSignal> signalEntries, String label, Color color) {

		int size = signalEntries.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		for(int i = 0; i < size; i++) {
			IResponseSignal concentrationResponseEntry = signalEntries.get(i);
			xSeries[i] = concentrationResponseEntry.getConcentration();
			ySeries[i] = concentrationResponseEntry.getResponse();
		}
		//
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label, color);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setSymbolType(PlotSymbolType.CIRCLE);
		lineSeriesSettings.setSymbolSize(5);
		//
		return lineSeriesData;
	}

	private ILineSeriesData getLineSeriesData(List<IResponseSignal> signalEntries, IEquation equation, CalibrationMethod calibrationMethod, boolean useCrossZero, Point pointMin, Point pointMax, String label, Color color) {

		ILineSeriesData lineSeriesData = null;
		switch(calibrationMethod) {
			case LINEAR:
				/*
				 * Equations may not have a negative slope.
				 * In this case, use Cross Zero = true.
				 */
				label += " eq";
				if(((LinearEquation)equation).getA() < 0) {
					lineSeriesData = getErrorEquationSeries(label, color);
				} else {
					lineSeriesData = calculateLinearEquationSeries(equation, useCrossZero, pointMin, pointMax, label, color);
				}
				break;
			case QUADRATIC:
				/*
				 * Quadratic
				 */
				label += " eq^2";
				try {
					lineSeriesData = calculateQuadraticEquationSeries(equation, useCrossZero, pointMin, pointMax, label, color);
				} catch(Exception e) {
					lineSeriesData = getErrorEquationSeries(label, color);
				}
				break;
			case AVERAGE:
				/*
				 * Average
				 */
				label += " avg";
				try {
					lineSeriesData = calculateAverageSeries(signalEntries, label, color);
				} catch(Exception e) {
					lineSeriesData = getErrorEquationSeries(label, color);
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

	private ILineSeriesData calculateLinearEquationSeries(IEquation equation, boolean useCrossZero, Point pointMin, Point pointMax, String label, Color color) {

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
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label, color);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineStyle(LineStyle.SOLID);
		//
		return lineSeriesData;
	}

	private ILineSeriesData calculateQuadraticEquationSeries(IEquation equation, boolean useCrossZero, Point pointMin, Point pointMax, String label, Color color) {

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
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label, color);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineStyle(LineStyle.SOLID);
		//
		return lineSeriesData;
	}

	private ILineSeriesData calculateAverageSeries(List<IResponseSignal> entries, String label, Color color) {

		double x = 0.0d;
		double y = 0.0d;
		int size = entries.size();
		if(size == 0) {
			return null;
		}
		/*
		 * Calculate the center.
		 */
		for(IResponseSignal entry : entries) {
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
		ILineSeriesData lineSeriesData = getLineSeriesData(xSeries, ySeries, label, color);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setSymbolType(PlotSymbolType.CROSS);
		//
		return lineSeriesData;
	}

	private ILineSeriesData getErrorEquationSeries(String label, Color color) {

		double[] xSeries = {0};
		double[] ySeries = {0};
		//
		return getLineSeriesData(xSeries, ySeries, label, color);
	}

	private ILineSeriesData getLineSeriesData(double[] xSeries, double[] ySeries, String label, Color color) {

		ISeriesData seriesData = new SeriesData(xSeries, ySeries, label);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineStyle(LineStyle.NONE);
		lineSeriesSettings.setLineWidth(1);
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setEnableArea(false);
		lineSeriesSettings.setSymbolType(PlotSymbolType.NONE);
		lineSeriesSettings.setSymbolColor(color);
		lineSeriesSettings.setSymbolSize(1);
		//
		return lineSeriesData;
	}
}
