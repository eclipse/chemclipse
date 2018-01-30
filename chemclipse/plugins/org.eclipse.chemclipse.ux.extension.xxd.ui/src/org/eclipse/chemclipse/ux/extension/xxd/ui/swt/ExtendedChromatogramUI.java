/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.ChromatogramFilterCSD;
import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.IChromatogramFilterSupportCSD;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupplier;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupport;
import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupportMSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.PeakLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.ScrollableChart;
import org.eclipse.eavp.service.swtchart.customcharts.ChromatogramChart;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.eavp.service.swtchart.menu.AbstractChartMenuEntry;
import org.eclipse.eavp.service.swtchart.menu.IChartMenuEntry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.LineStyle;

public class ExtendedChromatogramUI {

	private static final Logger logger = Logger.getLogger(ExtendedChromatogramUI.class);
	//
	private static final String TYPE_GENERIC = "TYPE_GENERIC";
	private static final String TYPE_MSD = "TYPE_MSD";
	private static final String TYPE_CSD = "TYPE_CSD";
	private static final String TYPE_WSD = "TYPE_WSD";
	//
	private Composite toolbarInfo;
	private Label labelChromatogramInfo;
	private Composite toolbarReferencedChromatograms;
	private Combo comboReferencedChromatograms;
	private ChromatogramChart chromatogramChart;
	//
	private IChromatogramSelection chromatogramSelection;
	private PeakLabelMarker peakLabelMarker;
	//
	private List<IChartMenuEntry> chartMenuEntriesFilter;
	//
	private PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	private PeakChartSupport peakChartSupport = new PeakChartSupport();
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private Shell shell = Display.getDefault().getActiveShell();

	private class FilterMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

		private String name;
		private String filterId;
		private String type;
		private IChromatogramSelection chromatogramSelection;

		public FilterMenuEntry(String name, String filterId, String type, IChromatogramSelection chromatogramSelection) {
			this.name = name;
			this.filterId = filterId;
			this.type = type;
			this.chromatogramSelection = chromatogramSelection;
		}

		@Override
		public String getCategory() {

			return "Filter";
		}

		@Override
		public String getName() {

			return name;
		}

