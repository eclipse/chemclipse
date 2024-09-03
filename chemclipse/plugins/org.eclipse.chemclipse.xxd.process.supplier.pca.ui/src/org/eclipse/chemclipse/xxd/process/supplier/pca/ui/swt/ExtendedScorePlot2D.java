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
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ResultDelta;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.preferences.PreferencePageScorePlot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IMouseSupport;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;

public class ExtendedScorePlot2D extends Composite implements IExtendedPartUI {

	private AtomicReference<ScorePlot> scorePlotControl = new AtomicReference<>();
	private AtomicReference<PrincipalComponentUI> principalComponentControl = new AtomicReference<>();
	//
	private EvaluationPCA evaluationPCA = null;

	public ExtendedScorePlot2D(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(EvaluationPCA evaluationPCA) {

		this.evaluationPCA = evaluationPCA;
		updatePlot();
	}

	public void updatePlot() {

		updateWidgets();
		applySettings();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createScorePlot(this);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createPrincipalComponentUI(composite);
		createButtonReset(composite);
		createSettingsButton(composite);
	}

	private void createScorePlot(Composite parent) {

		ScorePlot scorePlot = new ScorePlot(parent, SWT.BORDER);
		scorePlot.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		IChartSettings chartSettings = scorePlot.getChartSettings();
		chartSettings.addHandledEventProcessor(new IHandledEventProcessor() {

			@Override
			public int getEvent() {

				return IMouseSupport.EVENT_MOUSE_DOUBLE_CLICK;
			}

			@Override
			public int getButton() {

				return IMouseSupport.MOUSE_BUTTON_LEFT;
			}

			@Override
			public int getStateMask() {

				return SWT.NONE;
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
					double pY = rangeY.lower + (rangeY.upper - rangeY.lower) * ((1.0d / height) * y);
					/*
					 * Map the result deltas.
					 */
					PrincipalComponentUI principalComponentUI = principalComponentControl.get();
					int pcX = principalComponentUI.getPCX();
					int pcY = principalComponentUI.getPCY();
					IResultsPCA<? extends IResultPCA, ? extends IVariable> resultsPCA = evaluationPCA.getResults();
					List<? extends IResultPCA> resultList = resultsPCA.getPcaResultList();
					List<ResultDelta> resultDeltas = new ArrayList<>();
					//
					for(int i = 0; i < resultList.size(); i++) {
						IResultPCA pcaResult = resultList.get(i);
						IPoint pointResult = getPoint(pcaResult, pcX, pcY, i);
						double deltaX = Math.abs(pointResult.getX() - pX);
						double deltaY = Math.abs(pointResult.getY() - pY);
						resultDeltas.add(new ResultDelta(pcaResult, deltaX, deltaY));
					}
					/*
					 * Get the closest result.
					 */
					if(!resultDeltas.isEmpty()) {
						Collections.sort(resultDeltas, Comparator.comparing(ResultDelta::getDeltaX).thenComparing(ResultDelta::getDeltaY));
						ResultDelta resultDelta = resultDeltas.get(0);
						IResultPCA resultPCA = resultDelta.getResultPCA();
						UpdateNotifierUI.update(event.display, IChemClipseEvents.TOPIC_PCA_UPDATE_RESULT, resultPCA);
					}
				}
			}
		});
		scorePlot.applySettings(chartSettings);
		//
		scorePlotControl.set(scorePlot);
	}

	private IPoint getPoint(IResultPCA pcaResult, int pcX, int pcY, int i) {

		double[] eigenSpace = pcaResult.getScoreVector();
		double rX = 0;
		if(pcX != 0) {
			rX = eigenSpace[pcX - 1]; // e.g. 0 = PC1
		} else {
			rX = i;
		}
		double rY = eigenSpace[pcY - 1]; // e.g. 1 = PC2
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

	private Button createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Reset the 2D plot.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				applySettings();
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePage.class, PreferencePageScorePlot.class), new ISettingsHandler() {

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

		ScorePlot scorePlot = scorePlotControl.get();
		scorePlot.deleteSeries();
		//
		if(evaluationPCA != null) {
			scorePlot.setInput(evaluationPCA, pcX, pcY);
		} else {
			scorePlot.setInput(null, pcX, pcY);
		}
	}
}