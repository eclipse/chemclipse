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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.ChromatogramFilterCSD;
import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.IChromatogramFilterSupportCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.IPeakIdentifierSupplierCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.IPeakIdentifierSupportCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.PeakIdentifierCSD;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.IPeakDetectorCSDSupplier;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.IPeakDetectorCSDSupport;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.PeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupplier;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupport;
import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.core.ChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.core.IChromatogramClassifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.core.IChromatogramClassifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.NoChromatogramClassifierSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupportMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupplierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupportMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.IPeakDetectorMSDSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.IPeakDetectorMSDSupport;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.IPeakQuantifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.IPeakQuantifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.PeakQuantifier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.exceptions.NoPeakQuantifierAvailableException;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.NoPeakDetectorAvailableException;
import org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram.ChromatogramFilterWSD;
import org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram.IChromatogramFilterSupportWSD;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.IPeakDetectorWSDSupplier;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.IPeakDetectorWSDSupport;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.PeakDetectorWSD;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.ChromatogramCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.IChromatogramCalculatorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.IChromatogramCalculatorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.exceptions.NoChromatogramCalculatorSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupport;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.implementation.ComparisonResult;
import org.eclipse.chemclipse.model.implementation.LibraryInformation;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.IdentificationLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ChromatogramLengthModifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.RetentionTimeValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramPeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramActionUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.RetentionIndexTableViewerUI;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.eavp.service.swtchart.axisconverter.MillisecondsToScanNumberConverter;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IExtendedChart;
import org.eclipse.eavp.service.swtchart.core.ISecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.RangeRestriction;
import org.eclipse.eavp.service.swtchart.core.SecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.menu.IChartMenuEntry;
import org.eclipse.eavp.service.swtchart.menu.ResetChartHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.swtchart.IAxis.Position;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.LineStyle;

@SuppressWarnings("rawtypes")
public class ExtendedChromatogramUI {

	private static final Logger logger = Logger.getLogger(ExtendedChromatogramUI.class);
	//
	protected static final String TYPE_GENERIC = "TYPE_GENERIC";
	protected static final String TYPE_MSD = "TYPE_MSD";
	protected static final String TYPE_CSD = "TYPE_CSD";
	protected static final String TYPE_WSD = "TYPE_WSD";
	//
	private static final String TITLE_X_AXIS_SCANS = "Scans (approx.)";
	private static final String LABEL_SCAN_NUMBER = "Scan Number";
	//
	private static final String SERIES_ID_CHROMATOGRAM = "Chromatogram";
	private static final String SERIES_ID_BASELINE = "Baseline";
	private static final String SERIES_ID_PEAKS_NORMAL_ACTIVE = "Peak(s) [Active]";
	private static final String SERIES_ID_PEAKS_NORMAL_INACTIVE = "Peak(s) [Inactive]";
	private static final String SERIES_ID_PEAKS_ISTD_ACTIVE = "Peak(s) ISTD [Active]";
	private static final String SERIES_ID_PEAKS_ISTD_INACTIVE = "Peak(s) ISTD [Inactive]";
	private static final String SERIES_ID_SELECTED_PEAK_MARKER = "Selected Peak Marker";
	private static final String SERIES_ID_SELECTED_PEAK_SHAPE = "Selected Peak Shape";
	private static final String SERIES_ID_SELECTED_PEAK_BACKGROUND = "Selected Peak Background";
	private static final String SERIES_ID_SELECTED_SCAN = "Selected Scan";
	private static final String SERIES_ID_IDENTIFIED_SCANS = "Identified Scans";
	private static final String SERIES_ID_IDENTIFIED_SCAN_SELECTED = "Identified Scan Selected";
	//
	private static final String MODIFY_LENGTH_SHORTEST = "MODIFY_LENGTH_SHORTEST";
	private static final String MODIFY_LENGTH_SELECTED = "MODIFY_LENGTH_SELECTED";
	private static final String MODIFY_LENGTH_LONGEST = "MODIFY_LENGTH_LONGEST";
	private static final String MODIFY_LENGTH_ADJUST = "MODIFY_LENGTH_ADJUST";
	//
	private static final int THREE_MINUTES = (int)(AbstractChromatogram.MINUTE_CORRELATION_FACTOR * 3);
	private static final int FIVE_MINUTES = (int)(AbstractChromatogram.MINUTE_CORRELATION_FACTOR * 5);
	//
	private Composite toolbarMain;
	private Composite toolbarInfo;
	private Label labelChromatogramInfo;
	private Composite toolbarRetentionIndices;
	private RetentionIndexTableViewerUI retentionIndexTableViewerUI;
	private Composite toolbarMethod;
	private Combo comboChromatograms;
	private Composite toolbarEdit;
	private ChromatogramChart chromatogramChart;
	private Combo comboTargetTransfer;
	private ComboViewer comboViewerSeparationColumn;
	private MethodSupportUI methodSupportUI;
	private ChromatogramActionUI chromatogramActionUI;
	//
	private IChromatogramSelection chromatogramSelection = null;
	private List<IChromatogramSelection> referencedChromatogramSelections = null; // Might be null ... no references.
	private List<IChromatogramSelection> targetChromatogramSelections = new ArrayList<IChromatogramSelection>(); // Is filled dynamically.
	//
	private List<IChartMenuEntry> chartMenuEntriesCalculators = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesClassifier = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesFilter = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakDetectors = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakIntegrators = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakIdentifier = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakQuantifier = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesReports = new ArrayList<IChartMenuEntry>();
	//
	private Map<String, IdentificationLabelMarker> peakLabelMarkerMap = new HashMap<String, IdentificationLabelMarker>();
	private Map<String, IdentificationLabelMarker> scanLabelMarkerMap = new HashMap<String, IdentificationLabelMarker>();
	//
	private PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	private PeakChartSupport peakChartSupport = new PeakChartSupport();
	private ScanChartSupport scanChartSupport = new ScanChartSupport();
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private PeakDataSupport peakDataSupport = new PeakDataSupport();
	//
	private boolean suspendUpdate = false;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	@Inject
	public ExtendedChromatogramUI(Composite parent, int style) {
		initialize(parent, style);
	}

	public void setToolbarVisible(boolean visible) {

		PartSupport.setCompositeVisibility(toolbarMain, visible);
	}

	public void fireUpdate() {

		fireUpdateChromatogram();
		if(!fireUpdatePeak()) {
			fireUpdateScan();
		}
	}

