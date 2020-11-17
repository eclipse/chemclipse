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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePeakTraces;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.ISeriesSet;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.core.SeriesStatusAdapter;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class ExtendedPeakTracesUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedPeakTracesUI.class);
	//
	private static final String TRACE_LABEL_NONE = "--";
	private static final String TRACE_LABEL_PREFIX = "Trace";
	private static final String CATEGORY = "Peak Trace(s)";
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private ComboViewer comboViewerTraces;
	private Button buttonDeleteTrace;
	private AtomicReference<PeakTracesUI> chartControl = new AtomicReference<>();
	//
	private IPeak peak = null;
	private final PeakDataSupport peakDataSupport = new PeakDataSupport();

	@Inject
	public ExtendedPeakTracesUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Focus
	public boolean setFocus() {

		update(peak);
		return true;
	}

	public void update(IPeak peak) {

		this.peak = peak;
		updateLabel();
		updatePeak();
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		setLayout(gridLayout);
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createPeakChart(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(7, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		comboViewerTraces = createComboViewerTraces(composite);
		buttonDeleteTrace = createButtonDeleteTrace(composite);
		createButtonCopyTracesClipboard(composite);
		createButtonToggleChartLegend(composite, chartControl, IMAGE_LEGEND);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private ComboViewer createComboViewerTraces(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof String) {
					return (String)element;
				} else if(element instanceof Integer) {
					return TRACE_LABEL_PREFIX + " " + Integer.toString((int)element);
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a trace.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				//
				PeakTracesUI peakTracesUI = chartControl.get();
				if(object instanceof String) {
					peakTracesUI.deselectTrace();
					updatePeak();
				} else if(object instanceof Integer) {
					int selectedTrace = (int)object;
					enableDeleteButton(true);
					peakTracesUI.setSelectedTrace(selectedTrace);
					peakTracesUI.updateChart();
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonDeleteTrace(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete the selected trace.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewerTraces.getStructuredSelection().getFirstElement();
				if(object instanceof Integer) {
					Set<Integer> traces = new HashSet<>();
					traces.add((int)object);
					deleteTraces(e.display.getActiveShell(), traces);
					updatePeak();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonCopyTracesClipboard(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Copy the traces to clipboard.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COPY_CLIPBOARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<Integer> traces = chartControl.get().getTraces();
				Iterator<Integer> iterator = traces.iterator();
				StringBuilder builder = new StringBuilder();
				//
				while(iterator.hasNext()) {
					builder.append(iterator.next());
					if(iterator.hasNext()) {
						builder.append(" ");
					}
				}
				//
				TextTransfer textTransfer = TextTransfer.getInstance();
				Object[] data = new Object[]{builder.toString()};
				Transfer[] dataTypes = new Transfer[]{textTransfer};
				Clipboard clipboard = new Clipboard(e.widget.getDisplay());
				clipboard.setContents(data, dataTypes);
			}
		});
		//
		return button;
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updatePeak();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePagePeakTraces.class, PreferencePageScans.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void createPeakChart(Composite parent) {

		PeakTracesUI peakTracesUI = new PeakTracesUI(parent, SWT.BORDER);
		peakTracesUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		BaseChart baseChart = peakTracesUI.getBaseChart();
		baseChart.addSeriesStatusListener(new SeriesStatusAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void handleSeriesSelectionEvent(String seriesId) {

				if(!"".equals(seriesId)) {
					Object input = comboViewerTraces.getInput();
					if(input instanceof List) {
						List list = (List)input;
						exitloop:
						for(int i = 0; i < list.size(); i++) {
							if(list.get(i).toString().equals(seriesId)) {
								selectComboSeries(i);
								break exitloop;
							}
						}
					}
				}
			}

			@Override
			public void handleSeriesHideEvent(String seriesId) {

				selectComboSeries(0);
			}

			@Override
			public void handleSeriesResetEvent(String seriesId) {

				if(BaseChart.SELECTED_SERIES_NONE.equals(seriesId)) {
					selectComboSeries(0);
				}
			}
		});
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = peakTracesUI.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setShowLegendMarker(false);
		chartSettings.addMenuEntry(createMenuDeleteHiddenSeries());
		chartSettings.addMenuEntry(createMenuDeleteSelectedSeries());
		peakTracesUI.applySettings(chartSettings);
		//
		chartControl.set(peakTracesUI);
	}

	private IChartMenuEntry createMenuDeleteHiddenSeries() {

		IChartMenuEntry menuEntry = new IChartMenuEntry() {

			@Override
			public String getName() {

				return "Delete Hidden Serie(s)";
			}

			@Override
			public String getCategory() {

				return CATEGORY;
			}

			@Override
			public void execute(Shell shell, ScrollableChart scrollableChart) {

				if(peak != null) {
					deleteTraces(shell, getHiddenTraces(scrollableChart));
					updatePeak();
				}
			}
		};
		//
		return menuEntry;
	}

	private IChartMenuEntry createMenuDeleteSelectedSeries() {

		IChartMenuEntry menuEntry = new IChartMenuEntry() {

			@Override
			public String getName() {

				return "Delete Selected Serie(s)";
			}

			@Override
			public String getCategory() {

				return CATEGORY;
			}

			@Override
			public void execute(Shell shell, ScrollableChart scrollableChart) {

				if(peak != null) {
					deleteTraces(shell, getSelectedTraces(scrollableChart));
					updatePeak();
				}
			}
		};
		//
		return menuEntry;
	}

	private Set<Integer> getHiddenTraces(ScrollableChart scrollableChart) {

		Set<Integer> traces = new HashSet<>();
		//
		ISeriesSet seriesSet = scrollableChart.getBaseChart().getSeriesSet();
		for(ISeries<?> series : seriesSet.getSeries()) {
			if(!series.isVisible()) {
				try {
					int trace = Integer.parseInt(series.getId());
					traces.add(trace);
				} catch(NumberFormatException e) {
					logger.warn(e);
				}
			}
		}
		//
		return traces;
	}

	private Set<Integer> getSelectedTraces(ScrollableChart scrollableChart) {

		Set<Integer> traces = new HashSet<>();
		//
		BaseChart baseChart = scrollableChart.getBaseChart();
		Set<String> selectedSeriesIds = baseChart.getSelectedSeriesIds();
		for(String seriesId : selectedSeriesIds) {
			try {
				int trace = Integer.parseInt(seriesId);
				traces.add(trace);
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
		}
		//
		return traces;
	}

	private void deleteTraces(Shell shell, Set<Integer> traces) {

		if(peak instanceof IPeakMSD) {
			IPeakMSD peakMSD = (IPeakMSD)peak;
			IPeakModelMSD peakModelMSD = peakMSD.getPeakModel();
			IScanMSD scanMSD = peakModelMSD.getPeakMassSpectrum();
			int maxDeleteIons = scanMSD.getNumberOfIons() - 1;
			if(traces.size() >= maxDeleteIons) {
				MessageDialog.openInformation(shell, CATEGORY, "It's not possible to delete all ions.");
			} else {
				scanMSD.removeIons(traces);
			}
		} else if(peak instanceof IPeakWSD) {
			MessageDialog.openInformation(shell, CATEGORY, "Handling DAD peak traces is not implemented yet.");
		} else if(peak instanceof IPeakCSD) {
			MessageDialog.openInformation(shell, CATEGORY, "It's not possible to delete a FID peak.");
		}
	}

	private void applySettings() {

		updatePeak();
	}

	private void updateLabel() {

		toolbarInfo.get().setText(peakDataSupport.getPeakLabel(peak));
	}

	private void updatePeak() {

		chartControl.get().setInput(peak);
		updateComboTraces();
		enableDeleteButton(false);
	}

	private void updateComboTraces() {

		List<Object> input = new ArrayList<>();
		input.add(TRACE_LABEL_NONE);
		input.addAll(chartControl.get().getTraces());
		comboViewerTraces.setInput(input);
		comboViewerTraces.getCombo().select(0);
	}

	private void enableDeleteButton(boolean enabled) {

		buttonDeleteTrace.setEnabled(enabled);
	}

	private void selectComboSeries(int index) {

		comboViewerTraces.getCombo().select(index);
		enableDeleteButton(index > 0 ? true : false);
	}
}
