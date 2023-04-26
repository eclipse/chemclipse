/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePeakTraces;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.chemclipse.wsd.model.core.IPeakModelWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
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
import org.eclipse.swt.graphics.Image;
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
	private static final String CATEGORY = "Peak Traces";
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private ComboViewer comboViewerTraces;
	private Button buttonDeleteTrace;
	private Button buttonCopyClipboard;
	private AtomicReference<PeakTracesUI> chartControl = new AtomicReference<>();
	//
	private IPeak peak = null;
	private final PeakDataSupport peakDataSupport = new PeakDataSupport();

	@Inject
	public ExtendedPeakTracesUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	@Focus
	public boolean setFocus() {

		DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
		List<Object> objects = dataUpdateSupport.getUpdates(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION);
		if(!objects.isEmpty()) {
			if(objects.get(0) instanceof IPeak last) {
				peak = last;
			}
		}
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
		buttonCopyClipboard = createButtonCopyTracesClipboard(composite);
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

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof String string) {
					return string;
				} else if(element instanceof Integer integer) {
					return TRACE_LABEL_PREFIX + " " + Integer.toString(integer);
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
					deleteTraces(e.display, traces);
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
				String content = builder.toString();
				if(!content.isEmpty()) {
					TextTransfer textTransfer = TextTransfer.getInstance();
					Object[] data = new Object[]{builder.toString()};
					Transfer[] dataTypes = new Transfer[]{textTransfer};
					Clipboard clipboard = new Clipboard(e.widget.getDisplay());
					clipboard.setContents(data, dataTypes);
				}
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

			@Override
			public void handleSeriesSelectionEvent(String seriesId) {

				if(!"".equals(seriesId)) {
					Object input = comboViewerTraces.getInput();
					if(input instanceof List list) {
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

		return new IChartMenuEntry() {

			@Override
			public String getName() {

				return "Delete Hidden Series";
			}

			@Override
			public String getCategory() {

				return CATEGORY;
			}

			@Override
			public void execute(Shell shell, ScrollableChart scrollableChart) {

				if(peak != null) {
					deleteTraces(scrollableChart.getDisplay(), getTraces(scrollableChart, true));
				}
			}
		};
	}

	private IChartMenuEntry createMenuDeleteSelectedSeries() {

		return new IChartMenuEntry() {

			@Override
			public String getName() {

				return "Delete Visible Series";
			}

			@Override
			public String getCategory() {

				return CATEGORY;
			}

			@Override
			public Image getIcon() {

				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImageProvider.SIZE_16x16);
			}

			@Override
			public void execute(Shell shell, ScrollableChart scrollableChart) {

				if(peak != null) {
					deleteTraces(scrollableChart.getDisplay(), getTraces(scrollableChart, false));
				}
			}
		};
	}

	private Set<Integer> getTraces(ScrollableChart scrollableChart, boolean useHidden) {

		Set<Integer> traces = new HashSet<>();
		//
		ISeriesSet seriesSet = scrollableChart.getBaseChart().getSeriesSet();
		for(ISeries<?> series : seriesSet.getSeries()) {
			int trace = getTrace(series.getId());
			if(trace != -1) {
				if(useHidden) {
					if(!series.isVisible()) {
						traces.add(trace);
					}
				} else if(series.isVisible()) {
					traces.add(trace);
				}
			}
		}
		//
		return traces;
	}

	private int getTrace(String id) {

		int trace = -1;
		try {
			trace = Integer.parseInt(id);
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		//
		return trace;
	}

	private void deleteTraces(Display display, Set<Integer> traces) {

		if(peak instanceof IPeakMSD peakMSD) {
			IPeakModelMSD peakModelMSD = peakMSD.getPeakModel();
			IScanMSD scanMSD = peakModelMSD.getPeakMassSpectrum();
			int maxDeleteTraces = scanMSD.getNumberOfIons() - 1;
			if(traces.size() >= maxDeleteTraces) {
				MessageDialog.openInformation(display.getActiveShell(), CATEGORY, "It's not possible to delete all ions.");
			} else {
				scanMSD.removeIons(traces);
				updatePeak();
				UpdateNotifierUI.update(display, peak);
			}
		} else if(peak instanceof IPeakWSD peakWSD) {
			IPeakModelWSD peakModelWSD = peakWSD.getPeakModel();
			IScan scan = peakModelWSD.getPeakMaximum();
			if(scan instanceof IScanWSD scanWSD) {
				int maxDeleteTraces = scanWSD.getNumberOfScanSignals() - 1;
				if(traces.size() >= maxDeleteTraces) {
					MessageDialog.openInformation(display.getActiveShell(), CATEGORY, "It's not possible to delete all wavelengths.");
				} else {
					scanWSD.removeScanSignals(traces);
					updatePeak();
					UpdateNotifierUI.update(display, peak);
				}
			}
		}
		if(peak instanceof IChromatogramPeak chromatogramPeak) {
			chromatogramPeak.getChromatogram().setDirty(true);
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
		//
		boolean enabled = peakIsEditable();
		comboViewerTraces.getCombo().setEnabled(enabled);
		buttonCopyClipboard.setEnabled(enabled);
	}

	private void updateComboTraces() {

		List<Object> input = new ArrayList<>();
		input.add(TRACE_LABEL_NONE);
		input.addAll(chartControl.get().getTraces());
		comboViewerTraces.setInput(input);
		comboViewerTraces.getCombo().select(0);
	}

	private void enableDeleteButton(boolean enabled) {

		buttonDeleteTrace.setEnabled(enabled && peakIsEditable());
	}

	private void selectComboSeries(int index) {

		comboViewerTraces.getCombo().select(index);
		enableDeleteButton(index > 0);
	}

	private boolean peakIsEditable() {

		return (peak instanceof IPeakMSD || peak instanceof IPeakWSD);
	}
}