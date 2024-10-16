/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Lorenz Gerber - update feature table selection from loading plot
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Feature;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.chart2d.LoadingsPlot;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.preferences.PreferencePageLoadingPlot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IMouseSupport;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;

public class ExtendedLoadingsPlot extends Composite implements IExtendedPartUI {

	private AtomicReference<LoadingsPlot> plotControl = new AtomicReference<>();
	private AtomicReference<PrincipalComponentUI> principalComponentControl = new AtomicReference<>();
	private double pointX;
	private double pointY;
	//
	private EvaluationPCA evaluationPCA = null;

	public ExtendedLoadingsPlot(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(EvaluationPCA evaluationPCA) {

		this.evaluationPCA = evaluationPCA;
		updateInput();
	}

	public void updateInput() {

		updateWidgets();
		applySettings();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createPlot(this);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		createPrincipalComponentUI(composite);
		createSettingsButton(composite);
	}

	private void createPlot(Composite parent) {

		LoadingsPlot plot = new LoadingsPlot(parent, SWT.BORDER);
		plot.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		IChartSettings chartSettings = plot.getChartSettings();
		chartSettings.addHandledEventProcessor(new IHandledEventProcessor() {

			@Override
			public int getEvent() {

				return IMouseSupport.EVENT_MOUSE_DOWN;
			}

			@Override
			public int getButton() {

				return IMouseSupport.MOUSE_BUTTON_LEFT;
			}

			@Override
			public int getStateMask() {

				return SWT.MOD1;
			}

			@Override
			public void handleEvent(BaseChart baseChart, Event event) {

				if(evaluationPCA != null) {
					/*
					 * Determine the x|y coordinates.
					 */
					Rectangle rectangle = baseChart.getPlotArea().getBounds();
					int x = event.x;
					int y = event.y;
					int width = rectangle.width;
					int height = rectangle.height;
					/*
					 * Calculate the selected point.
					 */
					Range rangeX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
					Range rangeY = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
					pointX = rangeX.lower + (rangeX.upper - rangeX.lower) * ((1.0d / width) * x);
					pointY = rangeY.lower + (rangeY.upper - rangeY.lower) * ((1.0d / height) * (height - y));
				}
			}
		});
		chartSettings.addHandledEventProcessor(new IHandledEventProcessor() {

			@Override
			public int getEvent() {

				return IMouseSupport.EVENT_MOUSE_UP;
			}

			@Override
			public int getButton() {

				return IMouseSupport.MOUSE_BUTTON_LEFT;
			}

			@Override
			public int getStateMask() {

				return SWT.MOD1;
			}

			@Override
			public void handleEvent(BaseChart baseChart, Event event) {

				if(evaluationPCA != null) {
					/*
					 * Determine the x|y coordinates.
					 */
					Rectangle rectangle = baseChart.getPlotArea().getBounds();
					int x = event.x;
					int y = event.y;
					int width = rectangle.width;
					int height = rectangle.height;
					/*
					 * Calculate the selected point.
					 */
					Range rangeX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
					Range rangeY = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
					double pX = rangeX.lower + (rangeX.upper - rangeX.lower) * ((1.0d / width) * x);
					double pY = rangeY.lower + (rangeY.upper - rangeY.lower) * ((1.0d / height) * (height - y));
					/*
					 * Map the result deltas.
					 */
					PrincipalComponentUI principalComponentUI = principalComponentControl.get();
					int pcX = principalComponentUI.getPCX();
					int pcY = principalComponentUI.getPCY();
					IResultsPCA<? extends IResultPCA, ? extends IVariable> resultsPCA = evaluationPCA.getResults();
					// List<FeatureDelta> featureDeltas = new ArrayList<>();
					List<Feature> featureSelected = new ArrayList<>();
					//
					// Here need to prepare a result object with loading vectors per variable
					//
					for(int i = 0; i < resultsPCA.getExtractedVariables().size(); i++) {
						double[] variableLoading = getVariableLoading(resultsPCA, i);
						IPoint pointResult = getPoint(variableLoading, pcX, pcY, i);
						if(pointResult.getX() > pointX && pointResult.getX() < pX && pointResult.getY() < pointY && pointResult.getY() > pY) {
							featureSelected.add(evaluationPCA.getFeatureDataMatrix().getFeatures().get(i));
						}
						// double deltaX = Math.abs(pointResult.getX() - pX);
						// double deltaY = Math.abs(pointResult.getY() - pY);
						// featureDeltas.add(new FeatureDelta(evaluationPCA.getFeatureDataMatrix().getFeatures().get(i), deltaX, deltaY));
					}
					/*
					 * Get the closest result.
					 */
					if(!featureSelected.isEmpty()) {
						UpdateNotifierUI.update(event.display, IChemClipseEvents.TOPIC_PCA_UPDATE_RESULT, featureSelected.toArray());
						// featureSelected.toArray();
						// for(Feature feature : featureSelected) {
						//
						// UpdateNotifierUI.update(event.display, IChemClipseEvents.TOPIC_PCA_UPDATE_RESULT, feature);
						// }
						// Collections.sort(featureDeltas, Comparator.comparing(FeatureDelta::getDeltaX).thenComparing(FeatureDelta::getDeltaY));
						// FeatureDelta featureDelta = featureDeltas.get(0);
						// Feature feature = featureDelta.getFeature();
						// UpdateNotifierUI.update(event.display, IChemClipseEvents.TOPIC_PCA_UPDATE_RESULT, feature);
					}
				}
			}
		});
		plot.applySettings(chartSettings);
		//
		plotControl.set(plot);
	}

	private double[] getVariableLoading(IResultsPCA<? extends IResultPCA, ? extends IVariable> results, int number) {

		double[] variableLoading = new double[results.getLoadingVectors().size()];
		for(int i = 0; i < results.getLoadingVectors().size(); i++) {
			variableLoading[i] = results.getLoadingVectors().get(i)[number];
		}
		return variableLoading;
	}

	private IPoint getPoint(double[] variableLoading, int pcX, int pcY, int i) {

		double rX = 0;
		if(pcX != 0) {
			rX = variableLoading[pcX - 1]; // e.g. 0 = PC1
		} else {
			rX = i;
		}
		double rY = variableLoading[pcY - 1]; // e.g. 1 = PC2
		//
		return new Point(rX, rY);
	}

	private void createPrincipalComponentUI(Composite parent) {

		PrincipalComponentUI principalComponentUI = new PrincipalComponentUI(parent, SWT.NONE, PrincipalComponentUI.SPINNER_X | PrincipalComponentUI.SPINNER_Y);
		principalComponentUI.setSelectionListener(new ISelectionListenerPCs() {

			@Override
			public void update(int pcX, int pcY, int pcZ) {

				updatePlot(pcX, pcY);
			}
		});
		//
		principalComponentControl.set(principalComponentUI);
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePage.class, PreferencePageLoadingPlot.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		PrincipalComponentUI principalComponentUI = principalComponentControl.get();
		int pcX = principalComponentUI.getPCX();
		int pcY = principalComponentUI.getPCY();
		updatePlot(pcX, pcY);
	}

	private void updateWidgets() {

		PrincipalComponentUI principalComponentUI = principalComponentControl.get();
		if(evaluationPCA != null) {
			IAnalysisSettings analysisSettings = evaluationPCA.getSamples().getAnalysisSettings();
			principalComponentUI.setInput(analysisSettings);
		} else {
			principalComponentUI.setInput(null);
		}
	}

	private void updatePlot(int pcX, int pcY) {

		LoadingsPlot plot = plotControl.get();
		plot.deleteSeries();
		//
		if(evaluationPCA != null) {
			plot.setInput(evaluationPCA, pcX, pcY);
		} else {
			plot.setInput(null, pcX, pcY);
		}
	}
}