		@Override
		public void execute(Shell shell, ScrollableChart scrollableChart) {

			if(chromatogramSelection != null) {
				IProgressMonitor monitor = new NullProgressMonitor();
				switch(type) {
					case TYPE_GENERIC:
						ChromatogramFilter.applyFilter(chromatogramSelection, filterId, monitor);
						chromatogramSelection.update(false);
						break;
					case TYPE_MSD:
						if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
							IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
							ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, filterId, monitor);
							chromatogramSelectionMSD.update(false);
						}
						break;
					case TYPE_CSD:
						if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
							IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
							ChromatogramFilterCSD.applyFilter(chromatogramSelectionCSD, filterId, monitor);
							chromatogramSelectionCSD.update(false);
						}
						break;
					case TYPE_WSD:
						//
						break;
				}
			}
		}
	}

	@Inject
	public ExtendedChromatogramUI(Composite parent) {
		chartMenuEntriesFilter = new ArrayList<IChartMenuEntry>();
		initialize(parent);
	}

	public void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		addChartMenuEntriesFilter();
		updateChromatogramSelection();
		updateReferencedChromatograms();
	}

	public void update() {

		chromatogramChart.update();
	}

	public IChromatogramSelection getChromatogramSelection() {

		return chromatogramSelection;
	}

	public boolean isActiveChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		if(this.chromatogramSelection == chromatogramSelection) {
			return true;
		}
		return false;
	}

	private void addChartMenuEntriesFilter() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntriesFilter(chartSettings);
		//
		if(chromatogramSelection != null) {
			/*
			 * Generic
			 */
			addChartMenuEntriesFilter(chartSettings, ChromatogramFilter.getChromatogramFilterSupport(), TYPE_GENERIC);
			/*
			 * Specific
			 */
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				addChartMenuEntriesFilterMSD(chartSettings, ChromatogramFilterMSD.getChromatogramFilterSupport(), TYPE_MSD);
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				addChartMenuEntriesFilterCSD(chartSettings, ChromatogramFilterCSD.getChromatogramFilterSupport(), TYPE_CSD);
			} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
				//
			}
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChartMenuEntriesFilter(IChartSettings chartSettings, IChromatogramFilterSupport chromatogramFilterSupport, String type) {

		try {
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(name, filterId, type, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	// TODO Refactor
	private void addChartMenuEntriesFilterMSD(IChartSettings chartSettings, IChromatogramFilterSupportMSD chromatogramFilterSupport, String type) {

		try {
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(name, filterId, type, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	// TODO Refactor
	private void addChartMenuEntriesFilterCSD(IChartSettings chartSettings, IChromatogramFilterSupportCSD chromatogramFilterSupport, String type) {

		try {
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(name, filterId, type, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void cleanChartMenuEntriesFilter(IChartSettings chartSettings) {

		for(IChartMenuEntry chartMenuEntry : chartMenuEntriesFilter) {
			chartSettings.removeMenuEntry(chartMenuEntry);
		}
		chartMenuEntriesFilter.clear();
	}

	private void updateChromatogramSelection() {

		updateLabel();
		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			chromatogramChart.deleteSeries();
		} else {
			addChromatogramSeriesData();
		}
	}

	private void addChromatogramSeriesData() {

		/*
		 * Show the chromatogram.
		 */
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		ILineSeriesData lineSeriesData;
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		/*
		 * TIC
		 */
		lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogramSelection, "Chromatogram", Colors.RED);
		lineSeriesData.getLineSeriesSettings().setEnableArea(true);
		lineSeriesDataList.add(lineSeriesData);
		/*
		 * Peaks
		 */
		List<? extends IPeak> peaks = new ArrayList<IPeak>();
		if(chromatogram instanceof IChromatogramMSD) {
			IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
			peaks = chromatogramMSD.getPeaks((IChromatogramSelectionMSD)chromatogramSelection);
		} else if(chromatogram instanceof IChromatogramCSD) {
			IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
			peaks = chromatogramCSD.getPeaks((IChromatogramSelectionCSD)chromatogramSelection);
		} else if(chromatogram instanceof IChromatogramWSD) {
			//
		}
		//
		Collections.sort(peaks, peakRetentionTimeComparator);
		ILineSeriesData peakSeriesData = peakChartSupport.getPeaks(peaks, true, false, Colors.BLACK, "Peaks");
		ILineSeriesSettings lineSeriesSettings = peakSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setEnableArea(false);
		lineSeriesSettings.setLineStyle(LineStyle.NONE);
		lineSeriesSettings.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
		lineSeriesSettings.setSymbolSize(5);
		lineSeriesSettings.setLineColor(Colors.GRAY);
		lineSeriesSettings.setSymbolColor(Colors.DARK_GRAY);
		lineSeriesDataList.add(peakSeriesData);
		//
		IPlotArea plotArea = (IPlotArea)chromatogramChart.getBaseChart().getPlotArea();
		plotArea.removeCustomPaintListener(peakLabelMarker);
		peakLabelMarker = new PeakLabelMarker(chromatogramChart.getBaseChart(), 1, peaks);
		plotArea.addCustomPaintListener(peakLabelMarker);
		/*
		 * Define the compression level.
		 */
		int compressionToLength;
		String compressionType = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE);
		switch(compressionType) {
			case LineChart.COMPRESSION_EXTREME:
				compressionToLength = LineChart.EXTREME_COMPRESSION;
				break;
			case LineChart.COMPRESSION_HIGH:
				compressionToLength = LineChart.HIGH_COMPRESSION;
				break;
			case LineChart.COMPRESSION_MEDIUM:
				compressionToLength = LineChart.MEDIUM_COMPRESSION;
				break;
			case LineChart.COMPRESSION_LOW:
				compressionToLength = LineChart.LOW_COMPRESSION;
				break;
			default:
				compressionToLength = LineChart.NO_COMPRESSION;
				break;
		}
		chromatogramChart.addSeriesData(lineSeriesDataList, compressionToLength);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarReferencedChromatograms = createToolbarReferencedChromatograms(parent);
		createChromatogramChart(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, false);
		PartSupport.setCompositeVisibility(toolbarReferencedChromatograms, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(7, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarReferencedChromatograms(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createToggleRangeSelectorButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelChromatogramInfo = new Label(composite, SWT.NONE);
		labelChromatogramInfo.setText("");
		labelChromatogramInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarReferencedChromatograms(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		comboReferencedChromatograms = new Combo(composite, SWT.READ_ONLY);
		comboReferencedChromatograms.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private void createChromatogramChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.BORDER);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setRangeSelectorDefaultAxisX(1); // Minutes
		chartSettings.setRangeSelectorDefaultAxisY(1); // Relative Abundance
		chartSettings.setShowRangeSelectorInitially(false);
		chromatogramChart.applySettings(chartSettings);
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

	private Button createButtonToggleToolbarReferencedChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle referenced chromatogram toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarReferencedChromatograms);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
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

				chromatogramChart.toggleSeriesLegendVisibility();
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

				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				boolean isShowLegendMarker = chartSettings.isShowLegendMarker();
				chartSettings.setShowLegendMarker(!isShowLegendMarker);
				chromatogramChart.applySettings(chartSettings);
			}
		});
	}

	private void createToggleRangeSelectorButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart range selector.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_RANGE_SELECTOR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.toggleRangeSelectorVisibility();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chromatogram");
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

				IPreferencePage preferencePageChromatogram = new PreferencePageChromatogram();
				preferencePageChromatogram.setTitle("Chromatogram Settings ");
				IPreferencePage preferencePageSWT = new PreferencePageSWT();
				preferencePageSWT.setTitle("Settings (SWT)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageChromatogram));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageSWT));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(shell, "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		updateChromatogramSelection();
	}

	private void reset() {

		updateChromatogramSelection();
	}

	private void updateLabel() {

		if(chromatogramSelection != null) {
			labelChromatogramInfo.setText(chromatogramDataSupport.getChromatogramLabel(chromatogramSelection.getChromatogram()));
		} else {
			labelChromatogramInfo.setText(chromatogramDataSupport.getChromatogramLabel(null));
		}
	}

	private void updateReferencedChromatograms() {

		List<String> references = new ArrayList<String>();
		if(chromatogramSelection != null) {
			List<IChromatogram> referencedChromatograms = chromatogramSelection.getChromatogram().getReferencedChromatograms();
			int i = 0;
			for(IChromatogram chromatogram : referencedChromatograms) {
				references.add("Chromatogram #" + (i + 1) + " " + chromatogram.getName());
				i++;
			}
		}
		//
		if(references.size() == 0) {
			references.add("No referenced chromatogram available.");
		}
		//
		comboReferencedChromatograms.setItems(references.toArray(new String[references.size()]));
	}
}
