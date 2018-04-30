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

import java.io.File;
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
import org.eclipse.chemclipse.chromatogram.msd.identifier.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.IPeakDetectorMSDSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.IPeakDetectorMSDSupport;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.NoPeakDetectorAvailableException;
import org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram.ChromatogramFilterWSD;
import org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram.IChromatogramFilterSupportWSD;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.IPeakDetectorWSDSupplier;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.IPeakDetectorWSDSupport;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.PeakDetectorWSD;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupport;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.implementation.ComparisonResult;
import org.eclipse.chemclipse.model.implementation.LibraryInformation;
import org.eclipse.chemclipse.model.selection.ChromatogramSelectionSupport;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.selection.MoveDirection;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.IdentificationLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ChromatogramLengthModifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.RetentionTimeValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.eavp.service.swtchart.axisconverter.MillisecondsToScanNumberConverter;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.ICustomSelectionHandler;
import org.eclipse.eavp.service.swtchart.core.IExtendedChart;
import org.eclipse.eavp.service.swtchart.core.ISecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.RangeRestriction;
import org.eclipse.eavp.service.swtchart.core.ScrollableChart;
import org.eclipse.eavp.service.swtchart.core.SecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.events.AbstractHandledEventProcessor;
import org.eclipse.eavp.service.swtchart.events.IHandledEventProcessor;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.eavp.service.swtchart.menu.AbstractChartMenuEntry;
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
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.swtchart.IAxis.Position;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.LineStyle;
import org.swtchart.Range;

public class ExtendedChromatogramUI {

	private static final Logger logger = Logger.getLogger(ExtendedChromatogramUI.class);
	//
	private static final String LABEL_SCAN_NUMBER = "Scan Number";
	//
	private static final String TYPE_GENERIC = "TYPE_GENERIC";
	private static final String TYPE_MSD = "TYPE_MSD";
	private static final String TYPE_CSD = "TYPE_CSD";
	private static final String TYPE_WSD = "TYPE_WSD";
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
	private Composite toolbarInfo;
	private Label labelChromatogramInfo;
	private Composite toolbarChromatograms;
	private Combo comboChromatograms;
	private Composite toolbarEdit;
	private ChromatogramChart chromatogramChart;
	private Combo comboTargetTransfer;
	//
	private IChromatogramSelection chromatogramSelection = null;
	private List<IChromatogramSelection> referencedChromatogramSelections = null; // Might be null ... no references.
	private List<IChromatogramSelection> targetChromatogramSelections = new ArrayList<IChromatogramSelection>(); // Is filled dynamically.
	//
	private List<IChartMenuEntry> chartMenuEntriesClassifier = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesFilter = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakDetectors = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakIntegrators = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakIdentifier = new ArrayList<IChartMenuEntry>();
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
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private Display display = Display.getDefault();
	private Shell shell = display.getActiveShell();

	private class ChromatogramResetHandler extends ResetChartHandler {

		@Override
		public void execute(Shell shell, ScrollableChart scrollableChart) {

			super.execute(shell, scrollableChart);
			if(chromatogramSelection != null) {
				chromatogramSelection.reset(true);
			}
		}
	}

	private class ChromatogramSelectionHandler implements ICustomSelectionHandler {

		private BaseChart baseChart;

		public ChromatogramSelectionHandler(BaseChart baseChart) {
			this.baseChart = baseChart;
		}

		@Override
		public void handleUserSelection(Event event) {

			if(chromatogramSelection != null) {
				/*
				 * Get the range.
				 */
				Range rangeX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
				Range rangeY = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
				//
				int startRetentionTime = (int)rangeX.lower;
				int stopRetentionTime = (int)rangeX.upper;
				float startAbundance = (float)rangeY.lower;
				float stopAbundance = (float)rangeY.upper;
				//
				setChromatogramSelectionRange(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
			}
		}
	}

	private class ScanSelectionHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getEvent() {

			return BaseChart.EVENT_MOUSE_DOUBLE_CLICK;
		}

		@Override
		public int getButton() {

			return BaseChart.BUTTON_LEFT;
		}

