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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import static org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.getCalculationType;
import static org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.getSessionSubtractMassSpectrum;
import static org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan;
import static org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUsePeaksInsteadOfScans;
import static org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.setSessionSubtractMassSpectrum;
import static org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.storeSessionSubtractMassSpectrum;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.support.FilterSupport;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSubtract;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ScanChartAxisIntensity;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ScanChartAxisIon;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ScanChartAxisRelativeIntensity;
import org.eclipse.chemclipse.vsd.model.core.selection.IChromatogramSelectionVSD;
import org.eclipse.chemclipse.vsd.model.support.FilterSupportVSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;

public class ExtendedCombinedScanUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedCombinedScanUI.class);

	private static final int INDEX_CHART = 0;
	private static final int INDEX_TABLE = 1;
	private static final int INDEX_TARGETS = 2;

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private TabFolder tabFolder;
	private ScanChartUI scanChartUI;
	private ScanTableUI scanTableUI;
	private AtomicReference<TargetsListUI> tableViewer = new AtomicReference<>();
	private CLabel labelEdit;
	private AtomicReference<TracesClipboardUI> tracesClipboardControl = new AtomicReference<>();
	private Button buttonLocked;
	private ScanIdentifierUI scanIdentifierUI;
	/*
	 * MSD, VSD
	 */
	private IChromatogramSelection chromatogramSelection = null;
	private IScan combinedScan = null;
	private boolean locked = false;

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public ExtendedCombinedScanUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public boolean setFocus() {

		updateScan();
		return true;
	}

	public void updateSelection() {

		update(chromatogramSelection);
	}

	public void update(Object object) {

		if(!locked) {
			chromatogramSelection = null;
			combinedScan = null;

			CalculationType calculationType = getCalculationType();
			if(object instanceof IChromatogramSelectionMSD chromatogramSelectionMSD) {
				this.chromatogramSelection = chromatogramSelectionMSD;
				boolean useNormalize = isUseNormalizedScan();
				boolean usePeaksInsteadOfScans = isUsePeaksInsteadOfScans();
				combinedScan = FilterSupport.getCombinedMassSpectrum(chromatogramSelectionMSD, null, useNormalize, calculationType, usePeaksInsteadOfScans);
			} else if(object instanceof IChromatogramSelectionVSD chromatogramSelectionVSD) {
				this.chromatogramSelection = chromatogramSelectionVSD;
				combinedScan = FilterSupportVSD.getCombinedSpectrum(chromatogramSelectionVSD, false, calculationType);
			}

			toolbarInfo.get().setText(getCombinedRangeInfo(object));
			scanIdentifierUI.setInput(combinedScan);
			updateScan();
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));

		createToolbarMain(this);
		createToolbarInfo(this);
		createTabFolderSection(this);

		initialize();
	}

	private void initialize() {

		/*
		 * The space is limited, hence deactivate the info toolbar by default.
		 */
		enableToolbar(toolbarInfo, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, false);
		locked = false;
		updateStatus();
		adjustAxisSettings();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(8, false));

		labelEdit = createInfoLabelEdit(composite);
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		buttonLocked = createButtonLocked(composite);
		scanIdentifierUI = createScanIdentifierUI(composite);
		createTracesClipboardUI(composite);
		createButtonAddSubstract(composite);
		createButtonSave(composite);
		createButtonSettings(composite);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		toolbarInfo.set(informationUI);
	}

	private void createTabFolderSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));

		tabFolder = new TabFolder(composite, SWT.BOTTOM);
		tabFolder.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateScan();
			}
		});

		createScanChart(tabFolder);
		createScanTable(tabFolder);
		createTargetsTable(tabFolder);
	}

	private void createScanChart(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Chart");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);

		scanChartUI = new ScanChartUI(composite, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createScanTable(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Table");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);

		scanTableUI = new ScanTableUI(composite, SWT.VIRTUAL | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		scanTableUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createTargetsTable(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Targets");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);

		TargetsListUI targetsListUI = new TargetsListUI(composite, SWT.BORDER);
		targetsListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		targetsListUI.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				propagateTarget(e.display);
			}
		});

		tableViewer.set(targetsListUI);
	}

	private void propagateTarget(Display display) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			Table table = tableViewer.get().getTable();
			int index = table.getSelectionIndex();
			if(index >= 0) {
				TableItem tableItem = table.getItem(index);
				Object object = tableItem.getData();
				if(object instanceof IIdentificationTarget identificationTarget) {
					/*
					 * First update the mass spectrum.
					 */
					if(combinedScan != null) {
						UpdateNotifierUI.update(display, combinedScan, identificationTarget);
					}
					UpdateNotifierUI.update(display, identificationTarget);
				}
			}
		}
	}

	private CLabel createInfoLabelEdit(Composite parent) {

		CLabel label = createInfoLabel(parent);
		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				if(!"".equals(label.getText())) {
					locked = false;
					updateStatus();
				}
			}
		});
		return label;
	}

	private CLabel createInfoLabel(Composite parent) {

		CLabel label = new CLabel(parent, SWT.CENTER);
		label.setForeground(Colors.RED);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return label;
	}

	private Button createButtonLocked(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		button.setToolTipText("Lock the combined scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				locked = !locked;
				updateStatus();
			}
		});
		return button;
	}

	private void updateStatus() {

		buttonLocked.setToolTipText(locked ? "Edit modus: on" : "Edit modus: off");
		buttonLocked.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImageProvider.SIZE_16x16));
		buttonLocked.setSelection(locked);
		updateLabel(labelEdit, locked ? "Edit On" : "");
		updateButtons();
	}

	private void updateLabel(CLabel label, String message) {

		label.setText(message);
		if("".equals(message)) {
			label.setBackground(getBackground());
		} else {
			label.setBackground(Colors.LIGHT_YELLOW);
		}
	}

	private ScanIdentifierUI createScanIdentifierUI(Composite parent) {

		ScanIdentifierUI scanIdentifierUI = new ScanIdentifierUI(parent, SWT.NONE);
		scanIdentifierUI.setUpdateListener(_ -> {

			if(combinedScan != null) {
				tabFolder.setSelection(INDEX_TARGETS);
				updateScan();
			}
		});

		return scanIdentifierUI;
	}

	private void createTracesClipboardUI(Composite parent) {

		TracesClipboardUI tracesClipboardUI = new TracesClipboardUI(parent, SWT.NONE);
		tracesClipboardControl.set(tracesClipboardUI);
	}

	private void createButtonAddSubstract(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add combined scan to the subtract spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_ADD_COMBINED_SCAN, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(combinedScan instanceof ICombinedMassSpectrum combinedMassSpectrum) {
					boolean useNormalize = isUseNormalizedScan();
					CalculationType calculationType = getCalculationType();
					IScanMSD massSpectrum1 = getSessionSubtractMassSpectrum();
					IScanMSD massSpectrum2 = combinedMassSpectrum;
					IScanMSD subtractMassSpectrum = FilterSupport.getCombinedMassSpectrum(massSpectrum1, massSpectrum2, null, useNormalize, calculationType);
					saveSessionMassSpectrum(e.display, subtractMassSpectrum);
				}
			}
		});
	}

	/**
	 * If the display is set to null, no event is fired.
	 *
	 * @param display
	 * @param scanMSD
	 */
	private void saveSessionMassSpectrum(Display display, IScanMSD scanMSD) {

		setSessionSubtractMassSpectrum(scanMSD);
		storeSessionSubtractMassSpectrum();

		if(display != null) {
			fireUpdateEvent(display);
		}
	}

	private void fireUpdateEvent(Display display) {

		UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
	}

	private Button createButtonSave(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Save the combined scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					if(combinedScan instanceof ICombinedMassSpectrum combinedMassSpectrum) {
						DatabaseFileSupport.saveMassSpectrum(button.getShell(), combinedMassSpectrum, "CombinedScan");
					}
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
		return button;
	}

	private void createButtonSettings(Composite parent) {

		List<Class<? extends IPreferencePage>> preferencePages = getPreferencePages();
		createSettingsButton(parent, preferencePages, _ -> applySettings());
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

	private void applySettings() {

		update(chromatogramSelection);
		scanIdentifierUI.updateIdentifier();
		tracesClipboardControl.get().updateOption();
	}

	private void updateScan() {

		switch(tabFolder.getSelectionIndex()) {
			case INDEX_CHART:
				if(scanChartUI != null) {
					scanChartUI.setInput(combinedScan);
				}
				break;
			case INDEX_TABLE:
				if(scanTableUI != null) {
					scanTableUI.setInput(combinedScan);
				}
				break;
			case INDEX_TARGETS:
				if(tableViewer.get() != null) {
					tableViewer.get().setInput(combinedScan.getTargets());
				}
				break;
		}

		updateButtons();
		adjustAxisSettings();
	}

	private String getCombinedRangeInfo(Object object) {

		StringBuilder builder = new StringBuilder();
		if(object instanceof IChromatogramSelection chromatogramSelection) {
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			builder.append("Scan range: ");
			builder.append(chromatogram.getScanNumber(startRetentionTime));
			builder.append("–");
			builder.append(chromatogram.getScanNumber(stopRetentionTime));
			builder.append(" | RT range: ");
			builder.append(decimalFormat.format(startRetentionTime / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			builder.append("–");
			builder.append(decimalFormat.format(stopRetentionTime / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			builder.append(" | Calculation Type: ");
			builder.append(getCalculationType().toString());
		} else {
			builder.append("No chromatogram selected.");
		}
		return builder.toString();
	}

	private void updateButtons() {

		boolean enabled = combinedScan instanceof ICombinedMassSpectrum;
		tracesClipboardControl.get().setInput(combinedScan);
		tracesClipboardControl.get().setEnabled(enabled);
	}

	private void adjustAxisSettings() {

		adjustAxisIons();
		adjustAxisIntensity();
		adjustAxisRelativeIntensity();

		IChartSettings chartSettings = scanChartUI.getChartSettings();
		scanChartUI.applySettings(chartSettings);
	}

	private void adjustAxisIons() {

		IChartSettings chartSettings = scanChartUI.getChartSettings();
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

		IChartSettings chartSettings = scanChartUI.getChartSettings();
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

		IChartSettings chartSettings = scanChartUI.getChartSettings();
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