	public boolean fireUpdateChromatogram() {

		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null) {
			//
			DisplayUtils.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_LOAD_CHROMATOGRAM_SELECTION, chromatogramSelection);
				}
			});
			//
			final Map<String, Object> map = new HashMap<>();
			map.put(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION, chromatogramSelection);
			map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, true);
			final String topic;
			//
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION;
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION;
			} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
				topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION;
			} else {
				topic = null;
			}
			//
			if(topic != null) {
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
						eventBroker.post(topic, map);
					}
				});
			}
		}
		return chromatogramSelection != null ? true : false;
	}

	public boolean fireUpdatePeak() {

		boolean update = false;
		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null) {
			final IPeak peak = chromatogramSelection.getSelectedPeak();
			if(peak != null) {
				//
				update = true;
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
						eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, peak);
					}
				});
				//
				final Map<String, Object> map = new HashMap<>();
				map.put(IChemClipseEvents.PROPERTY_PEAK_MSD, peak);
				map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, true);
				final String topic;
				//
				if(peak instanceof IPeakMSD) {
					topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAK;
				} else if(peak instanceof IPeakCSD) {
					topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_PEAK;
				} else if(peak instanceof IPeakWSD) {
					topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_PEAK;
				} else {
					topic = null;
				}
				//
				if(topic != null) {
					DisplayUtils.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {

							IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
							eventBroker.post(topic, map);
						}
					});
				}
			}
		}
		return update;
	}

	public boolean fireUpdateScan() {

		boolean update = false;
		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null) {
			final IScan scan = chromatogramSelection.getSelectedScan();
			if(scan != null) {
				update = true;
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
						eventBroker.post(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, scan);
					}
				});
			}
		}
		return update;
	}

	public ChromatogramChart getChromatogramChart() {

		return chromatogramChart;
	}

	public synchronized void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		chromatogramActionUI.setChromatogramActionMenu(chromatogramSelection);
		//
		if(chromatogramSelection != null) {
			/*
			 * Adjust
			 */
			adjustAxisSettings();
			addChartMenuEntries();
			updateChromatogram();
			if(referencedChromatogramSelections == null) {
				updateChromatogramCombo();
				updateChromatogramTargetTransferCombo();
			} else {
				updateChromatogramTargetTransferCombo();
			}
			setSeparationColumnSelection();
			retentionIndexTableViewerUI.setInput(chromatogramSelection.getChromatogram().getSeparationColumnIndices());
		} else {
			comboChromatograms.setItems(new String[0]);
			retentionIndexTableViewerUI.setInput(null);
			updateChromatogram();
		}
	}

	public void updateChromatogramTargetTransferSelections() {

		updateChromatogramTargetTransferCombo();
	}

	public void update() {

		if(!suspendUpdate) {
			updateChromatogramTargetTransferSelections();
			updateChromatogram();
			adjustChromatogramSelectionRange();
			setSeparationColumnSelection();
		}
	}

	public void updateSelectedScan() {

		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_SCAN);
		chromatogramChart.deleteSeries(SERIES_ID_IDENTIFIED_SCAN_SELECTED);
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		addSelectedScanData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);
		addLineSeriesData(lineSeriesDataList);
		adjustChromatogramSelectionRange();
	}

	public void updateSelectedPeak() {

		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_PEAK_MARKER);
		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_PEAK_SHAPE);
		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_PEAK_BACKGROUND);
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		addSelectedPeakData(lineSeriesDataList);
		addLineSeriesData(lineSeriesDataList);
		adjustChromatogramSelectionRange();
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

	protected void setChromatogramSelectionRange(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) {

		chromatogramSelection.setRanges(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance, false);
		suspendUpdate = true;
		chromatogramSelection.update(true);
		suspendUpdate = false;
		adjustChromatogramSelectionRange();
	}

	protected void updateSelection() {

		if(chromatogramSelection != null) {
			chromatogramSelection.update(true);
			adjustChromatogramSelectionRange();
		}
	}

	protected void processChromatogram(IRunnableWithProgress runnable) {

		/*
		 * Excecute
		 */
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(DisplayUtils.getShell());
		try {
			monitor.run(true, true, runnable);
			updateChromatogram();
			updateSelection();
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void adjustMinuteScale() {

		if(chromatogramSelection != null) {
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			int deltaRetentionTime = stopRetentionTime - startRetentionTime + 1;
			IChartSettings chartSettings = chromatogramChart.getChartSettings();
			List<ISecondaryAxisSettings> axisSettings = chartSettings.getSecondaryAxisSettingsListX();
			for(ISecondaryAxisSettings axisSetting : axisSettings) {
				if(axisSetting.getTitle().equals("Minutes")) {
					if(deltaRetentionTime >= FIVE_MINUTES) {
						axisSetting.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
					} else if(deltaRetentionTime >= THREE_MINUTES) {
						axisSetting.setDecimalFormat(new DecimalFormat(("0.000"), new DecimalFormatSymbols(Locale.ENGLISH)));
					} else {
						axisSetting.setDecimalFormat(new DecimalFormat(("0.0000"), new DecimalFormatSymbols(Locale.ENGLISH)));
					}
				}
			}
			chromatogramChart.applySettings(chartSettings);
		}
	}

	private void addChartMenuEntries() {

		addChartMenuEntriesCalculators();
		addChartMenuEntriesClassifier();
		addChartMenuEntriesFilter();
		addChartMenuEntriesPeakDetectors();
		addChartMenuEntriesPeakIntegrators();
		addChartMenuEntriesPeakIdentifier();
		addChartMenuEntriesPeakQuantifier();
		addChartMenuEntriesReport();
	}

	private void addChartMenuEntriesCalculators() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesCalculators);
		//
		if(chromatogramSelection != null) {
			if(chromatogramSelection instanceof IChromatogramSelection) {
				addChartMenuEntriesCalculators(chartSettings);
			}
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChartMenuEntriesCalculators(IChartSettings chartSettings) {

		try {
			IChromatogramCalculatorSupport chromatogramCalculatorSupport = ChromatogramCalculator.getChromatogramCalculatorSupport();
			for(String calculatorId : chromatogramCalculatorSupport.getAvailableCalculatorIds()) {
				IChromatogramCalculatorSupplier calculator = chromatogramCalculatorSupport.getCalculatorSupplier(calculatorId);
				String name = calculator.getCalculatorName();
				CalculatorMenuEntry calculatorMenuEntry = new CalculatorMenuEntry(this, name, calculatorId, TYPE_GENERIC, chromatogramSelection);
				chartMenuEntriesCalculators.add(calculatorMenuEntry);
				chartSettings.addMenuEntry(calculatorMenuEntry);
			}
		} catch(NoChromatogramCalculatorSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesClassifier() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesClassifier);
		//
		if(chromatogramSelection != null) {
			/*
			 * MSD
			 */
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				addChartMenuEntriesClassifierMSD(chartSettings);
			}
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChartMenuEntriesClassifierMSD(IChartSettings chartSettings) {

		try {
			IChromatogramClassifierSupport chromatogramClassifierSupport = ChromatogramClassifier.getChromatogramClassifierSupport();
			for(String filterId : chromatogramClassifierSupport.getAvailableClassifierIds()) {
				IChromatogramClassifierSupplier classifier = chromatogramClassifierSupport.getClassifierSupplier(filterId);
				String name = classifier.getClassifierName();
				ClassifierMenuEntry classifierMenuEntry = new ClassifierMenuEntry(this, name, filterId, TYPE_MSD, chromatogramSelection);
				chartMenuEntriesClassifier.add(classifierMenuEntry);
				chartSettings.addMenuEntry(classifierMenuEntry);
			}
		} catch(NoChromatogramClassifierSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesFilter() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesFilter);
		//
		if(chromatogramSelection != null) {
			/*
			 * Generic
			 */
			addChartMenuEntriesFilter(chartSettings);
			/*
			 * Specific
			 */
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				addChartMenuEntriesFilterMSD(chartSettings);
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				addChartMenuEntriesFilterCSD(chartSettings);
			} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
				addChartMenuEntriesFilterWSD(chartSettings);
			}
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChartMenuEntriesFilter(IChartSettings chartSettings) {

		try {
			IChromatogramFilterSupport chromatogramFilterSupport = ChromatogramFilter.getChromatogramFilterSupport();
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(this, name, filterId, TYPE_GENERIC, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesFilterMSD(IChartSettings chartSettings) {

		try {
			IChromatogramFilterSupportMSD chromatogramFilterSupport = ChromatogramFilterMSD.getChromatogramFilterSupport();
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(this, name, filterId, TYPE_MSD, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesFilterCSD(IChartSettings chartSettings) {

		try {
			IChromatogramFilterSupportCSD chromatogramFilterSupport = ChromatogramFilterCSD.getChromatogramFilterSupport();
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(this, name, filterId, TYPE_CSD, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesFilterWSD(IChartSettings chartSettings) {

		try {
			IChromatogramFilterSupportWSD chromatogramFilterSupport = ChromatogramFilterWSD.getChromatogramFilterSupport();
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(this, name, filterId, TYPE_WSD, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakDetectors() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesPeakDetectors);
		//
		if(chromatogramSelection != null) {
			/*
			 * Specific
			 */
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				addChartMenuEntriesPeakDetectorMSD(chartSettings);
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				addChartMenuEntriesPeakDetectorCSD(chartSettings);
			} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
				addChartMenuEntriesPeakDetectorWSD(chartSettings);
			}
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChartMenuEntriesPeakDetectorMSD(IChartSettings chartSettings) {

		try {
			IPeakDetectorMSDSupport peakDetectorSupport = PeakDetectorMSD.getPeakDetectorSupport();
			for(String peakDetectorId : peakDetectorSupport.getAvailablePeakDetectorIds()) {
				IPeakDetectorMSDSupplier peakDetecorSupplier = peakDetectorSupport.getPeakDetectorSupplier(peakDetectorId);
				String name = peakDetecorSupplier.getPeakDetectorName();
				PeakDetectorMenuEntry menuEntry = new PeakDetectorMenuEntry(this, name, peakDetectorId, TYPE_MSD, chromatogramSelection);
				chartMenuEntriesPeakDetectors.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoPeakDetectorAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakDetectorCSD(IChartSettings chartSettings) {

		try {
			IPeakDetectorCSDSupport peakDetectorSupport = PeakDetectorCSD.getPeakDetectorSupport();
			for(String peakDetectorId : peakDetectorSupport.getAvailablePeakDetectorIds()) {
				IPeakDetectorCSDSupplier peakDetecorSupplier = peakDetectorSupport.getPeakDetectorSupplier(peakDetectorId);
				String name = peakDetecorSupplier.getPeakDetectorName();
				PeakDetectorMenuEntry menuEntry = new PeakDetectorMenuEntry(this, name, peakDetectorId, TYPE_CSD, chromatogramSelection);
				chartMenuEntriesPeakDetectors.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoPeakDetectorAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakDetectorWSD(IChartSettings chartSettings) {

		try {
			IPeakDetectorWSDSupport peakDetectorSupport = PeakDetectorWSD.getPeakDetectorSupport();
			for(String peakDetectorId : peakDetectorSupport.getAvailablePeakDetectorIds()) {
				IPeakDetectorWSDSupplier peakDetecorSupplier = peakDetectorSupport.getPeakDetectorSupplier(peakDetectorId);
				String name = peakDetecorSupplier.getPeakDetectorName();
				PeakDetectorMenuEntry menuEntry = new PeakDetectorMenuEntry(this, name, peakDetectorId, TYPE_WSD, chromatogramSelection);
				chartMenuEntriesPeakDetectors.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoPeakDetectorAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakIntegrators() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesPeakIntegrators);
		//
		if(chromatogramSelection != null) {
			/*
			 * Generic
			 */
			addChartMenuEntriesPeakIntegrator(chartSettings);
		}
	}

	private void addChartMenuEntriesPeakIntegrator(IChartSettings chartSettings) {

		try {
			IPeakIntegratorSupport peakIntegratorSupport = PeakIntegrator.getPeakIntegratorSupport();
			for(String peakIntegratorId : peakIntegratorSupport.getAvailableIntegratorIds()) {
				IPeakIntegratorSupplier peakIntegratorSupplier = peakIntegratorSupport.getIntegratorSupplier(peakIntegratorId);
				String name = peakIntegratorSupplier.getIntegratorName();
				PeakIntegratorMenuEntry menuEntry = new PeakIntegratorMenuEntry(this, name, peakIntegratorId, TYPE_GENERIC, chromatogramSelection);
				chartMenuEntriesPeakIntegrators.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoIntegratorAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakIdentifier() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesPeakIdentifier);
		//
		if(chromatogramSelection != null) {
			/*
			 * Specific
			 */
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				addChartMenuEntriesPeakIdentifierMSD(chartSettings);
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				addChartMenuEntriesPeakIdentifierCSD(chartSettings);
			}
		}
	}

	private void addChartMenuEntriesPeakIdentifierMSD(IChartSettings chartSettings) {

		try {
			IPeakIdentifierSupportMSD peakIdentifierSupport = PeakIdentifierMSD.getPeakIdentifierSupport();
			for(String peakIdentifierId : peakIdentifierSupport.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplierMSD peakIdentifierSupplier = peakIdentifierSupport.getIdentifierSupplier(peakIdentifierId);
				String name = peakIdentifierSupplier.getIdentifierName();
				PeakIdentifierMenuEntry menuEntry = new PeakIdentifierMenuEntry(this, name, peakIdentifierId, TYPE_MSD, chromatogramSelection);
				chartMenuEntriesPeakIdentifier.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoIdentifierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakIdentifierCSD(IChartSettings chartSettings) {

		try {
			IPeakIdentifierSupportCSD peakIdentifierSupport = PeakIdentifierCSD.getPeakIdentifierSupport();
			for(String peakIdentifierId : peakIdentifierSupport.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplierCSD peakIdentifierSupplier = peakIdentifierSupport.getIdentifierSupplier(peakIdentifierId);
				String name = peakIdentifierSupplier.getIdentifierName();
				PeakIdentifierMenuEntry menuEntry = new PeakIdentifierMenuEntry(this, name, peakIdentifierId, TYPE_CSD, chromatogramSelection);
				chartMenuEntriesPeakIdentifier.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoIdentifierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakQuantifier() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesPeakQuantifier);
		//
		if(chromatogramSelection != null) {
			/*
			 * Generic
			 */
			addChartMenuEntriesPeakQuantifier(chartSettings);
		}
	}

	private void addChartMenuEntriesPeakQuantifier(IChartSettings chartSettings) {

		try {
			IPeakQuantifierSupport peakQuantifierSupport = PeakQuantifier.getPeakQuantifierSupport();
			for(String peakQuantifierId : peakQuantifierSupport.getAvailablePeakQuantifierIds()) {
				IPeakQuantifierSupplier peakQuantifierSupplier = peakQuantifierSupport.getPeakQuantifierSupplier(peakQuantifierId);
				String name = peakQuantifierSupplier.getPeakQuantifierName();
				PeakQuantifierMenuEntry menuEntry = new PeakQuantifierMenuEntry(this, name, peakQuantifierId, TYPE_GENERIC, chromatogramSelection);
				chartMenuEntriesPeakQuantifier.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoPeakQuantifierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesReport() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesReports);
		//
		if(chromatogramSelection != null) {
			/*
			 * Generic
			 */
			addChartMenuEntriesReport(chartSettings);
		}
	}

	private void addChartMenuEntriesReport(IChartSettings chartSettings) {

		IChromatogramReportSupport chromatogramReportSupport = ChromatogramReports.getChromatogramReportSupplierSupport();
		for(IChromatogramReportSupplier chromatogramReportSupplier : chromatogramReportSupport.getReportSupplier()) {
			ReportMenuEntry menuEntry = new ReportMenuEntry(this, chromatogramReportSupplier, TYPE_GENERIC);
			chartMenuEntriesReports.add(menuEntry);
			chartSettings.addMenuEntry(menuEntry);
		}
	}

	private void cleanChartMenuEntries(IChartSettings chartSettings, List<IChartMenuEntry> chartMenuEntries) {

		for(IChartMenuEntry chartMenuEntry : chartMenuEntries) {
			chartSettings.removeMenuEntry(chartMenuEntry);
		}
		chartMenuEntries.clear();
	}

	private void updateChromatogram() {

		updateLabel();
		clearPeakAndScanLabels();
		adjustMinuteScale();
		deleteScanNumberSecondaryAxisX();
		chromatogramChart.deleteSeries();
		//
		if(chromatogramSelection != null) {
			addjustChromatogramChart();
			addChromatogramSeriesData();
			addScanNumberSecondaryAxisX();
			adjustChromatogramSelectionRange();
		}
	}

	private void clearPeakAndScanLabels() {

		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_NORMAL_ACTIVE);
		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_NORMAL_INACTIVE);
		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_ISTD_ACTIVE);
		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_ISTD_INACTIVE);
		removeIdentificationLabelMarker(scanLabelMarkerMap, SERIES_ID_IDENTIFIED_SCANS);
		//
		clearLabelMarker(peakLabelMarkerMap);
		clearLabelMarker(scanLabelMarkerMap);
	}

	private void clearLabelMarker(Map<String, IdentificationLabelMarker> markerMap) {

		for(IdentificationLabelMarker identificationLabelMarker : markerMap.values()) {
			identificationLabelMarker.clear();
		}
		markerMap.clear();
	}

	private void removeIdentificationLabelMarker(Map<String, IdentificationLabelMarker> markerMap, String seriesId) {

		IPlotArea plotArea = (IPlotArea)chromatogramChart.getBaseChart().getPlotArea();
		IdentificationLabelMarker labelMarker = markerMap.get(seriesId);
		/*
		 * Remove the label marker.
		 */
		if(labelMarker != null) {
			plotArea.removeCustomPaintListener(labelMarker);
		}
	}

	private void addjustChromatogramChart() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setForceZeroMinY(false);
		/*
		 * Add space on top to show labels correctly.
		 */
		double extendX = preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_EXTEND_X);
		rangeRestriction.setExtendMaxY(extendX);
		/*
		 * MSD has no negative intensity values, so setZeroY(true)
		 */
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			rangeRestriction.setZeroY(true);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			rangeRestriction.setZeroY(false);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			rangeRestriction.setZeroY(false);
		}
		/*
		 * Zooming
		 */
		rangeRestriction.setXZoomOnly(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_X_ZOOM_ONLY));
		rangeRestriction.setYZoomOnly(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_Y_ZOOM_ONLY));
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChromatogramSeriesData() {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		//
		addChromatogramData(lineSeriesDataList);
		addPeakData(lineSeriesDataList);
		addIdentifiedScansData(lineSeriesDataList);
		addSelectedPeakData(lineSeriesDataList);
		addSelectedScanData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);
		addBaselineData(lineSeriesDataList);
		addLineSeriesData(lineSeriesDataList);
	}

	private void addChromatogramData(List<ILineSeriesData> lineSeriesDataList) {

		Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM));
		boolean enableChromatogramArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_CHROMATOGRAM_AREA);
		//
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesDataChromatogram(chromatogram, SERIES_ID_CHROMATOGRAM, color);
		lineSeriesData.getLineSeriesSettings().setEnableArea(enableChromatogramArea);
		lineSeriesDataList.add(lineSeriesData);
	}

	private void addPeakData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			//
			List<? extends IPeak> peaks = chromatogramDataSupport.getPeaks(chromatogram);
			List<IPeak> peaksActiveNormal = new ArrayList<IPeak>();
			List<IPeak> peaksInactiveNormal = new ArrayList<IPeak>();
			List<IPeak> peaksActiveISTD = new ArrayList<IPeak>();
			List<IPeak> peaksInactiveISTD = new ArrayList<IPeak>();
			//
			for(IPeak peak : peaks) {
				if(peak.getInternalStandards().size() > 0) {
					if(peak.isActiveForAnalysis()) {
						peaksActiveISTD.add(peak);
					} else {
						peaksInactiveISTD.add(peak);
					}
				} else {
					if(peak.isActiveForAnalysis()) {
						peaksActiveNormal.add(peak);
					} else {
						peaksInactiveNormal.add(peak);
					}
				}
			}
			//
			addPeaks(lineSeriesDataList, peaksActiveNormal, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, Colors.DARK_GRAY, SERIES_ID_PEAKS_NORMAL_ACTIVE);
			addPeaks(lineSeriesDataList, peaksInactiveNormal, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_NORMAL_INACTIVE);
			addPeaks(lineSeriesDataList, peaksActiveISTD, PlotSymbolType.DIAMOND, symbolSize, Colors.RED, SERIES_ID_PEAKS_ISTD_ACTIVE);
			addPeaks(lineSeriesDataList, peaksInactiveISTD, PlotSymbolType.DIAMOND, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_ISTD_INACTIVE);
		}
	}

	private void addPeaks(List<ILineSeriesData> lineSeriesDataList, List<IPeak> peaks, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(peaks.size() > 0) {
			//
			Collections.sort(peaks, peakRetentionTimeComparator);
			ILineSeriesData lineSeriesData = peakChartSupport.getPeaks(peaks, true, false, symbolColor, seriesId);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setEnableArea(false);
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(plotSymbolType);
			lineSeriesSettings.setSymbolSize(symbolSize);
			lineSeriesSettings.setSymbolColor(symbolColor);
			lineSeriesDataList.add(lineSeriesData);
			/*
			 * Add the labels.
			 */
			removeIdentificationLabelMarker(peakLabelMarkerMap, seriesId);
			boolean showChromatogramPeakLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS);
			if(showChromatogramPeakLabels) {
				IPlotArea plotArea = (IPlotArea)chromatogramChart.getBaseChart().getPlotArea();
				int indexSeries = lineSeriesDataList.size() - 1;
				IdentificationLabelMarker peakLabelMarker = new IdentificationLabelMarker(chromatogramChart.getBaseChart(), indexSeries, peaks, null);
				plotArea.addCustomPaintListener(peakLabelMarker);
				peakLabelMarkerMap.put(seriesId, peakLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			String seriesId = SERIES_ID_IDENTIFIED_SCANS;
			List<IScan> scans = chromatogramDataSupport.getIdentifiedScans(chromatogramSelection.getChromatogram());
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
			addIdentifiedScansData(lineSeriesDataList, scans, PlotSymbolType.CIRCLE, symbolSize, Colors.DARK_GRAY, seriesId);
			/*
			 * Add the labels.
			 */
			removeIdentificationLabelMarker(scanLabelMarkerMap, seriesId);
			boolean showChromatogramScanLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS);
			if(showChromatogramScanLabels) {
				IPlotArea plotArea = (IPlotArea)chromatogramChart.getBaseChart().getPlotArea();
				int indexSeries = lineSeriesDataList.size() - 1;
				IdentificationLabelMarker scanLabelMarker = new IdentificationLabelMarker(chromatogramChart.getBaseChart(), indexSeries, null, scans);
				plotArea.addCustomPaintListener(scanLabelMarker);
				scanLabelMarkerMap.put(seriesId, scanLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList, List<IScan> scans, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(scans.size() > 0) {
			ILineSeriesData lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scans, false, seriesId);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(plotSymbolType);
			lineSeriesSettings.setSymbolSize(symbolSize);
			lineSeriesSettings.setSymbolColor(symbolColor);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addSelectedIdentifiedScanData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			IScan scan = chromatogramSelection.getSelectedIdentifiedScan();
			if(scan != null) {
				String seriesId = SERIES_ID_IDENTIFIED_SCAN_SELECTED;
				Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN_IDENTIFIED));
				int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
				List<IScan> scans = new ArrayList<>();
				scans.add(scan);
				addIdentifiedScansData(lineSeriesDataList, scans, PlotSymbolType.CIRCLE, symbolSize, color, seriesId);
			}
		}
	}

	private void addSelectedPeakData(List<ILineSeriesData> lineSeriesDataList) {

		IPeak peak = chromatogramSelection.getSelectedPeak();
		if(peak != null) {
			/*
			 * Settings
			 */
			boolean mirrored = false;
			ILineSeriesData lineSeriesData;
			Color colorPeak = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_PEAK));
			/*
			 * Peak Marker
			 */
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			List<IPeak> peaks = new ArrayList<>();
			peaks.add(peak);
			addPeaks(lineSeriesDataList, peaks, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, colorPeak, SERIES_ID_SELECTED_PEAK_MARKER);
			/*
			 * Peak
			 */
			int markerSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE));
			lineSeriesData = peakChartSupport.getPeak(peak, true, mirrored, colorPeak, SERIES_ID_SELECTED_PEAK_SHAPE);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setSymbolType(symbolType);
			lineSeriesSettings.setSymbolColor(colorPeak);
			lineSeriesSettings.setSymbolSize(markerSize);
			lineSeriesDataList.add(lineSeriesData);
			/*
			 * Background
			 */
			Color colorBackground = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_BACKGROUND));
			lineSeriesData = peakChartSupport.getPeakBackground(peak, mirrored, colorBackground, SERIES_ID_SELECTED_PEAK_BACKGROUND);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addSelectedScanData(List<ILineSeriesData> lineSeriesDataList) {

		IScan scan = chromatogramSelection.getSelectedScan();
		if(scan != null) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN));
			int markerSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE));
			ILineSeriesData lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scan, false, SERIES_ID_SELECTED_SCAN);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(symbolType);
			lineSeriesSettings.setSymbolSize(markerSize);
			lineSeriesSettings.setSymbolColor(color);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addBaselineData(List<ILineSeriesData> lineSeriesDataList) {

		boolean showChromatogramBaseline = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_BASELINE);
		//
		if(chromatogramSelection != null && showChromatogramBaseline) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_BASELINE));
			boolean enableBaselineArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_BASELINE_AREA);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesDataBaseline(chromatogram, SERIES_ID_BASELINE, color);
			lineSeriesData.getLineSeriesSettings().setEnableArea(enableBaselineArea);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addLineSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		/*
		 * Define the compression level.
		 */
		String compressionType = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE);
		int compressionToLength = chromatogramChartSupport.getCompressionLength(compressionType, lineSeriesDataList.size());
		chromatogramChart.addSeriesData(lineSeriesDataList, compressionToLength);
	}

	private void initialize(Composite parent, int style) {

		parent.setLayout(new GridLayout(1, true));
		//
		toolbarMain = createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarRetentionIndices = createToolbarRetentionIndices(parent);
		toolbarMethod = createToolbarMethod(parent);
		toolbarEdit = createToolbarEdit(parent);
		createChromatogramChart(parent, style);
		//
		comboViewerSeparationColumn.setInput(SeparationColumnFactory.getSeparationColumns());
		//
		PartSupport.setCompositeVisibility(toolbarMain, true);
		PartSupport.setCompositeVisibility(toolbarInfo, false);
		PartSupport.setCompositeVisibility(toolbarRetentionIndices, false);
		PartSupport.setCompositeVisibility(toolbarMethod, false);
		PartSupport.setCompositeVisibility(toolbarEdit, false);
		//
		chromatogramActionUI.setChromatogramActionMenu(chromatogramSelection);
	}

	private Composite createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(11, false));
		//
		createButtonToggleToolbarInfo(composite);
		comboViewerSeparationColumn = createComboViewerSeparationColumn(composite);
		createButtonToggleToolbarRetentionIndices(composite);
		createButtonToggleToolbarMethod(composite);
		createButtonToggleToolbarEdit(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createToggleRangeSelectorButton(composite);
		chromatogramActionUI = createChromatogramActionUI(composite);
		createResetButton(composite);
		createSettingsButton(composite);
		//
		return composite;
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

	private Composite createToolbarRetentionIndices(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		retentionIndexTableViewerUI = new RetentionIndexTableViewerUI(composite, SWT.BORDER);
		retentionIndexTableViewerUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		return composite;
	}

	private Composite createToolbarMethod(Composite parent) {

		methodSupportUI = new MethodSupportUI(parent, SWT.NONE);
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener(new IMethodListener() {

			@Override
			public void execute() {

			}
		});
		//
		return methodSupportUI;
	}

	private ComboViewer createComboViewerSeparationColumn(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ISeparationColumn) {
					ISeparationColumn separationColumn = (ISeparationColumn)element;
					return separationColumn.getName();
				}
				return null;
			}
		});
		combo.setToolTipText("Select a chromatogram column.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ISeparationColumn && chromatogramSelection != null) {
					ISeparationColumn separationColumn = (ISeparationColumn)object;
					chromatogramSelection.getChromatogram().getSeparationColumnIndices().setSeparationColumn(separationColumn);
					updateLabel();
				}
			}
		});
		//
		return comboViewer;
	}

	private Composite createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(15, false));
		//
		createButtonSelectPreviousChromatogram(composite);
		createComboChromatograms(composite);
		createButtonSelectNextChromatogram(composite);
		createVerticalSeparator(composite);
		createComboTargetTransfer(composite);
		createTextTargetDelta(composite);
		createCheckBoxTransferTargets(composite);
		createButtonTransferTargets(composite);
		createVerticalSeparator(composite);
		createButtonShrinkChromatograms(composite);
		createButtonAlignChromatograms(composite);
		createButtonStretchChromatograms(composite);
		createButtonAdjustChromatograms(composite);
		createVerticalSeparator(composite);
		createButtonSetRanges(composite);
		//
		return composite;
	}

	private void createComboTargetTransfer(Composite parent) {

		comboTargetTransfer = new Combo(parent, SWT.READ_ONLY);
		comboTargetTransfer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboTargetTransfer.setToolTipText("Select the chromatogram sink for target transfer.");
	}

	private void createTextTargetDelta(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText(Double.toString(preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME)));
		text.setToolTipText("Delta retention time in minutes.");
		GridData gridData = new GridData();
		gridData.widthHint = 100;
		text.setLayoutData(gridData);
		//
		RetentionTimeValidator retentionTimeValidator = new RetentionTimeValidator();
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * It crashes when the control decoration is created earlier in case
				 * more than one chromatogram is opened simultaneously via the
				 * open selected files button in the supplier file explorer.
				 */
				ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
				if(validate(retentionTimeValidator, controlDecoration, text)) {
					preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, retentionTimeValidator.getRetentionTime());
				}
			}
		});
	}

	private boolean validate(IValidator validator, ControlDecoration controlDecoration, Text text) {

		IStatus status = validator.validate(text.getText());
		if(status.isOK()) {
			controlDecoration.hide();
			return true;
		} else {
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(status.getMessage());
			controlDecoration.show();
			return false;
		}
	}

	private void createCheckBoxTransferTargets(Composite parent) {

		Button checkBox = new Button(parent, SWT.CHECK);
		checkBox.setText("Best Target Only");
		checkBox.setSelection(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY));
		checkBox.setToolTipText("Transfer only the best matching target.");
		checkBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY, checkBox.getSelection());
			}
		});
	}

	private void createButtonTransferTargets(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Transfer the targets from this chromatogram to the selected chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				transferPeakTargets();
			}
		});
	}

	private void createButtonShrinkChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Shrink the chromatograms to the smallest chromatogram of all open editors.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHRINK_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyChromatogramLength(MODIFY_LENGTH_SHORTEST);
			}
		});
	}

	private void createButtonAlignChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Align the chromatograms to the length of the selection.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ALIGN_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyChromatogramLength(MODIFY_LENGTH_SELECTED);
			}
		});
	}

	private void createButtonStretchChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Stretch the chromatograms to the widest chromatogram of all open editors.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STRETCH_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyChromatogramLength(MODIFY_LENGTH_LONGEST);
			}
		});
	}

	private void createButtonAdjustChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Adjust the chromatograms using the settings");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADJUST_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyChromatogramLength(MODIFY_LENGTH_ADJUST);
			}
		});
	}

	private void modifyChromatogramLength(String modifyLengthType) {

		MessageBox messageBox = new MessageBox(DisplayUtils.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Modify Chromatogram Length");
		messageBox.setMessage("Would you like to modify the length of all opened chromatograms? Peaks will be deleted.");
		if(messageBox.open() == SWT.YES) {
			IChromatogram chromatogram = getChromatogram(modifyLengthType);
			if(chromatogram != null) {
				/*
				 * Settings
				 */
				int scanDelay;
				int chromatogramLength;
				if(MODIFY_LENGTH_ADJUST.equals(modifyLengthType)) {
					scanDelay = preferenceStore.getInt(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY);
					chromatogramLength = preferenceStore.getInt(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH);
				} else {
					scanDelay = chromatogram.getScanDelay();
					chromatogramLength = chromatogram.getStopRetentionTime();
					preferenceStore.setValue(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY, scanDelay);
					preferenceStore.setValue(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH, chromatogramLength);
				}
				/*
				 * Modify chromatograms.
				 */
				for(IChromatogramSelection chromatogramSelection : targetChromatogramSelections) {
					if(realignChromatogram(modifyLengthType, chromatogramSelection, chromatogram)) {
						IRunnableWithProgress runnable = new ChromatogramLengthModifier(chromatogramSelection, scanDelay, chromatogramLength);
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(DisplayUtils.getShell());
						try {
							monitor.run(true, true, runnable);
						} catch(InvocationTargetException e) {
							logger.warn(e);
						} catch(InterruptedException e) {
							logger.warn(e);
						}
					}
					/*
					 * Refresh the master chromatogram on length change.
					 */
					if(!MODIFY_LENGTH_SELECTED.equals(modifyLengthType)) {
						update();
					}
				}
			}
		}
	}

	private boolean realignChromatogram(String modifyLengthType, IChromatogramSelection chromatogramSelection, IChromatogram chromatogram) {

		/*
		 * Don't re-align the template chromatogram.
		 */
		if(MODIFY_LENGTH_ADJUST.equals(modifyLengthType)) {
			return true;
		} else {
			if(chromatogramSelection.getChromatogram() != chromatogram) {
				return true;
			}
			return false;
		}
	}

	private IChromatogram getChromatogram(String modifyLengthType) {

		IChromatogram chromatogram;
		switch(modifyLengthType) {
			case MODIFY_LENGTH_SHORTEST:
				chromatogram = getShortestChromatogram();
				break;
			case MODIFY_LENGTH_SELECTED:
			case MODIFY_LENGTH_ADJUST:
				chromatogram = chromatogramSelection.getChromatogram();
				break;
			case MODIFY_LENGTH_LONGEST:
				chromatogram = getLongestChromatogram();
				break;
			default:
				chromatogram = null;
				break;
		}
		return chromatogram;
	}

	/**
	 * May return null.
	 * 
	 * @return IChromatogram
	 */
	private IChromatogram getShortestChromatogram() {

		IChromatogram chromatogram = null;
		int maxRetentionTime = Integer.MAX_VALUE;
		for(IChromatogramSelection chromatogramSelection : targetChromatogramSelections) {
			if(chromatogramSelection.getChromatogram().getStopRetentionTime() < maxRetentionTime) {
				maxRetentionTime = chromatogramSelection.getChromatogram().getStopRetentionTime();
				chromatogram = chromatogramSelection.getChromatogram();
			}
		}
		return chromatogram;
	}

	/**
	 * May return null.
	 * 
	 * @return IChromatogram
	 */
	private IChromatogram getLongestChromatogram() {

		IChromatogram chromatogram = null;
		int minRetentionTime = Integer.MIN_VALUE;
		for(IChromatogramSelection chromatogramSelection : targetChromatogramSelections) {
			if(chromatogramSelection.getChromatogram().getStopRetentionTime() > minRetentionTime) {
				minRetentionTime = chromatogramSelection.getChromatogram().getStopRetentionTime();
				chromatogram = chromatogramSelection.getChromatogram();
			}
		}
		return chromatogram;
	}

	private void createButtonSetRanges(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Set the time range for all editors.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setRanges();
				MessageDialog.openInformation(DisplayUtils.getShell(), "Range Selection", "The selected editor range has been set successfully to all opened chromatograms.");
			}
		});
	}

	private void setRanges() {

		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		float startAbundance = chromatogramSelection.getStartAbundance();
		float stopAbundance = chromatogramSelection.getStopAbundance();
		boolean setChromatogramIntensityRange = preferenceStore.getBoolean(PreferenceConstants.P_SET_CHROMATOGRAM_INTENSITY_RANGE);
		/*
		 * Editor
		 */
		for(IChromatogramSelection selection : targetChromatogramSelections) {
			if(selection != chromatogramSelection) {
				/*
				 * Don't fire an update. The next time the selection is on focus,
				 * the correct range will be loaded.
				 * selection.fireUpdateChange(true);
				 */
				selection.setStartRetentionTime(startRetentionTime);
				selection.setStopRetentionTime(stopRetentionTime);
				if(setChromatogramIntensityRange) {
					selection.setStartAbundance(startAbundance);
					selection.setStopAbundance(stopAbundance);
				}
			}
		}
	}

	private void transferPeakTargets() {

		int index = comboTargetTransfer.getSelectionIndex();
		if(index >= 0 && index < targetChromatogramSelections.size()) {
			IChromatogramSelection selection = targetChromatogramSelections.get(index);
			if(selection != null && selection != chromatogramSelection) {
				/*
				 * Question
				 */
				MessageBox messageBox = new MessageBox(DisplayUtils.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setText("Transfer Peak Target(s)");
				messageBox.setMessage("Would you like to transfer the selected peak target(s) to chromatogram: " + selection.getChromatogram().getName() + "?");
				if(messageBox.open() == SWT.YES) {
					/*
					 * Transfer the peak targets.
					 */
					int retentionTimeDelta = (int)(preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					boolean useBestTargetOnly = preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY);
					//
					List<? extends IPeak> peaksSource = chromatogramDataSupport.getPeaks(chromatogramSelection, true);
					List<? extends IPeak> peaksSink = chromatogramDataSupport.getPeaks(selection, false);
					/*
					 * Are peaks available?
					 */
					if(peaksSource.size() > 0) {
						if(peaksSink.size() > 0) {
							/*
							 * Transfer OK
							 */
							for(IPeak peakSink : peaksSink) {
								for(IPeak peakSource : peaksSource) {
									int retentionTimePeakSink = peakSink.getPeakModel().getRetentionTimeAtPeakMaximum();
									int retentionTimePeakSource = peakSource.getPeakModel().getRetentionTimeAtPeakMaximum();
									if(isPeakInFocus(retentionTimePeakSink, retentionTimePeakSource, retentionTimeDelta)) {
										/*
										 * Best target or all?
										 */
										if(useBestTargetOnly) {
											IIdentificationTarget peakTarget = peakDataSupport.getBestPeakTarget(peakSource.getTargets());
											transferPeakTarget(peakTarget, peakSink);
										} else {
											for(IPeakTarget peakTarget : peakSource.getTargets()) {
												transferPeakTarget(peakTarget, peakSink);
											}
										}
									}
								}
							}
						} else {
							MessageDialog.openWarning(DisplayUtils.getShell(), "Transfer Peak Target(s)", "The sink chromatogram contains no peaks.");
						}
					} else {
						MessageDialog.openWarning(DisplayUtils.getShell(), "Transfer Peak Target(s)", "The source chromatogram contains no peaks.");
					}
				}
			} else {
				MessageDialog.openWarning(DisplayUtils.getShell(), "Transfer Peak Target(s)", "It's not possible to transfer targets to the same chromatogram.");
			}
		} else {
			MessageDialog.openWarning(DisplayUtils.getShell(), "Transfer Peak Target(s)", "Please select a chromatogram.");
		}
	}

	private void transferPeakTarget(IIdentificationTarget identificationTargetSource, IPeak peakSink) {

		ILibraryInformation libraryInformation = new LibraryInformation(identificationTargetSource.getLibraryInformation());
		IComparisonResult comparisonResult = new ComparisonResult(identificationTargetSource.getComparisonResult());
		IPeakTarget peakTargetSink = new PeakTarget(libraryInformation, comparisonResult);
		peakSink.addTarget(peakTargetSink);
	}

	private boolean isPeakInFocus(int retentionTimePeakSink, int retentionTimePeakSource, int retentionTimeDelta) {

		if(retentionTimePeakSink >= (retentionTimePeakSource - retentionTimeDelta) && retentionTimePeakSink <= (retentionTimePeakSource + retentionTimeDelta)) {
			return true;
		} else {
			return false;
		}
	}

	private void createVerticalSeparator(Composite parent) {

		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gridData = new GridData();
		gridData.heightHint = 35;
		label.setLayoutData(gridData);
	}

	private void createButtonSelectPreviousChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select previous chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboChromatograms.getSelectionIndex();
				index--;
				index = (index < 0) ? 0 : index;
				selectChromatogram(index);
			}
		});
	}

	private void createComboChromatograms(Composite parent) {

		comboChromatograms = new Combo(parent, SWT.READ_ONLY);
		comboChromatograms.setToolTipText("Select a referenced chromatogram.");
		comboChromatograms.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboChromatograms.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(referencedChromatogramSelections != null) {
					int index = comboChromatograms.getSelectionIndex();
					selectChromatogram(index);
					chromatogramActionUI.setChromatogramActionMenu(chromatogramSelection);
					fireUpdate();
				}
			}
		});
	}

	private void createButtonSelectNextChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select next chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboChromatograms.getSelectionIndex();
				index++;
				index = (index >= comboChromatograms.getItemCount()) ? comboChromatograms.getItemCount() - 1 : index;
				selectChromatogram(index);
			}
		});
	}

	private void selectChromatogram(int index) {

		comboChromatograms.select(index);
		if(referencedChromatogramSelections != null) {
			IChromatogramSelection chromatogramSelection = referencedChromatogramSelections.get(index);
			if(chromatogramSelection != null) {
				updateChromatogramSelection(chromatogramSelection);
			}
		}
	}

	private void updateChromatogramCombo() {

		referencedChromatogramSelections = new ArrayList<IChromatogramSelection>();
		List<String> references = new ArrayList<String>();
		/*
		 * Get the original and the referenced data.
		 */
		if(chromatogramSelection != null) {
			referencedChromatogramSelections.addAll(getChromatogramReferences(chromatogramSelection));
			references.addAll(getChromatogramReferences(referencedChromatogramSelections));
		}
		/*
		 * Set the items.
		 */
		comboChromatograms.setItems(references.toArray(new String[references.size()]));
		if(references.size() > 0) {
			comboChromatograms.select(0);
		}
	}

	private void updateChromatogramTargetTransferCombo() {

		targetChromatogramSelections = new ArrayList<>();
		List<String> references = new ArrayList<String>();
		/*
		 * Get the editor and reference chromatograms.
		 */
		List<IChromatogramSelection> editorChromatogramSelections = new ArrayList<IChromatogramSelection>();
		if(chromatogramSelection != null) {
			for(IChromatogramSelection editorChromatogramSelection : editorUpdateSupport.getChromatogramSelections()) {
				if(editorChromatogramSelection.getChromatogram() != chromatogramSelection.getChromatogram()) {
					editorChromatogramSelections.add(editorChromatogramSelection);
				}
			}
		}
		//
		targetChromatogramSelections.addAll(editorChromatogramSelections);
		references.addAll(getEditorReferences(editorChromatogramSelections));
		//
		if(referencedChromatogramSelections != null) {
			targetChromatogramSelections.addAll(referencedChromatogramSelections);
			references.addAll(getChromatogramReferences(referencedChromatogramSelections));
		}
		/*
		 * Set the items.
		 */
		comboTargetTransfer.setItems(references.toArray(new String[references.size()]));
		if(references.size() > 0) {
			comboTargetTransfer.select(0);
		}
	}

	@SuppressWarnings("unchecked")
	private List<IChromatogramSelection> getChromatogramReferences(IChromatogramSelection chromatogramSelection) {

		/*
		 * Current selection / Reference
		 */
		List<IChromatogramSelection> chromatogramSelections = new ArrayList<IChromatogramSelection>();
		chromatogramSelections.add(chromatogramSelection);
		//
		List<IChromatogram> referencedChromatograms = chromatogramSelection.getChromatogram().getReferencedChromatograms();
		for(IChromatogram referencedChromatogram : referencedChromatograms) {
			if(referencedChromatogram != chromatogramSelection.getChromatogram()) {
				IChromatogramSelection referencedChromatogramSelection = null;
				try {
					if(referencedChromatogram instanceof IChromatogramMSD) {
						referencedChromatogramSelection = new ChromatogramSelectionMSD(referencedChromatogram);
					} else if(referencedChromatogram instanceof IChromatogramCSD) {
						referencedChromatogramSelection = new ChromatogramSelectionCSD(referencedChromatogram);
					} else if(referencedChromatogram instanceof IChromatogramWSD) {
						referencedChromatogramSelection = new ChromatogramSelectionWSD(referencedChromatogram);
					}
					//
					if(referencedChromatogramSelection != null) {
						chromatogramSelections.add(referencedChromatogramSelection);
					}
				} catch(ChromatogramIsNullException e) {
					logger.warn(e);
				}
			}
		}
		//
		return chromatogramSelections;
	}

	private List<String> getChromatogramReferences(List<IChromatogramSelection> referencedChromatogramSelections) {

		List<String> references = new ArrayList<String>();
		//
		int i = 1;
		for(IChromatogramSelection referencedChromatogramSelection : referencedChromatogramSelections) {
			/*
			 * Get the label.
			 */
			String type = chromatogramDataSupport.getChromatogramType(referencedChromatogramSelection);
			if(referencedChromatogramSelection != null) {
				if(chromatogramSelection == referencedChromatogramSelection) {
					references.add("Current Chromatogram " + type);
				} else {
					references.add("Chromatogram" + ChromatogramChartSupport.REFERENCE_MARKER + i++ + " " + type);
				}
			}
		}
		//
		return references;
	}

	private List<String> getEditorReferences(List<IChromatogramSelection> editorChromatogramSelections) {

		List<String> references = new ArrayList<String>();
		//
		int index = 1;
		for(IChromatogramSelection targetChromatogramSelection : targetChromatogramSelections) {
			String type = chromatogramDataSupport.getChromatogramType(targetChromatogramSelection);
			references.add(targetChromatogramSelection.getChromatogram().getName() + type + " " + "(Tab#: " + index++ + ")");
		}
		//
		return references;
	}

	private void createChromatogramChart(Composite parent, int style) {

		chromatogramChart = new ChromatogramChart(parent, style);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Custom Selection Handler
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		baseChart.addCustomRangeSelectionHandler(new ChromatogramSelectionHandler(this));
		/*
		 * Update
		 */
		baseChart.getPlotArea().addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {

				updateChromatogramTargetTransferCombo();
			}
		});
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setRangeSelectorDefaultAxisX(1); // Minutes
		chartSettings.setRangeSelectorDefaultAxisY(1); // Relative Abundance
		chartSettings.setShowRangeSelectorInitially(false);
		IChartMenuEntry chartMenuEntry = chartSettings.getChartMenuEntry(ResetChartHandler.NAME);
		chartSettings.removeMenuEntry(chartMenuEntry);
		chartSettings.addMenuEntry(new ChromatogramResetHandler(this));
		chartSettings.addHandledEventProcessor(new ScanSelectionHandler(this));
		chartSettings.addHandledEventProcessor(new PeakSelectionHandler(this));
		chartSettings.addHandledEventProcessor(new ScanSelectionArrowKeyHandler(this, SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new ScanSelectionArrowKeyHandler(this, SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_UP));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_DOWN));
		//
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

	private Button createButtonToggleToolbarRetentionIndices(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle retention indices toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RETENION_INDEX, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarRetentionIndices);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RETENION_INDEX, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RETENION_INDEX, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chromatogram method toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarMethod);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarEdit(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle edit toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarEdit);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
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

				chromatogramChart.togglePositionLegendVisibility();
				chromatogramChart.redraw();
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

	private ChromatogramActionUI createChromatogramActionUI(Composite parent) {

		ChromatogramActionUI chromatogramActionUI = new ChromatogramActionUI(parent, SWT.NONE);
		return chromatogramActionUI;
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chromatogram");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset(true);
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
				preferencePageChromatogram.setTitle("Chromatogram Settings");
				IPreferencePage preferencePageChromatogramPeaks = new PreferencePageChromatogramPeaks();
				preferencePageChromatogramPeaks.setTitle("Chromatogram Peaks");
				IPreferencePage preferencePageChromatogramScans = new PreferencePageChromatogramScans();
				preferencePageChromatogramScans.setTitle("Chromatogram Scans");
				IPreferencePage preferencePageSWT = new PreferencePageSWT();
				preferencePageSWT.setTitle("Settings (SWT)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageChromatogram));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageChromatogramPeaks));
				preferenceManager.addToRoot(new PreferenceNode("3", preferencePageChromatogramScans));
				preferenceManager.addToRoot(new PreferenceNode("4", preferencePageSWT));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.widget.getDisplay().getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		adjustAxisSettings();
		updateChromatogram();
	}

	private void reset(boolean resetRange) {

		updateChromatogram();
		if(resetRange && chromatogramSelection != null) {
			chromatogramSelection.reset(true);
		}
	}

	private void updateLabel() {

		if(chromatogramSelection != null) {
			labelChromatogramInfo.setText(chromatogramDataSupport.getChromatogramLabelExtended(chromatogramSelection.getChromatogram()));
		} else {
			labelChromatogramInfo.setText("");
		}
	}

	private void deleteScanNumberSecondaryAxisX() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		List<ISecondaryAxisSettings> secondaryAxisSettings = chartSettings.getSecondaryAxisSettingsListX();
		//
		ISecondaryAxisSettings secondaryAxisScanNumber = null;
		exitloop:
		for(ISecondaryAxisSettings secondaryAxis : secondaryAxisSettings) {
			if(secondaryAxis.getLabel().equals(LABEL_SCAN_NUMBER)) {
				secondaryAxisScanNumber = secondaryAxis;
				break exitloop;
			}
		}
		//
		if(secondaryAxisScanNumber != null) {
			secondaryAxisSettings.remove(secondaryAxisScanNumber);
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addScanNumberSecondaryAxisX() {

		boolean showChromatogramScanAxis = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_AXIS);
		//
		try {
			deleteScanNumberSecondaryAxisX();
			IChartSettings chartSettings = chromatogramChart.getChartSettings();
			if(chromatogramSelection != null && showChromatogramScanAxis) {
				//
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				int scanDelay = chromatogram.getScanDelay();
				int scanInterval = chromatogram.getScanInterval();
				//
				ISecondaryAxisSettings secondaryAxisSettings = new SecondaryAxisSettings(LABEL_SCAN_NUMBER, new MillisecondsToScanNumberConverter(scanDelay, scanInterval));
				secondaryAxisSettings.setPosition(Position.Secondary);
				secondaryAxisSettings.setDecimalFormat(ValueFormat.getDecimalFormatEnglish("0"));
				secondaryAxisSettings.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				secondaryAxisSettings.setExtraSpaceTitle(0);
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettings);
			}
			//
			chromatogramChart.applySettings(chartSettings);
			chromatogramChart.adjustRange(true);
			chromatogramChart.redraw();
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	private void adjustChromatogramSelectionRange() {

		if(chromatogramSelection != null) {
			chromatogramChart.setRange(IExtendedChart.X_AXIS, chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime());
			chromatogramChart.setRange(IExtendedChart.Y_AXIS, chromatogramSelection.getStartAbundance(), chromatogramSelection.getStopAbundance());
		}
	}

	private void setSeparationColumnSelection() {

		if(chromatogramSelection != null) {
			ISeparationColumn separationColumn = chromatogramSelection.getChromatogram().getSeparationColumnIndices().getSeparationColumn();
			if(separationColumn != null) {
				String name = separationColumn.getName();
				int index = -1;
				exitloop:
				for(String item : comboViewerSeparationColumn.getCombo().getItems()) {
					index++;
					if(item.equals(name)) {
						break exitloop;
					}
				}
				//
				if(index >= 0) {
					comboViewerSeparationColumn.getCombo().select(index);
				}
			}
		}
	}

	private void adjustAxisSettings() {

		chromatogramChart.modifyAxisSet(false);
		/*
		 * Scan Axis
		 */
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				ISecondaryAxisSettings axisSettings = chromatogramChart.getSecondaryAxisSettingsX(TITLE_X_AXIS_SCANS);
				if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_SCANS)) {
					Position position = Position.valueOf(preferenceStore.getString(PreferenceConstants.P_POSITION_X_AXIS_SCANS));
					if(axisSettings == null) {
						try {
							int scanDelay = chromatogram.getScanDelay();
							int scanInterval = chromatogram.getScanInterval();
							ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(TITLE_X_AXIS_SCANS, new MillisecondsToScanNumberConverter(scanDelay, scanInterval));
							secondaryAxisSettingsX.setPosition(position);
							secondaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
							secondaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
							chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
						} catch(Exception e) {
							logger.warn(e);
						}
					} else {
						axisSettings.setPosition(Position.valueOf(preferenceStore.getString(PreferenceConstants.P_POSITION_X_AXIS_SCANS)));
					}
				} else {
					/*
					 * Remove
					 */
					if(axisSettings != null) {
						chartSettings.getSecondaryAxisSettingsListX().remove(axisSettings);
					}
				}
				chromatogramChart.applySettings(chartSettings);
			}
		}
	}
}