		@Override
		public int getStateMask() {

			return SWT.NONE;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			if(chromatogramSelection != null) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				int retentionTime = (int)baseChart.getSelectedPrimaryAxisValue(event.x, IExtendedChart.X_AXIS);
				int scanNumber = chromatogram.getScanNumber(retentionTime);
				IScan scan = chromatogram.getScan(scanNumber);
				if(scan != null) {
					chromatogramSelection.setSelectedScan(scan);
					display.asyncExec(new Runnable() {

						@Override
						public void run() {

							IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
							eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, scan);
						}
					});
				}
			}
		}
	}

	private class PeakSelectionHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getEvent() {

			return BaseChart.EVENT_MOUSE_DOUBLE_CLICK;
		}

		@Override
		public int getButton() {

			return BaseChart.BUTTON_LEFT;
		}

		@Override
		public int getStateMask() {

			return SWT.ALT;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			if(chromatogramSelection != null) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				int retentionTime = (int)baseChart.getSelectedPrimaryAxisValue(event.x, IExtendedChart.X_AXIS);
				IPeak peak = null;
				if(chromatogram instanceof IChromatogramMSD) {
					IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
					peak = chromatogramMSD.getPeak(retentionTime);
				} else if(chromatogram instanceof IChromatogramCSD) {
					IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
					peak = chromatogramCSD.getPeak(retentionTime);
				} else if(chromatogram instanceof IChromatogramWSD) {
					IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
					peak = chromatogramWSD.getPeak(retentionTime);
				}
				if(peak != null) {
					/*
					 * Fire an update.
					 */
					chromatogramSelection.setSelectedPeak(peak);
					boolean moveRetentionTimeOnPeakSelection = preferenceStore.getBoolean(PreferenceConstants.P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION);
					if(moveRetentionTimeOnPeakSelection) {
						adjustChromatogramSelection(peak, chromatogramSelection);
					}
					//
					updateSelection();
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, peak);
				}
			}
		}

		private void adjustChromatogramSelection(IPeak peak, IChromatogramSelection chromatogramSelection) {

			if(chromatogramSelection != null) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				List<? extends IPeak> peaks = chromatogramDataSupport.getPeaks(chromatogram);
				List<? extends IPeak> peaksSelection = new ArrayList<>(chromatogramDataSupport.getPeaks(chromatogram, chromatogramSelection));
				Collections.sort(peaks, peakRetentionTimeComparator);
				Collections.sort(peaksSelection, peakRetentionTimeComparator);
				//
				if(peaks.get(0).equals(peak) || peaks.get(peaks.size() - 1).equals(peak)) {
					/*
					 * Don't move if it is the first or last peak of the chromatogram.
					 */
				} else {
					/*
					 * First peak of the selection: move left
					 * Last peak of the selection: move right
					 */
					if(peaksSelection.get(0).equals(peak)) {
						ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.LEFT, 5);
					} else if(peaksSelection.get(peaksSelection.size() - 1).equals(peak)) {
						ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.RIGHT, 5);
					}
				}
			}
		}
	}

	private class ScanSelectionArrowKeyHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		private int keyCode;

		public ScanSelectionArrowKeyHandler(int keyCode) {
			this.keyCode = keyCode;
		}

		@Override
		public int getEvent() {

			return BaseChart.EVENT_KEY_UP;
		}

		@Override
		public int getButton() {

			return keyCode;
		}

		@Override
		public int getStateMask() {

			return SWT.CTRL;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			handleControlScanSelection(keyCode);
		}
	}

	private class ChromatogramMoveArrowKeyHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		private int keyCode;

		public ChromatogramMoveArrowKeyHandler(int keyCode) {
			this.keyCode = keyCode;
		}

		@Override
		public int getEvent() {

			return BaseChart.EVENT_KEY_UP;
		}

		@Override
		public int getButton() {

			return keyCode;
		}

		@Override
		public int getStateMask() {

			return SWT.NONE;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			handleArrowMoveWindowSelection(keyCode);
		}
	}

	private class ClassifierMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

		private String name;
		private String classifierId;
		private String type;
		private IChromatogramSelection chromatogramSelection;

		public ClassifierMenuEntry(String name, String classifierId, String type, IChromatogramSelection chromatogramSelection) {
			this.name = name;
			this.classifierId = classifierId;
			this.type = type;
			this.chromatogramSelection = chromatogramSelection;
		}

		@Override
		public String getCategory() {

			return "Classifier";
		}

		@Override
		public String getName() {

			return name;
		}

		@Override
		public void execute(Shell shell, ScrollableChart scrollableChart) {

			if(chromatogramSelection != null) {
				/*
				 * Create the runnable.
				 */
				IRunnableWithProgress runnable = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						switch(type) {
							case TYPE_GENERIC:
								//
								break;
							case TYPE_MSD:
								if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
									IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
									ChromatogramClassifier.applyClassifier(chromatogramSelectionMSD, classifierId, monitor);
								}
								break;
							case TYPE_CSD:
								if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
									IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
									//
								}
								break;
							case TYPE_WSD:
								if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
									IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
									//
								}
								break;
						}
					}
				};
				/*
				 * Execute
				 */
				processChromatogram(runnable);
			}
		}
	}

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
				/*
				 * Create the runnable.
				 */
				IRunnableWithProgress runnable = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						switch(type) {
							case TYPE_GENERIC:
								ChromatogramFilter.applyFilter(chromatogramSelection, filterId, monitor);
								break;
							case TYPE_MSD:
								if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
									IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
									ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, filterId, monitor);
								}
								break;
							case TYPE_CSD:
								if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
									IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
									ChromatogramFilterCSD.applyFilter(chromatogramSelectionCSD, filterId, monitor);
								}
								break;
							case TYPE_WSD:
								if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
									IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
									ChromatogramFilterWSD.applyFilter(chromatogramSelectionWSD, filterId, monitor);
								}
								break;
						}
					}
				};
				/*
				 * Execute
				 */
				processChromatogram(runnable);
			}
		}
	}

	private class PeakDetectorMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

		private String name;
		private String peakDetectorId;
		private String type;
		private IChromatogramSelection chromatogramSelection;

		public PeakDetectorMenuEntry(String name, String peakDetectorId, String type, IChromatogramSelection chromatogramSelection) {
			this.name = name;
			this.peakDetectorId = peakDetectorId;
			this.type = type;
			this.chromatogramSelection = chromatogramSelection;
		}

		@Override
		public String getCategory() {

			return "Peak Detectors";
		}

		@Override
		public String getName() {

			return name;
		}

		@Override
		public void execute(Shell shell, ScrollableChart scrollableChart) {

			if(chromatogramSelection != null) {
				/*
				 * Create the runnable.
				 */
				IRunnableWithProgress runnable = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						switch(type) {
							case TYPE_MSD:
								if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
									IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
									PeakDetectorMSD.detect(chromatogramSelectionMSD, peakDetectorId, monitor);
								}
								break;
							case TYPE_CSD:
								if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
									IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
									PeakDetectorCSD.detect(chromatogramSelectionCSD, peakDetectorId, monitor);
								}
								break;
							case TYPE_WSD:
								if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
									IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
									PeakDetectorWSD.detect(chromatogramSelectionWSD, peakDetectorId, monitor);
								}
								break;
						}
					}
				};
				/*
				 * Execute
				 */
				processChromatogram(runnable);
			}
		}
	}

	private class PeakIntegratorMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

		private String name;
		private String peakIntegratorId;
		private String type;
		private IChromatogramSelection chromatogramSelection;

		public PeakIntegratorMenuEntry(String name, String peakIntegratorId, String type, IChromatogramSelection chromatogramSelection) {
			this.name = name;
			this.peakIntegratorId = peakIntegratorId;
			this.type = type;
			this.chromatogramSelection = chromatogramSelection;
		}

		@Override
		public String getCategory() {

			return "Peak Integrators";
		}

		@Override
		public String getName() {

			return name;
		}

		@Override
		public void execute(Shell shell, ScrollableChart scrollableChart) {

			if(chromatogramSelection != null) {
				/*
				 * Create the runnable.
				 */
				IRunnableWithProgress runnable = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						switch(type) {
							case TYPE_GENERIC:
								PeakIntegrator.integrate(chromatogramSelection, peakIntegratorId, monitor);
								break;
						}
					}
				};
				/*
				 * Execute
				 */
				processChromatogram(runnable);
			}
		}
	}

	private class PeakIdentifierMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

		private String name;
		private String peakIdentifierId;
		private String type;
		private IChromatogramSelection chromatogramSelection;

		public PeakIdentifierMenuEntry(String name, String peakIdentifierId, String type, IChromatogramSelection chromatogramSelection) {
			this.name = name;
			this.peakIdentifierId = peakIdentifierId;
			this.type = type;
			this.chromatogramSelection = chromatogramSelection;
		}

		@Override
		public String getCategory() {

			return "Peak Identifier";
		}

		@Override
		public String getName() {

			return name;
		}

		@Override
		public void execute(Shell shell, ScrollableChart scrollableChart) {

			if(chromatogramSelection != null) {
				/*
				 * Create the runnable.
				 */
				IRunnableWithProgress runnable = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						switch(type) {
							case TYPE_MSD:
								if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
									IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
									PeakIdentifier.identify(chromatogramSelectionMSD, peakIdentifierId, monitor);
								}
								break;
						}
					}
				};
				/*
				 * Execute
				 */
				processChromatogram(runnable);
			}
		}
	}

	private class ReportMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

		private IChromatogramReportSupplier chromatogramReportSupplier;
		private String type;
		private IChromatogramSelection chromatogramSelection;

		public ReportMenuEntry(IChromatogramReportSupplier chromatogramReportSupplier, String type, IChromatogramSelection chromatogramSelection) {
			this.chromatogramReportSupplier = chromatogramReportSupplier;
			this.type = type;
			this.chromatogramSelection = chromatogramSelection;
		}

		@Override
		public String getCategory() {

			return "Reports";
		}

		@Override
		public String getName() {

			return chromatogramReportSupplier.getReportName();
		}

		@Override
		public void execute(Shell shell, ScrollableChart scrollableChart) {

			if(chromatogramSelection != null) {
				/*
				 * Create the runnable.
				 */
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				File file = getFileFromFileDialog(chromatogram.getName(), chromatogramReportSupplier);
				//
				if(file != null) {
					IRunnableWithProgress runnable = new IRunnableWithProgress() {

						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

							switch(type) {
								case TYPE_GENERIC:
									ChromatogramReports.generate(file, false, chromatogram, chromatogramReportSupplier.getId(), monitor);
									break;
							}
						}
					};
					/*
					 * Execute
					 */
					processChromatogram(runnable);
				}
			}
		}

		private File getFileFromFileDialog(String defaultFileName, IChromatogramReportSupplier chromatogramReportSupplier) {

			FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
			fileDialog.setOverwrite(true);
			fileDialog.setText("Report");
			fileDialog.setFileName(defaultFileName);
			fileDialog.setFilterExtensions(new String[]{"*" + chromatogramReportSupplier.getFileExtension()});
			fileDialog.setFilterNames(new String[]{chromatogramReportSupplier.getReportName()});
			String fileName = fileDialog.open();
			if(fileName == null || fileName.equals("")) {
				return null;
			} else {
				return new File(fileName);
			}
		}
	}

	private void processChromatogram(IRunnableWithProgress runnable) {

		/*
		 * Excecute
		 */
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
		try {
			monitor.run(true, true, runnable);
			updateChromatogram();
			updateSelection();
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
	}

	@Inject
	public ExtendedChromatogramUI(Composite parent) {
		initialize(parent);
	}

	public BaseChart getBaseChart() {

		return chromatogramChart.getBaseChart();
	}

	public synchronized void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		if(chromatogramSelection != null) {
			/*
			 * Adjust
			 */
			addChartMenuEntries();
			updateChromatogram();
			if(referencedChromatogramSelections == null) {
				updateChromatogramCombo();
				updateChromatogramTargetTransferCombo();
			}
		} else {
			comboChromatograms.setItems(new String[0]);
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

	private void adjustMinuteScale() {

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

	private void addChartMenuEntries() {

		addChartMenuEntriesClassifier();
		addChartMenuEntriesFilter();
		addChartMenuEntriesPeakDetectors();
		addChartMenuEntriesPeakIntegrators();
		addChartMenuEntriesPeakIdentifier();
		addChartMenuEntriesReport();
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
				ClassifierMenuEntry classifierMenuEntry = new ClassifierMenuEntry(name, filterId, TYPE_MSD, chromatogramSelection);
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
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(name, filterId, TYPE_GENERIC, chromatogramSelection);
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
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(name, filterId, TYPE_MSD, chromatogramSelection);
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
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(name, filterId, TYPE_CSD, chromatogramSelection);
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
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(name, filterId, TYPE_WSD, chromatogramSelection);
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
				PeakDetectorMenuEntry menuEntry = new PeakDetectorMenuEntry(name, peakDetectorId, TYPE_MSD, chromatogramSelection);
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
				PeakDetectorMenuEntry menuEntry = new PeakDetectorMenuEntry(name, peakDetectorId, TYPE_CSD, chromatogramSelection);
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
				PeakDetectorMenuEntry menuEntry = new PeakDetectorMenuEntry(name, peakDetectorId, TYPE_WSD, chromatogramSelection);
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
				PeakIntegratorMenuEntry menuEntry = new PeakIntegratorMenuEntry(name, peakIntegratorId, TYPE_GENERIC, chromatogramSelection);
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
			}
		}
	}

	private void addChartMenuEntriesPeakIdentifierMSD(IChartSettings chartSettings) {

		try {
			IPeakIdentifierSupport peakIdentifierSupport = PeakIdentifier.getPeakIdentifierSupport();
			for(String peakIdentifierId : peakIdentifierSupport.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplier peakIdentifierSupplier = peakIdentifierSupport.getIdentifierSupplier(peakIdentifierId);
				String name = peakIdentifierSupplier.getIdentifierName();
				PeakIdentifierMenuEntry menuEntry = new PeakIdentifierMenuEntry(name, peakIdentifierId, TYPE_MSD, chromatogramSelection);
				chartMenuEntriesPeakIdentifier.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoIdentifierAvailableException e) {
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
			ReportMenuEntry menuEntry = new ReportMenuEntry(chromatogramReportSupplier, TYPE_GENERIC, chromatogramSelection);
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
		//
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			rangeRestriction.setZeroY(true);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			rangeRestriction.setZeroY(false);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			rangeRestriction.setZeroY(false);
		}
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
		toolbarChromatograms = createToolbarChromatograms(parent);
		toolbarEdit = createToolbarEdit(parent);
		createChromatogramChart(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, false);
		PartSupport.setCompositeVisibility(toolbarChromatograms, false);
		PartSupport.setCompositeVisibility(toolbarEdit, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(8, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarChromatograms(composite);
		createButtonToggleToolbarEdit(composite);
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

	private Composite createToolbarChromatograms(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		Label label = new Label(composite, SWT.NONE);
		label.setText("Show methods and peak detection, ... items here ... simple workflow.");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// createButtonSelectPreviousChromatogram(composite);
		// createComboChromatograms(composite);
		// createButtonSelectNextChromatogram(composite);
		//
		return composite;
	}

	private Composite createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(14, false));
		//
		createButtonSelectPreviousChromatogram(composite);
		createComboChromatograms(composite);
		createButtonSelectNextChromatogram(composite);
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

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
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
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
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
				MessageDialog.openInformation(shell, "Range Selection", "The selected editor range has been set successfully to all opened chromatograms.");
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
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
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
							MessageDialog.openWarning(shell, "Transfer Peak Target(s)", "The sink chromatogram contains no peaks.");
						}
					} else {
						MessageDialog.openWarning(shell, "Transfer Peak Target(s)", "The source chromatogram contains no peaks.");
					}
				}
			} else {
				MessageDialog.openWarning(shell, "Transfer Peak Target(s)", "It's not possible to transfer targets to the same chromatogram.");
			}
		} else {
			MessageDialog.openWarning(shell, "Transfer Peak Target(s)", "Please select a chromatogram.");
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
		IChromatogramSelection chromatogramSelection = referencedChromatogramSelections.get(index);
		if(chromatogramSelection != null) {
			updateChromatogramSelection(chromatogramSelection);
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
		for(IChromatogramSelection editorChromatogramSelection : editorUpdateSupport.getChromatogramSelections()) {
			if(editorChromatogramSelection.getChromatogram() != chromatogramSelection.getChromatogram()) {
				editorChromatogramSelections.add(editorChromatogramSelection);
			}
		}
		//
		targetChromatogramSelections.addAll(editorChromatogramSelections);
		references.addAll(getEditorReferences(editorChromatogramSelections));
		//
		targetChromatogramSelections.addAll(referencedChromatogramSelections);
		references.addAll(getChromatogramReferences(referencedChromatogramSelections));
		/*
		 * Set the items.
		 */
		comboTargetTransfer.setItems(references.toArray(new String[references.size()]));
		if(references.size() > 0) {
			comboTargetTransfer.select(0);
		}
	}

	private List<IChromatogramSelection> getChromatogramReferences(IChromatogramSelection chromatogramSelection) {

		/*
		 * Current selection / Reference
		 */
		List<IChromatogramSelection> chromatogramSelections = new ArrayList<IChromatogramSelection>();
		chromatogramSelections.add(chromatogramSelection);
		//
		List<IChromatogram> referencedChromatograms = chromatogramSelection.getChromatogram().getReferencedChromatograms();
		int i = 1;
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
					references.add("Chromatogram Reference #" + i++ + " " + type);
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

	private void createChromatogramChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.BORDER);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Custom Selection Handler
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		baseChart.addCustomRangeSelectionHandler(new ChromatogramSelectionHandler(baseChart));
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
		chartSettings.addMenuEntry(new ChromatogramResetHandler());
		chartSettings.addHandledEventProcessor(new ScanSelectionHandler());
		chartSettings.addHandledEventProcessor(new PeakSelectionHandler());
		chartSettings.addHandledEventProcessor(new ScanSelectionArrowKeyHandler(SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new ScanSelectionArrowKeyHandler(SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(SWT.ARROW_UP));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(SWT.ARROW_DOWN));
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

	private Button createButtonToggleToolbarChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle chromatograms toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarChromatograms);
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

		updateChromatogram();
	}

	private void reset() {

		updateChromatogram();
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
				secondaryAxisSettings.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
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

	private void setChromatogramSelectionRange(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) {

		chromatogramSelection.setRanges(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance, false);
		suspendUpdate = true;
		chromatogramSelection.update(true);
		suspendUpdate = false;
		adjustChromatogramSelectionRange();
	}

	private void adjustChromatogramSelectionRange() {

		if(chromatogramSelection != null) {
			chromatogramChart.setRange(IExtendedChart.X_AXIS, chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime());
			chromatogramChart.setRange(IExtendedChart.Y_AXIS, chromatogramSelection.getStartAbundance(), chromatogramSelection.getStopAbundance());
		}
	}

	private void handleControlScanSelection(int keyCode) {

		if(chromatogramSelection != null) {
			/*
			 * Select the next or previous scan.
			 */
			int scanNumber = chromatogramSelection.getSelectedScan().getScanNumber();
			if(keyCode == SWT.ARROW_RIGHT) {
				scanNumber++;
			} else {
				scanNumber--;
			}
			/*
			 * Set and fire an update.
			 */
			IScan selectedScan = chromatogramSelection.getChromatogram().getScan(scanNumber);
			//
			IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
			eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, selectedScan);
			//
			if(selectedScan != null) {
				/*
				 * The selection should slide with the selected scans.
				 */
				int scanRetentionTime = selectedScan.getRetentionTime();
				int startRetentionTime = chromatogramSelection.getStartRetentionTime();
				int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
				/*
				 * Left or right slide on demand.
				 */
				if(scanRetentionTime <= startRetentionTime) {
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.LEFT, 5);
				} else if(scanRetentionTime >= stopRetentionTime) {
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.RIGHT, 5);
				}
				//
				chromatogramSelection.setSelectedScan(selectedScan, false);
				updateSelection();
			}
		}
	}

	private void handleArrowMoveWindowSelection(int keyCode) {

		if(chromatogramSelection != null) {
			if(keyCode == SWT.ARROW_RIGHT || keyCode == SWT.ARROW_LEFT) {
				/*
				 * Left, Right
				 * (Retention Time)
				 */
				boolean useAlternateWindowMoveDirection = preferenceStore.getBoolean(PreferenceConstants.P_ALTERNATE_WINDOW_MOVE_DIRECTION);
				//
				if(keyCode == SWT.ARROW_RIGHT) {
					MoveDirection moveDirection = (useAlternateWindowMoveDirection) ? MoveDirection.LEFT : MoveDirection.RIGHT;
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, moveDirection, 20);
				} else {
					MoveDirection moveDirection = (useAlternateWindowMoveDirection) ? MoveDirection.RIGHT : MoveDirection.LEFT;
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, moveDirection, 20);
				}
				updateSelection();
				//
			} else if(keyCode == SWT.ARROW_UP || keyCode == SWT.ARROW_DOWN) {
				/*
				 * Up, Down
				 * (Abundance)
				 * Doesn't work if auto adjust signals is enabled.
				 */
				float stopAbundance = chromatogramSelection.getStopAbundance();
				float newStopAbundance;
				if(PreferenceSupplier.useAlternateWindowMoveDirection()) {
					newStopAbundance = (keyCode == SWT.ARROW_UP) ? stopAbundance - stopAbundance / 20.0f : stopAbundance + stopAbundance / 20.0f;
				} else {
					newStopAbundance = (keyCode == SWT.ARROW_UP) ? stopAbundance + stopAbundance / 20.0f : stopAbundance - stopAbundance / 20.0f;
				}
				//
				int startRetentionTime = chromatogramSelection.getStartRetentionTime();
				int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
				float startAbundance = chromatogramSelection.getStartAbundance();
				setChromatogramSelectionRange(startRetentionTime, stopRetentionTime, startAbundance, newStopAbundance);
				updateSelection();
			}
		}
	}

	private void updateSelection() {

		if(chromatogramSelection != null) {
			chromatogramSelection.update(true);
			adjustChromatogramSelectionRange();
		}
	}
}
