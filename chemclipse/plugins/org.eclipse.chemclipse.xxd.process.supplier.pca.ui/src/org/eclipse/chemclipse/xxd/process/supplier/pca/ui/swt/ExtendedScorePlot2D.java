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
 * Lorenz Gerber - added box selection
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.chemclipse.xxd.process.supplier.pca.ui.help.HelpContext;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.preferences.PreferencePageScorePlot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IMouseSupport;
import org.eclipse.swtchart.extensions.core.UserSelection;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;
import org.eclipse.ui.PlatformUI;

public class ExtendedScorePlot2D extends Composite implements IExtendedPartUI {

	private AtomicReference<ScorePlot> scorePlotControl = new AtomicReference<>();
	private AtomicReference<PrincipalComponentUI> principalComponentControl = new AtomicReference<>();
	//
	private EvaluationPCA evaluationPCA = null;
	//
	private UserSelection userSelection = new UserSelection();

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
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, HelpContext.SCORE_PLOT);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		createPrincipalComponentUI(composite);
		createButtonReset(composite);
		createSettingsButton(composite);
		createButtonHelp(composite);
	}

	private void createScorePlot(Composite parent) {

		ScorePlot scorePlot = new ScorePlot(parent, SWT.BORDER);
		scorePlot.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		IChartSettings chartSettings = scorePlot.getChartSettings();
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
					if(event.count == 1) {
						userSelection.setSingleClick(true);
						userSelection.setStartCoordinate(event.x, event.y);
					}
				}
			}
		});
		chartSettings.addHandledEventProcessor(new IHandledEventProcessor() {

			@Override
			public int getEvent() {

				return IMouseSupport.EVENT_MOUSE_MOVE;
			}

			@Override
			public int getButton() {

				return IMouseSupport.MOUSE_BUTTON_NONE;
			}

			@Override
			public int getStateMask() {

				return SWT.MOD1;
			}

			@Override
			public void handleEvent(BaseChart baseChart, Event event) {

				if(userSelection.getStartX() > 0 && userSelection.getStartY() > 0) {
					userSelection.setStopCoordinate(event.x, event.y);
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
					 * Prepare Data viewport
					 */
					Rectangle rectangle = baseChart.getPlotArea().getBounds();
					int width = rectangle.width;
					int height = rectangle.height;
					Range rangeX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
					Range rangeY = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
					/*
					 * Determine x|y coordinates Start/Stop.
					 */
					int startX = userSelection.getStartX();
					int startY = userSelection.getStartY();
					int stopX = userSelection.getStopX();
					int stopY = userSelection.getStopY();
					/*
					 * Calculate selected points.
					 */
					double pXStart = rangeX.lower + (rangeX.upper - rangeX.lower) * ((1.0d / width) * startX);
					double pYStart = rangeY.lower + (rangeY.upper - rangeY.lower) * ((1.0d / height) * (height - startY));
					double pXStop = rangeX.lower + (rangeX.upper - rangeX.lower) * ((1.0d / width) * stopX);
					double pYStop = rangeY.lower + (rangeY.upper - rangeY.lower) * ((1.0d / height) * (height - stopY));
					/*
					 * Map the result deltas.
					 */
					PrincipalComponentUI principalComponentUI = principalComponentControl.get();
					int pcX = principalComponentUI.getPCX();
					int pcY = principalComponentUI.getPCY();
					IResultsPCA<? extends IResultPCA, ? extends IVariable> resultsPCA = evaluationPCA.getResults();
					List<IResultPCA> sampleSelected = new ArrayList<>();
					List<? extends IResultPCA> resultList = resultsPCA.getPcaResultList();
					/*
					 * get samples within selection
					 */
					for(int i = 0; i < resultList.size(); i++) {
						IResultPCA pcaResult = resultList.get(i);
						IPoint pointResult = getPoint(pcaResult, pcX, pcY, i);
						if(pointResult.getX() > pXStart && pointResult.getX() < pXStop && pointResult.getY() < pYStart && pointResult.getY() > pYStop) {
							sampleSelected.add(resultList.get(i));
						}
					}
					/*
					 * Send Update event.
					 */
					if(!sampleSelected.isEmpty()) {
						UpdateNotifierUI.update(event.display, IChemClipseEvents.TOPIC_PCA_UPDATE_RESULT, sampleSelected.toArray());
					}
					/*
					 * Finish User Selection Process
					 */
					userSelection.reset();
					userSelection.setSingleClick(false);
				}
			}
		});
		scorePlot.applySettings(chartSettings);
		/*
		 * Paint Listener
		 */
		scorePlot.getBaseChart().getPlotArea().addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				if(userSelection.isActive()) {
					int x = Math.min(userSelection.getStartX(), userSelection.getStopX());
					int y = Math.min(userSelection.getStartY(), userSelection.getStopY());
					int width = Math.abs(userSelection.getStopX() - userSelection.getStartX());
					int height = Math.abs(userSelection.getStopY() - userSelection.getStartY());
					//
					GC gc = e.gc;
					gc.setBackground(Colors.RED);
					gc.setForeground(Colors.DARK_RED);
					gc.setAlpha(45);
					gc.fillRectangle(x, y, width, height);
					gc.setLineStyle(SWT.LINE_DASH);
					gc.setLineWidth(2);
					gc.drawRectangle(x, y, width, height);
				}
			}
		});
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