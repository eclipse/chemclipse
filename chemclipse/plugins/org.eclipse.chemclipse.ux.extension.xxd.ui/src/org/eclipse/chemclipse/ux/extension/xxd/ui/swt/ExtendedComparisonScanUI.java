/*******************************************************************************
 * Copyright (c) 2017, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - make this configurable, null check for scan
 * Lorenz Gerber - inherit background mode in Tabfolder charts
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import static org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNominalMZ;
import static org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.calculator.SubtractCalculator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.signals.ComparisonCalculator;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.model.types.SignalType;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.ui.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.LabelOption;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.ComparisonScanOption;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSubtract;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ScanChartAxisIntensity;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ScanChartAxisIon;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ScanChartAxisRelativeIntensity;
import org.eclipse.chemclipse.ux.extension.xxd.ui.runnables.LibraryServiceRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.ChromatogramUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanDataSupport;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.barcharts.IBarSeriesData;
import org.eclipse.swtchart.extensions.barcharts.IBarSeriesSettings;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.ChartType;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import jakarta.inject.Inject;

public class ExtendedComparisonScanUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedComparisonScanUI.class);

	private static final float NORMALIZATION_FACTOR = 1000.0f;

	private static final String PREFIX_U = "[U]";
	private static final String PREFIX_R = "[R]";
	private static final String PREFIX_UR = "[U-R]";
	private static final String TITLE_UNKNOWN = "UNKNOWN MS";
	private static final String TITLE_REFERENCE = "REFERENCE MS";
	private static final String POSTFIX_NONE = "";
	private static final String POSTFIX_SHIFTED = "SHIFTED";
	private static final String IMAGE_HYBRID = IApplicationImage.IMAGE_BACKWARD;
	private static final String TOOLTIP_HYBRID = "the hybrid search.";

	private AtomicReference<TabFolder> tabFolderControl = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerOptionControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfoTop = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfoBottom = new AtomicReference<>();
	private AtomicReference<ScanChartUI> scanChartControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarHybrid = new AtomicReference<>();
	private AtomicReference<Button> buttonSubtractReference = new AtomicReference<>();
	private AtomicReference<Button> buttonUseOptimizedScan = new AtomicReference<>();
	private AtomicReference<ScanIdentifierUI> scanIdentifierControl = new AtomicReference<>();
	private AtomicReference<Button> buttonMirroredReference = new AtomicReference<>();
	private AtomicReference<Button> buttonDifferenceSpectrum = new AtomicReference<>();
	private AtomicReference<Button> buttonShiftReferenceSpectrum = new AtomicReference<>();
	private AtomicReference<Spinner> spinnerShiftReferenceSpectrum = new AtomicReference<>();
	private AtomicReference<Button> buttonLegendControl = new AtomicReference<>();
	private AtomicReference<Composite> toolbarHybridSearch = new AtomicReference<>();
	private AtomicReference<Text> textWeightUnknownControl = new AtomicReference<>();
	private AtomicReference<Text> textWeightReferenceControl = new AtomicReference<>();
	private AtomicReference<Button> buttonAutoAdjustUnknownControl = new AtomicReference<>();
	private AtomicReference<ScanChartUI> scanChartStackUnknownControl = new AtomicReference<>();
	private AtomicReference<ScanChartUI> scanChartStackReferenceControl = new AtomicReference<>();

	private boolean showDifferenceSpectrum = false;
	private boolean useMirroredSpectrum = true;
	private boolean useOptimizedSpectrum = false;
	private boolean shiftReferenceSpectrum = false;
	private int shiftMass = 1;

	private IScanMSD scanUnknownMaster = null;
	private IScanMSD scanUnknown = null;
	private IScanMSD scanReference = null;

	private ScanChartSupport scanChartSupport = new ScanChartSupport();
	private ScanDataSupport scanDataSupport = new ScanDataSupport();

	@Inject
	public ExtendedComparisonScanUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	@Focus
	public boolean setFocus() {

		updateOnFocus();
		return true;
	}

	public void clear() {

		IScanMSD unknown = null;
		IScanMSD reference = null;
		update(unknown, reference);
	}

	/**
	 * Update the mass spectrum and target.
	 *
	 * @param scanMSD
	 * @param identificationTarget
	 */
	public void update(IScanMSD scanMSD, IIdentificationTarget identificationTarget) {

		if(identificationTarget != null) {
			if(isLibrarySearch()) {
				if(scanMSD != null) {
					scanUnknownMaster = scanMSD;
					assignScan(scanMSD, true);
				}
				updateMolecularWeightUnknown();
				updateIdentificationTarget(identificationTarget);
				updateIdentifierControl();
			}
		} else {
			switch(getComparisonScanOption()) {
				case UNKNOWN:
					scanUnknownMaster = scanMSD;
					assignScan(scanMSD, true);
					break;
				case REFERENCE:
					assignScan(scanMSD, false);
					break;
				default:
					assignScan(scanMSD, true);
					scanReference = null;
					break;
			}
		}
		updateInput();
	}

	/**
	 * Update unknown and reference mass spectrum.
	 *
	 * @param unknownMassSpectrum
	 * @param referenceMassSpectrum
	 */
	public void update(IScanMSD unknownMassSpectrum, IScanMSD referenceMassSpectrum) {

		scanUnknownMaster = unknownMassSpectrum;
		updateInput(unknownMassSpectrum, referenceMassSpectrum);
	}

	@Override
	public void dispose() {

		scanChartControl.get().dispose();
	}

	private boolean isLibrarySearch() {

		return ComparisonScanOption.LIBRARY_SEARCH.equals(getComparisonScanOption());
	}

	private ComparisonScanOption getComparisonScanOption() {

		Object selection = comboViewerOptionControl.get().getStructuredSelection().getFirstElement();
		if(selection instanceof ComparisonScanOption comparisonScanOption) {
			return comparisonScanOption;
		} else {
			return ComparisonScanOption.LIBRARY_SEARCH;
		}
	}

	private void updateIdentificationTarget(IIdentificationTarget identificationTarget) {

		updateMolecularWeightReference(identificationTarget);

		LibraryServiceRunnable runnable = new LibraryServiceRunnable(identificationTarget, referenceMassSpectrum -> {

			scanReference = copyScan(referenceMassSpectrum);
			updateMolecularIon(identificationTarget, scanReference);
			Display.getDefault().asyncExec(() -> {

				updateChart();
			});
		});
		/*
		 * Create a runnable to update the reference.
		 */
		try {
			if(runnable.requireProgressMonitor()) {
				DisplayUtils.executeInUserInterfaceThread(() -> {
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(scanChartControl.get().getShell());
					monitor.run(true, true, runnable);
					return null;
				});
			} else {
				DisplayUtils.executeBusy(() -> {
					runnable.run(new NullProgressMonitor());
					return null;
				});
			}
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch(ExecutionException e) {
			ILog.get().error("Updating the reference scan failed.", e);
		}
	}

	private void updateMolecularIon(IIdentificationTarget identificationTarget, IScanMSD scanMSD) {

		if(identificationTarget != null && scanMSD != null) {
			ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
			IIon molecularIon = scanMSD.getIon(libraryInformation.getMolWeight());
			if(molecularIon.getAbundance() > 0) {
				scanChartControl.get().setMolecularIon(molecularIon);
			}
		}
	}

	private void updateMolecularWeightUnknown() {

		ILibraryInformation libraryInformation = IIdentificationTarget.getLibraryInformation(scanUnknown);
		if(libraryInformation != null) {
			textWeightUnknownControl.get().setText(Integer.toString((int)Math.round(libraryInformation.getMolWeight())));
		} else {
			textWeightUnknownControl.get().setText("");
		}
	}

	private void updateMolecularWeightReference(IIdentificationTarget identificationTarget) {

		if(identificationTarget != null) {
			ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
			textWeightReferenceControl.get().setText(Integer.toString((int)Math.round(libraryInformation.getMolWeight())));
		} else {
			textWeightReferenceControl.get().setText("");
		}
	}

	private void updateStackCharts() {

		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();
		ColorRegistry colorRegistry = currentTheme.getColorRegistry();
		Color colorScan1 = colorRegistry.get("org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ScanChart.ColorScanOne");
		Color colorScan2 = colorRegistry.get("org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ScanChart.ColorScanTwo");
		updateStackChart(scanChartStackUnknownControl.get(), scanUnknown, colorScan1, "Unknown");
		updateStackChart(scanChartStackReferenceControl.get(), scanReference, colorScan2, "Reference");
	}

	private void updateStackChart(ScanChartUI scanChartUI, IScanMSD scanMSD, Color color, String label) {

		scanChartUI.deleteSeries();
		List<IBarSeriesData> barSeriesDataList = new ArrayList<>();
		IBarSeriesData barSeriesDataScan = scanChartSupport.getBarSeriesData(scanMSD, label, false);
		IBarSeriesSettings barSeriesSettings = barSeriesDataScan.getSettings();
		barSeriesSettings.setBarColor(color);
		barSeriesSettings.setBarOverlay(true);
		barSeriesDataList.add(barSeriesDataScan);
		scanChartUI.addBarSeriesData(barSeriesDataList);
	}

	private void clearStackCharts() {

		scanChartStackUnknownControl.get().deleteSeries();
		scanChartStackReferenceControl.get().deleteSeries();
	}

	private void updateScanComparisonNormal() {

		updateToolbarInfoSpecial(PREFIX_U, PREFIX_R);
		ScanChartUI scanChartUI = scanChartControl.get();
		if(shiftReferenceSpectrum) {
			IScanMSD scanReferenceShifted = new ScanMSD();
			IExtractedIonSignal extractedIonSignalScanReference = scanReference.getExtractedIonSignal();
			int startIon = extractedIonSignalScanReference.getStartIon();
			int stopIon = extractedIonSignalScanReference.getStopIon();
			for(int ion = startIon; ion <= stopIon; ion++) {
				float abundance = extractedIonSignalScanReference.getAbundance(ion);
				if(abundance > 0) {
					scanReferenceShifted.addIon(getIon(ion + shiftMass, abundance));
				}
			}
			scanChartUI.setInput(scanUnknown, scanReferenceShifted, useMirroredSpectrum);
		} else {
			scanChartUI.setInput(scanUnknown, scanReference, useMirroredSpectrum);
		}
		/*
		 * Post modify if hybrid search is active.
		 */
		if(toolbarHybridSearch.get().isVisible()) {
			IChartSettings chartSettings = scanChartUI.getChartSettings();
			chartSettings.getPrimaryAxisSettingsX().setTitle("Delta [m/z]");
			RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
			rangeRestriction.setZeroX(false);
			rangeRestriction.setZeroY(false);
			rangeRestriction.setForceZeroMinY(false);
			rangeRestriction.setRestrictZoomX(false);
			scanChartUI.applySettings(chartSettings);
			int molWeightUnknown = getMolWeight(textWeightUnknownControl);
			int molWeightReference = getMolWeight(textWeightReferenceControl);
			BaseChart baseChart = scanChartUI.getBaseChart();
			baseChart.shiftSeries(ScanChartUI.LABEL_SCAN1, IExtendedChart.X_AXIS, -molWeightUnknown);
			baseChart.shiftSeries(ScanChartUI.LABEL_SCAN2, IExtendedChart.X_AXIS, -molWeightReference);
			scanChartUI.adjustRange(true);
		}
	}

	private int getMolWeight(AtomicReference<Text> textControl) {

		try {
			return Integer.parseInt(textControl.get().getText().trim());
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));

		createToolbarMain(this);
		createToolbarInfoTop(this);
		createToolbarHybridSearch(this);
		createTabFolderCharts(this);
		createToolbarInfoBottom(this);

		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfoTop, buttonToolbarInfo.get(), IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarInfoBottom, buttonToolbarInfo.get(), IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarHybridSearch, buttonToolbarHybrid.get(), IMAGE_HYBRID, TOOLTIP_HYBRID, false);
		comboViewerOptionControl.get().setInput(ComparisonScanOption.values());
		comboViewerOptionControl.get().setSelection(new StructuredSelection(ComparisonScanOption.LIBRARY_SEARCH));
		buttonLegendControl.get().setEnabled(true);
		adjustAxisSettings();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(14, false));

		createButtonToggleInfo(composite);
		createComboViewerComparisonScanOption(composite);
		createResetButton(composite);
		createSaveButton(composite);
		createButtonToggleHybrid(composite);
		createButtonSubtractReference(composite);
		createButtonOptimizedSpectrum(composite);
		createScanIdentifierUI(composite);
		createButtonMirroredSpectrum(composite);
		createButtonDifferenceSpectrum(composite);
		createButtonShiftReferenceSpectrum(composite);
		createSpinnerShiftReferenceSpectrum(composite);
		createToggleLegendButton(composite);
		createSettingsButton(composite);
	}

	private void createButtonToggleInfo(Composite parent) {

		buttonToolbarInfo.set(createButtonToggleToolbar(parent, Arrays.asList(toolbarInfoTop, toolbarInfoBottom), IMAGE_INFO, TOOLTIP_INFO));
	}

	private void createComboViewerComparisonScanOption(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ComparisonScanOption comparisonScanOption) {
					return comparisonScanOption.label();
				}
				return null;
			}
		});

		combo.setToolTipText("Comparison Scan Option");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ComparisonScanOption) {
					updateChart();
				}
			}
		});

		comboViewerOptionControl.set(comboViewer);
	}

	private void createToolbarHybridSearch(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, true));

		createTextWeightUnknown(composite, "MW Unknkown");
		createButtonAutoAdjustUnknown(composite);
		createTextWeightReference(composite, "MW Reference");

		toolbarHybridSearch.set(composite);
	}

	private void createButtonAutoAdjustUnknown(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Auto Adjust");
		button.setToolTipText("Try to calculate the best mol weight to match the hybrid delta mass spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_AUTO_MIRROR, IApplicationImageProvider.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(scanUnknown != null && scanReference != null) {
					try {
						int molWeightReference = Integer.parseInt(textWeightReferenceControl.get().getText().trim());
						if(molWeightReference > 0) {
							ScanChartUI scanChartUI = scanChartControl.get();
							BaseChart baseChart = scanChartUI.getBaseChart();
							/*
							 * Reference - fixed mol weight as the spectrum is fetched from the database
							 */
							ISeries<?> seriesReference = baseChart.getSeriesSet().getSeries(ScanChartUI.LABEL_SCAN2);
							Map<Integer, Double> referenceMap = new HashMap<>();
							double[] xSeriesReference = seriesReference.getXSeries();
							double[] ySeriesReference = seriesReference.getYSeries();
							for(int i = 0; i < xSeriesReference.length; i++) {
								referenceMap.put((int)Math.round(xSeriesReference[i] - molWeightReference), ySeriesReference[i] * -1);
							}
							Set<Integer> keysReference = referenceMap.keySet();
							int minReferenceX = keysReference.stream().min(Integer::compare).get();
							int maxReferenceX = keysReference.stream().max(Integer::compare).get();
							/*
							 * Unknown - get best matching mol weight
							 */
							int molWeightUnknown = -1;
							double bestMatch = Double.MIN_VALUE;
							ISeries<?> seriesUnknown = baseChart.getSeriesSet().getSeries(ScanChartUI.LABEL_SCAN1);
							ComparisonCalculator comparisonCalculator = new ComparisonCalculator();
							Map<Integer, Double> unknownMap = new HashMap<>();
							int molWeightMin = PreferenceSupplier.getHybridSearchMolWeightMin();
							int molWeightMax = PreferenceSupplier.getHybridSearchMolWeightMax();
							for(int i = molWeightMin; i <= molWeightMax; i++) {
								unknownMap.clear();
								double[] xSeriesUnknown = seriesUnknown.getXSeries();
								double[] ySeriesUnknown = seriesUnknown.getYSeries();
								for(int j = 0; j < xSeriesUnknown.length; j++) {
									unknownMap.put((int)Math.round(xSeriesUnknown[j] - i), ySeriesUnknown[j]);
								}
								/*
								 * Determine the size of the arrays to be compared.
								 */
								Set<Integer> keysUnknown = unknownMap.keySet();
								int minUnknownX = keysUnknown.stream().min(Integer::compare).get();
								int maxUnknownX = keysUnknown.stream().max(Integer::compare).get();
								int minX = Math.min(minReferenceX, minUnknownX);
								int maxX = Math.max(maxReferenceX, maxUnknownX);
								int size;
								if(minX < 0 && maxX >= 0) {
									size = minX * -1 + maxX + 1;
								} else {
									size = maxX - minX + 1;
								}
								/*
								 * Fetch the unknown / reference array data.
								 */
								if(size > 0) {
									double[] unknown = new double[size];
									double[] reference = new double[size];
									for(int k = minX,
											m = 0; k <= maxX; k++, m++) {
										unknown[m] = unknownMap.getOrDefault(k, 0.0d);
										reference[m] = referenceMap.getOrDefault(k, 0.0d);
									}
									/*
									 * Try to get the best matching mol weight.
									 */
									double currentMatch = comparisonCalculator.calculateMatch(unknown, reference);
									if(currentMatch > bestMatch) {
										bestMatch = currentMatch;
										molWeightUnknown = i;
									}
								}
							}
							/*
							 * Validate and set.
							 */
							if(molWeightUnknown >= 1) {
								textWeightUnknownControl.get().setText(Integer.toString(molWeightUnknown));
								updateChart();
							}
						}
					} catch(NumberFormatException e1) {
						logger.warn(e1);
					}
				}
			}
		});

		buttonAutoAdjustUnknownControl.set(button);
	}

	private void createTextWeightUnknown(Composite parent, String tooltip) {

		textWeightUnknownControl.set(createText(parent, tooltip));
	}

	private void createTextWeightReference(Composite parent, String tooltip) {

		textWeightReferenceControl.set(createText(parent, tooltip));
	}

	private Text createText(Composite parent, String tooltip) {

		Text text = new Text(parent, SWT.BORDER);
		text.setToolTipText(tooltip);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					updateChart();
				}
			}
		});

		return text;
	}

	private void createTabFolderCharts(Composite parent) {

		TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBackgroundMode(SWT.INHERIT_DEFAULT);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		createScanChartComparison(tabFolder);
		createScanChartStacked(tabFolder);

		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean enabled = tabFolder.getSelectionIndex() == 0;
				buttonLegendControl.get().setEnabled(enabled);
			}
		});

		tabFolderControl.set(tabFolder);
	}

	private void createScanChartComparison(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Comparison");

		ScanChartUI scanChartUI = new ScanChartUI(tabFolder, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));

		tabItem.setControl(scanChartUI);
		scanChartControl.set(scanChartUI);
	}

	private void createScanChartStacked(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Stacked");

		Composite composite = new Composite(tabFolder, SWT.BORDER);
		composite.setLayout(new GridLayout(1, true));

		ScanChartUI scanChartUnknown = createScanChartStacked(composite);
		ScanChartUI scanChartReference = createScanChartStacked(composite);
		scanChartReference.addLinkedScrollableChart(scanChartUnknown);

		scanChartStackUnknownControl.set(scanChartUnknown);
		scanChartStackReferenceControl.set(scanChartReference);
		tabItem.setControl(composite);
	}

	private ScanChartUI createScanChartStacked(Composite parent) {

		ScanChartUI scanChartUI = new ScanChartUI(parent, SWT.NONE);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		scanChartUI.setChartType(ChartType.BAR);
		scanChartUI.setDataType(DataType.MSD_NOMINAL);
		scanChartUI.setSignalType(SignalType.CENTROID);
		scanChartUI.setLabelOption(LabelOption.NOMIMAL);
		scanChartUI.activateLabelMarkerX();
		/*
		 * Settings
		 */
		IChartSettings chartSettings = scanChartUI.getChartSettings();
		chartSettings.setHorizontalSliderVisible(false);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.getPrimaryAxisSettingsX().setVisible(false);
		chartSettings.getPrimaryAxisSettingsY().setVisible(false);
		for(ISecondaryAxisSettings secondaryAxisSettings : chartSettings.getSecondaryAxisSettingsListY()) {
			secondaryAxisSettings.setVisible(false);
		}
		scanChartUI.applySettings(chartSettings);

		return scanChartUI;
	}

	private void createToolbarInfoTop(Composite parent) {

		toolbarInfoTop.set(createToolbarInfo(parent));
	}

	private void createToolbarInfoBottom(Composite parent) {

		toolbarInfoBottom.set(createToolbarInfo(parent));
	}

	private InformationUI createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return informationUI;
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chart.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
	}

	private Button createSaveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Save both mass spectra.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				saveMassSpectrum(scanUnknown, "UnknownMS");
				saveMassSpectrum(scanReference, "ReferenceMS");
			}
		});
		return button;
	}

	private void saveMassSpectrum(IScanMSD scanMSD, String fileName) {

		if(scanMSD != null) {
			try {
				DatabaseFileSupport.saveMassSpectrum(DisplayUtils.getShell(), scanMSD, fileName);
			} catch(NoConverterAvailableException e1) {
				logger.warn(e1);
			}
		}
	}

	private void createButtonToggleHybrid(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarHybridSearch, IMAGE_HYBRID, TOOLTIP_HYBRID);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent s) {

				updateChart();
			}
		});

		buttonToolbarHybrid.set(button);
	}

	private void createButtonSubtractReference(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Subtract the reference spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_SCAN_DEFAULT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IScanMSD scanMSD = subtractScanMSD(scanUnknown);
				scanUnknown = copyScan(scanMSD, false);
				if(scanUnknownMaster != null) {
					scanUnknownMaster.setOptimizedMassSpectrum(scanUnknown);
				}
				useOptimizedSpectrum = true;
				updateButtonOptimized(buttonUseOptimizedScan.get());
				updateInput();
			}
		});

		buttonSubtractReference.set(button);
	}

	private IScanMSD subtractScanMSD(IScanMSD scanSource) {

		/*
		 * Settings
		 */
		MassSpectrumFilterSettings settings = new MassSpectrumFilterSettings();
		settings.setUseNominalMasses(isUseNominalMZ());
		settings.setUseNormalize(isUseNormalizedScan());
		settings.setSubtractMassSpectrum(scanReference);
		/*
		 * Subtract
		 */
		SubtractCalculator subtractCalculator = new SubtractCalculator();
		subtractCalculator.subtractMassSpectrum(scanSource, settings);
		scanSource = copyScan(scanSource);

		return scanSource;
	}

	private void createButtonOptimizedSpectrum(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		button.setToolTipText("Use the optimized mass spectrum if available.");
		button.setSelection(useOptimizedSpectrum);
		updateButtonOptimized(button);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent s) {

				useOptimizedSpectrum = button.getSelection();
				updateButtonOptimized(button);
				if(scanUnknownMaster != null) {
					scanUnknown = copyScan(scanUnknownMaster, false);
				}
				updateInput(scanUnknown, scanReference);
			}
		});

		buttonUseOptimizedScan.set(button);
	}

	private void createScanIdentifierUI(Composite parent) {

		ScanIdentifierUI scanIdentifierUI = new ScanIdentifierUI(parent, SWT.NONE);
		scanIdentifierUI.setUpdateListener(display -> {

			updateInput();
			if(scanUnknownMaster != null) {
				UpdateNotifierUI.update(display, scanUnknownMaster);
				UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Scan Chart identification has been performed.");
				ChromatogramUpdateSupport.fireUpdateChromatogramSelection(display, scanUnknownMaster);
			}
		});

		scanIdentifierControl.set(scanIdentifierUI);
	}

	private void updateButtonOptimized(Button button) {

		setButtonImage(button, IApplicationImage.IMAGE_PLUS, PREFIX_ENABLE, PREFIX_DISABLE, "using the optimized mass spectrum if available.", useOptimizedSpectrum);
	}

	private void createButtonMirroredSpectrum(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		button.setToolTipText("Show the reference in mirrored modus.");
		updateButtonMirrored(button);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent s) {

				useMirroredSpectrum = !useMirroredSpectrum;
				updateButtonMirrored(button);
				updateInput();
			}
		});

		buttonMirroredReference.set(button);
	}

	private void updateButtonMirrored(Button button) {

		setButtonImage(button, IApplicationImage.IMAGE_MIRRORED_MASS_SPECTRUM, PREFIX_ENABLE, PREFIX_DISABLE, "showing the reference in mirrored modus.", useMirroredSpectrum);
	}

	private void createButtonDifferenceSpectrum(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		button.setToolTipText("Show both unknown and reference in difference modus.");
		updateButtonDifference(button);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent s) {

				showDifferenceSpectrum = !showDifferenceSpectrum;
				updateButtonDifference(button);
				updateInput();
			}
		});

		buttonDifferenceSpectrum.set(button);
	}

	private void updateButtonDifference(Button button) {

		setButtonImage(button, IApplicationImage.IMAGE_SUBTRACT_SCAN_DEFAULT, PREFIX_ENABLE, PREFIX_DISABLE, "showing the difference spectrum.", showDifferenceSpectrum);
	}

	private void createButtonShiftReferenceSpectrum(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		button.setToolTipText("Shift the reference spectrum.");
		updateButtonShiftReference(button);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent s) {

				shiftReferenceSpectrum = !shiftReferenceSpectrum;
				updateButtonShiftReference(button);
				updateInput();
			}
		});

		buttonShiftReferenceSpectrum.set(button);
	}

	private void updateButtonShiftReference(Button button) {

		setButtonImage(button, IApplicationImage.IMAGE_SHIFTED_MASS_SPECTRUM, PREFIX_ENABLE, PREFIX_DISABLE, "showing the reference spectrum with a mass shift.", shiftReferenceSpectrum);
	}

	private void createSpinnerShiftReferenceSpectrum(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setMaximum(50);
		spinner.setPageIncrement(1);
		spinner.setSelection(shiftMass);
		spinner.setToolTipText("Determine the shitf mass.");
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		spinner.setLayoutData(gridData);

		spinner.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					updateShiftMass();
				}
			}
		});

		spinner.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				if(e.button == 1) {
					updateShiftMass();
				}
			}
		});

		spinnerShiftReferenceSpectrum.set(spinner);
	}

	private void updateShiftMass() {

		shiftMass = spinnerShiftReferenceSpectrum.get().getSelection();
		updateInput();
	}

	private void createToggleLegendButton(Composite parent) {

		buttonLegendControl.set(createButtonToggleChartLegend(parent, scanChartControl, IMAGE_LEGEND));
	}

	private void createSettingsButton(Composite parent) {

		Button button = createSettingsButtonBasic(parent);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				/*
				 * Dynamically show different settings, based on the selected scan type.
				 */
				List<Class<? extends IPreferencePage>> preferencePages = getPreferencePages();
				showPreferencesDialog(event, preferencePages, _ -> applySettings(), true);
				adjustAxisSettings();
			}
		});
	}

	private List<Class<? extends IPreferencePage>> getPreferencePages() {

		/*
		 * Default pages
		 */
		List<Class<? extends IPreferencePage>> preferencePages = new ArrayList<>();
		preferencePages.add(PreferencePageScans.class);
		preferencePages.add(ScanChartAxisIon.class);
		preferencePages.add(ScanChartAxisIntensity.class);
		preferencePages.add(ScanChartAxisRelativeIntensity.class);
		preferencePages.add(PreferencePageSubtract.class);

		return preferencePages;
	}

	private void updateInput(IScanMSD unknownMassSpectrum, IScanMSD referenceMassSpectrum) {

		scanUnknown = copyScan(unknownMassSpectrum);
		scanReference = copyScan(referenceMassSpectrum);
		updateInput();
	}

	private void updateInput() {

		updateWidgets();
		Display.getDefault().asyncExec(this::updateChart);
	}

	private void updateWidgets() {

		boolean enabled = scanUnknown != null && scanReference != null;
		toolbarHybridSearch.get().setEnabled(enabled);
		buttonSubtractReference.get().setEnabled(enabled);
		buttonAutoAdjustUnknownControl.get().setEnabled(enabled);
		updateIdentifierControl();
	}

	private void updateIdentifierControl() {

		scanIdentifierControl.get().setInput(scanUnknown);
		scanIdentifierControl.get().setEnabled(scanUnknown != null);
	}

	private void reset() {

		updateChart();
	}

	private void applySettings() {

		scanIdentifierControl.get().updateIdentifier();
		updateChart();
	}

	private void assignScan(IScanMSD scanMSD, boolean unknown) {

		if(scanMSD == null) {
			if(unknown) {
				scanUnknown = null;
			} else {
				scanReference = null;
			}
		} else {
			IScanMSD copy = copyScan(scanMSD);
			if(unknown) {
				scanUnknown = copy;
			} else {
				scanReference = copy;
			}
		}
	}

	private void updateScanComparisonDifference() {

		updateToolbarInfoDifference();
		IExtractedIonSignal extractedIonSignalReference = scanUnknown.getExtractedIonSignal();
		IExtractedIonSignal extractedIonSignalComparison = scanReference.getExtractedIonSignal();
		int startIon = (extractedIonSignalReference.getStartIon() < extractedIonSignalComparison.getStartIon()) ? extractedIonSignalReference.getStartIon() : extractedIonSignalComparison.getStartIon();
		int stopIon = (extractedIonSignalReference.getStopIon() > extractedIonSignalComparison.getStopIon()) ? extractedIonSignalReference.getStopIon() : extractedIonSignalComparison.getStopIon();

		IScanMSD scanDifference1 = new ScanMSD();
		IScanMSD scanDifference2 = new ScanMSD();

		for(int ion = startIon; ion <= stopIon; ion++) {
			float abundance = extractedIonSignalReference.getAbundance(ion) - extractedIonSignalComparison.getAbundance(ion);
			if(abundance > 0) {
				scanDifference1.addIon(getIon(ion, abundance));
			} else if(abundance < 0) {
				abundance *= -1;
				if(shiftReferenceSpectrum) {
					scanDifference2.addIon(getIon(ion + shiftMass, abundance));
				} else {
					scanDifference2.addIon(getIon(ion, abundance));
				}
			}
		}

		scanChartControl.get().setInput(scanDifference1, scanDifference2, useMirroredSpectrum);
	}

	private IIon getIon(int mz, float abundance) {

		return new Ion(mz, abundance);
	}

	private void updateToolbarInfoDifference() {

		updateToolbarInfoSpecial(PREFIX_UR, PREFIX_UR);
	}

	private void updateToolbarInfoSpecial(String prefixUnknown, String prefixReference) {

		toolbarInfoTop.get().setText(scanDataSupport.getMassSpectrumLabel(scanUnknown, prefixUnknown, TITLE_UNKNOWN, POSTFIX_NONE));
		toolbarInfoBottom.get().setText(scanDataSupport.getMassSpectrumLabel(scanReference, prefixReference, TITLE_REFERENCE, shiftReferenceSpectrum ? POSTFIX_SHIFTED + " (" + shiftMass + ")" : POSTFIX_NONE));
	}

	private void updateScanNormal() {

		toolbarInfoTop.get().setText("");
		toolbarInfoBottom.get().setText("");

		if(scanUnknown != null) {
			toolbarInfoTop.get().setText(scanDataSupport.getMassSpectrumLabel(scanUnknown, PREFIX_U, TITLE_UNKNOWN, POSTFIX_NONE));
			scanChartControl.get().setInput(scanUnknown);
		} else if(scanReference != null) {
			IScanMSD secondScan = scanReference;
			toolbarInfoTop.get().setText(scanDataSupport.getMassSpectrumLabel(secondScan, PREFIX_U, TITLE_UNKNOWN, POSTFIX_NONE));
			scanChartControl.get().setInput(secondScan);
		} else {
			scanChartControl.get().setInput(null);
		}
	}

	private void updateOnFocus() {

		DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
		String topic = getLastTopic(dataUpdateSupport.getTopics());
		List<Object> objects = dataUpdateSupport.getUpdates(topic);
		if(!objects.isEmpty()) {
			Object last = objects.get(0);
			if(last instanceof IScanMSD scanMSD) {
				IIdentificationTarget identificationTarget = IIdentificationTarget.getIdentificationTarget(scanMSD);
				update(scanMSD, identificationTarget);
			} else if(last instanceof IPeakMSD peakMSD) {
				IIdentificationTarget identificationTarget = IIdentificationTarget.getIdentificationTarget(peakMSD);
				update(peakMSD.getExtractedMassSpectrum(), identificationTarget);
			} else if(last instanceof Object[] values) {
				Object first = values[0];
				Object second = values[1];
				if(IChemClipseEvents.TOPIC_SCAN_TARGET_UPDATE_COMPARISON.equals(topic)) {
					if(first instanceof IScanMSD unknownMassSpectrum && second instanceof IIdentificationTarget identificationTarget) {
						update(unknownMassSpectrum, identificationTarget);
					}
				} else if(IChemClipseEvents.TOPIC_SCAN_REFERENCE_UPDATE_COMPARISON.equals(topic)) {
					if(first instanceof IScanMSD unknownMassSpectrum && second instanceof IScanMSD referenceMassSpectrum) {
						updateInput(unknownMassSpectrum, referenceMassSpectrum);
					}
				}
			}
		}
	}

	private IScanMSD copyScan(IScanMSD scanMSD) {

		return copyScan(scanMSD, useOptimizedSpectrum);
	}

	private IScanMSD copyScan(IScanMSD scanMSD, boolean useOptimizedSpectrum) {

		if(scanMSD != null) {
			try {
				IScanMSD massSpectrum = scanMSD;
				if(useOptimizedSpectrum) {
					IScanMSD massSpectrumOptimized = scanMSD.getOptimizedMassSpectrum();
					if(massSpectrumOptimized != null) {
						massSpectrum = massSpectrumOptimized;
					}
				}
				return massSpectrum.makeDeepCopy().normalize(NORMALIZATION_FACTOR);
			} catch(CloneNotSupportedException e) {
			}
		}

		return null;
	}

	private void updateChart() {

		if(scanUnknown != null && scanReference != null) {
			if(showDifferenceSpectrum) {
				updateScanComparisonDifference();
			} else {
				updateScanComparisonNormal();
			}
			updateStackCharts();
		} else {
			updateScanNormal();
			clearStackCharts();
		}
		adjustAxisSettings();
	}

	private String getLastTopic(List<String> topics) {

		Collections.reverse(topics);
		for(String topic : topics) {
			if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION)) {
				return topic;
			}
			if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION)) {
				return topic;
			}
			if(topic.equals(IChemClipseEvents.TOPIC_SCAN_TARGET_UPDATE_COMPARISON)) {
				return topic;
			}
			if(topic.equals(IChemClipseEvents.TOPIC_SCAN_REFERENCE_UPDATE_COMPARISON)) {
				return topic;
			}
		}

		return "";
	}

	private void adjustAxisSettings() {

		adjustAxisIons();
		adjustAxisIntensity();
		adjustAxisRelativeIntensity();

		IChartSettings chartSettings = scanChartControl.get().getChartSettings();
		scanChartControl.get().applySettings(chartSettings);
	}

	private void adjustAxisIons() {

		IChartSettings chartSettings = scanChartControl.get().getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle(ExtensionMessages.ion);

		String positionNode = PreferenceSupplier.P_SCAN_CHART_POSITION_X_AXIS_IONS;
		String gridLineStyleNode = PreferenceSupplier.P_SCAN_CHART_GRIDLINE_STYLE_X_AXIS_IONS;

		ChartSupport.setAxisSettingsExtended(primaryAxisSettingsX, positionNode, "0", gridLineStyleNode);
		ChartSupport.themeAxis(primaryAxisSettingsX, ExtendedScanChartUI.class.getName() + ".AxisIons");
		primaryAxisSettingsX.setVisible(ChartSupport.getBoolean(PreferenceSupplier.P_SCAN_CHART_SHOW_X_AXIS_IONS));
		primaryAxisSettingsX.setTitleVisible(ChartSupport.getBoolean(PreferenceSupplier.P_SCAN_CHART_SHOW_X_AXIS_TITLE_IONS));
	}

	private void adjustAxisIntensity() {

		IChartSettings chartSettings = scanChartControl.get().getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(ExtensionMessages.intensity);

		String positionNode = PreferenceSupplier.P_SCAN_CHART_POSITION_Y_AXIS_INTENSITY;
		String patternNode = PreferenceSupplier.P_SCAN_CHART_FORMAT_Y_AXIS_INTENSITY;
		String gridLineStyleNode = PreferenceSupplier.P_SCAN_CHART_GRIDLINE_STYLE_Y_AXIS_INTENSITY;

		ChartSupport.setAxisSettingsExtended(primaryAxisSettingsY, positionNode, patternNode, gridLineStyleNode);
		ChartSupport.themeAxis(primaryAxisSettingsY, ExtendedScanChartUI.class.getName() + ".AxisIntensity");
		primaryAxisSettingsY.setVisible(ChartSupport.getBoolean(PreferenceSupplier.P_SCAN_CHART_SHOW_Y_AXIS_INTENSITY));
		primaryAxisSettingsY.setTitleVisible(ChartSupport.getBoolean(PreferenceSupplier.P_SCAN_CHART_SHOW_Y_AXIS_TITLE_INTENSITY));
	}

	private void adjustAxisRelativeIntensity() {

		IChartSettings chartSettings = scanChartControl.get().getChartSettings();
		ISecondaryAxisSettings axisSettings = ChartSupport.getSecondaryAxisSettingsY(ExtensionMessages.relativeIntensity, chartSettings);

		String positionNode = PreferenceSupplier.P_SCAN_CHART_POSITION_Y_AXIS_RELATIVE_INTENSITY;
		String patternNode = PreferenceSupplier.P_SCAN_CHART_FORMAT_Y_AXIS_RELATIVE_INTENSITY;
		String gridLineStyleNode = PreferenceSupplier.P_SCAN_CHART_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY;

		boolean isShowAxis = ChartSupport.getBoolean(PreferenceSupplier.P_SCAN_CHART_SHOW_Y_AXIS_RELATIVE_INTENSITY);
		boolean isShowAxisTitle = ChartSupport.getBoolean(PreferenceSupplier.P_SCAN_CHART_SHOW_Y_AXIS_TITLE_RELATIVE_INTENSITY);

		if(isShowAxis) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(ExtensionMessages.relativeIntensity, new PercentageConverter(SWT.VERTICAL, true));
				ChartSupport.setAxisSettingsExtended(secondaryAxisSettingsY, positionNode, patternNode, gridLineStyleNode);
				ChartSupport.themeAxis(secondaryAxisSettingsY, ExtendedScanChartUI.class.getName() + ".AxisRelativeIntensity");
				secondaryAxisSettingsY.setTitleVisible(isShowAxisTitle);
				chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
			} else {
				ChartSupport.setAxisSettingsExtended(axisSettings, positionNode, patternNode, gridLineStyleNode);
				ChartSupport.themeAxis(axisSettings, ExtendedScanChartUI.class.getName() + ".AxisRelativeIntensity");
				axisSettings.setTitle(ExtensionMessages.relativeIntensity);
				axisSettings.setVisible(true);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setTitle(ExtensionMessages.relativeIntensity);
				axisSettings.setVisible(false);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		}
	}
}