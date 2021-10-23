/*******************************************************************************
 * Copyright (c) 2017, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for configuration, improve user feedback for unsaved changes
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.calculator.SubtractCalculator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.support.ScanSupport;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.SignalType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.parts.ScanChartPart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSubtract;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.SubtractScanWizard;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.support.WavelengthSupport;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ExtendedScanChartUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ScanChartPart.class);
	/*
	 * The event broker should be set, but it
	 * could be null if no events shall be fired.
	 */
	private IEventBroker eventBroker;
	//
	private AtomicReference<Composite> toolbarMain = new AtomicReference<>();
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Button buttonToolbarEdit;
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	private Button buttonToolbarTypes;
	private AtomicReference<Composite> toolbarTypes = new AtomicReference<>();
	//
	private CLabel labelEdit;
	private CLabel labelSubtract;
	private CLabel labelOptimized;
	//
	private Button buttonCopyTraces;
	private Button buttonSave;
	private Button buttonDeleteOptimized;
	//
	private Button buttonChartGrid;
	private AtomicReference<ScanChartUI> chartControl = new AtomicReference<>();
	private ChartGridSupport chartGridSupport = new ChartGridSupport();
	//
	private Combo comboDataType;
	private Combo comboSignalType;
	//
	private Button buttonSubtractOption;
	private ScanFilterUI scanFilterUI;
	private ScanIdentifierUI scanIdentifierUI;
	//
	private IScan scan;
	//
	private final ScanDataSupport scanDataSupport = new ScanDataSupport();
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private boolean editModus = false;
	private boolean subtractModus = false;
	private Color backgroundDefault;

	public ExtendedScanChartUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setEventBroker(IEventBroker eventBroker) {

		this.eventBroker = eventBroker;
	}

	public void update(IScan scan) {

		this.update(scan, DisplayUtils.getDisplay());
	}

	/**
	 * Getting the updates from the system.
	 * 
	 * @param scan
	 */
	private void update(IScan scan, Display display) {

		if(editModus) {
			if(subtractModus) {
				if(this.scan instanceof IScanMSD) {
					IScanMSD scanSource = (IScanMSD)this.scan;
					if(scan instanceof IScanMSD) {
						/*
						 * Just subtract a mass spectrum once.
						 * Otherwise, following updates would lead
						 * to subsequent subtractions.
						 */
						IScanMSD scanSubtract = (IScanMSD)scan;
						int scanNumberSource = scanSource.getScanNumber();
						int scanNumberSubtract = scanSubtract.getScanNumber();
						if(scanNumberSubtract > 0 && scanNumberSource != scanNumberSubtract) {
							/*
							 * Prevent the scan is subtracted from itself.
							 */
							logger.info("Subtract Scan: " + scanNumberSource + " - " + scanNumberSubtract);
							subtractScanMSD(scanSource, scanSubtract);
							if(!preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_MULTI_SUBTRACT)) {
								setSubtractModus(display, false, false);
								updateInfoLabels();
							}
							fireUpdateChromatogramSelection(display, scanSource);
							updateScan(scanSource);
						}
					}
				}
			} else {
				/*
				 * Updates are disabled in "Edit Modus".
				 * Scan updates are fired regularly in the platform. We don't want to show a recurring
				 * dialog here, that the edit modus is activated. Maybe, it could be display once.
				 */
				// MessageDialog.openInformation(display, "Edit Modus", "To retrieve updates, please disable the edit modus.");
			}
		} else {
			updateScan(scan);
		}
	}

	private void updateScan(IScan scan) {

		this.scan = scan;
		scanFilterUI.setInput(scan);
		scanIdentifierUI.setInput(scan);
		toolbarInfo.get().setText(scanDataSupport.getScanLabel(scan));
		setDetectorSignalType(scan);
		updateScanChart(scan);
		updateInfoLabels();
		updateButtons();
	}

	private void updateScanChart(IScan scan) {

		IScanMSD optimizedMassSpectrum = getOptimizedScanMSD();
		ScanChartUI scanChartUI = chartControl.get();
		//
		if(optimizedMassSpectrum != null) {
			scanChartUI.setInput(optimizedMassSpectrum);
		} else {
			scanChartUI.setInput(scan);
		}
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
		GridLayout layout = new GridLayout(1, true);
		composite.setLayout(layout);
		backgroundDefault = composite.getBackground();
		//
		createToolbarMain(composite);
		createToolbarInfo(composite);
		createToolbarTypes(composite);
		createToolbarEdit(composite);
		createScanChart(composite);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarTypes, buttonToolbarTypes, IMAGE_TYPES, TOOLTIP_TYPES, false);
		enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
		enableChartGrid(chartControl, buttonChartGrid, IMAGE_CHART_GRID, chartGridSupport);
		//
		updateButtons();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(13, false));
		//
		labelEdit = createInfoLabelEdit(composite);
		labelSubtract = createInfoLabelSubtract(composite);
		labelOptimized = createInfoLabelOptimized(composite);
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarTypes = createButtonToggleToolbar(composite, toolbarTypes, IMAGE_TYPES, TOOLTIP_TYPES);
		buttonToolbarEdit = createButtonToggleToolbar(composite, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT);
		scanIdentifierUI = createScanIdentifierUI(composite);
		buttonCopyTraces = createButtonCopyTracesClipboard(composite);
		createResetButton(composite);
		buttonSave = createSaveButton(composite);
		buttonDeleteOptimized = createDeleteOptimizedButton(composite);
		buttonChartGrid = createButtonToggleChartGrid(composite, chartControl, IMAGE_CHART_GRID, chartGridSupport);
		createSettingsButton(composite);
		//
		buttonToolbarEdit.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = toolbarEdit.get().isVisible();
				setEditToolbarStatus(visible);
				updateInfoLabels();
			}
		});
		//
		toolbarMain.set(composite);
	}

	private CLabel createInfoLabelEdit(Composite parent) {

		CLabel label = createInfoLabel(parent);
		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				if(!"".equals(label.getText())) {
					setEditToolbarStatus(false);
					updateInfoLabels();
				}
			}
		});
		return label;
	}

	private CLabel createInfoLabelSubtract(Composite parent) {

		CLabel label = createInfoLabel(parent);
		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				if(!"".equals(label.getText())) {
					setSubtractModus(e.display, false, false);
					updateInfoLabels();
				}
			}
		});
		return label;
	}

	private CLabel createInfoLabelOptimized(Composite parent) {

		CLabel label = createInfoLabel(parent);
		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				if(!"".equals(label.getText())) {
					deleteOptimizedScan(e.widget.getDisplay());
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

	private void createToolbarTypes(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(2, true));
		//
		comboDataType = createDataType(composite);
		comboDataType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboSignalType = createSignalType(composite);
		comboSignalType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarTypes.set(composite);
	}

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(2, false));
		//
		buttonSubtractOption = createButtonSubtractOption(composite);
		scanFilterUI = createScanFilterUI(composite);
		//
		toolbarEdit.set(composite);
	}

	private Button createButtonSubtractOption(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Enable/Disable the interactive subtract option.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_SCAN_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setSubtractModus(e.display, !subtractModus, true);
				updateInfoLabels();
			}
		});
		return button;
	}

	private void setSubtractModus(Display display, boolean subtractModus, boolean showDialog) {

		this.subtractModus = subtractModus;
		String fileName = this.subtractModus ? IApplicationImage.IMAGE_SUBTRACT_SCAN_ACTIVE : IApplicationImage.IMAGE_SUBTRACT_SCAN_DEFAULT;
		buttonSubtractOption.setImage(ApplicationImageFactory.getInstance().getImage(fileName, IApplicationImage.SIZE_16x16));
		//
		if(this.subtractModus && showDialog) {
			if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_SUBTRACT_DIALOG)) {
				if(display != null) {
					SubtractScanWizard.openWizard(display.getActiveShell());
				}
			}
		}
	}

	private ScanFilterUI createScanFilterUI(Composite parent) {

		ScanFilterUI scanFilterUI = new ScanFilterUI(parent, SWT.NONE);
		scanFilterUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		scanFilterUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update(Display display) {

				updateScan(scan);
			}
		});
		return scanFilterUI;
	}

	private ScanIdentifierUI createScanIdentifierUI(Composite parent) {

		ScanIdentifierUI scanIdentifierUI = new ScanIdentifierUI(parent, SWT.NONE);
		scanIdentifierUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update(Display display) {

				updateScan(scan);
				UpdateNotifierUI.update(display, scan);
				UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Scan Chart identification has been performed.");
			}
		});
		//
		return scanIdentifierUI;
	}

	private void subtractScanMSD(IScanMSD scanSource, IScanMSD scanSubtract) {

		/*
		 * Settings
		 */
		MassSpectrumFilterSettings settings = new MassSpectrumFilterSettings();
		settings.setUseNominalMasses(PreferenceSupplier.isUseNominalMZ());
		settings.setUseNormalize(PreferenceSupplier.isUseNormalizedScan());
		settings.setSubtractMassSpectrum(PreferenceSupplier.getMassSpectrum(scanSubtract));
		/*
		 * Subtract
		 */
		IScanMSD optimizedMassSpectrum = getOptimizedMassSpectrum(scanSource);
		SubtractCalculator subtractCalculator = new SubtractCalculator();
		subtractCalculator.subtractMassSpectrum(optimizedMassSpectrum, settings);
	}

	private IScanMSD getOptimizedMassSpectrum(IScanMSD scanMSD) {

		IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
		if(optimizedMassSpectrum == null) {
			try {
				optimizedMassSpectrum = scanMSD.makeDeepCopy();
				scanMSD.setOptimizedMassSpectrum(optimizedMassSpectrum);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		}
		//
		return optimizedMassSpectrum;
	}

	private Combo createDataType(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Data Type (MS, MS/MS, FID, DAD, ...)");
		combo.setItems(ScanDataSupport.DATA_TYPES_DEFAULT);
		combo.select(0);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String selection = combo.getText();
				chartControl.get().setDataType(DataType.valueOf(selection));
				updateScanChart(scan);
			}
		});
		return combo;
	}

	private Combo createSignalType(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Signal Type (Centroid: Bar Series, Profile: Line Series)");
		combo.setItems(ScanDataSupport.SIGNAL_TYPES_DEFAULT);
		combo.select(0);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String selection = combo.getText();
				chartControl.get().setSignalType(SignalType.valueOf(selection));
				updateScanChart(scan);
			}
		});
		return combo;
	}

	private void setEditToolbarStatus(boolean visible) {

		if(!visible) {
			boolean toolbarIsVisible = toolbarEdit.get().isVisible();
			if(toolbarIsVisible) {
				enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
			}
		}
		/*
		 * Set the edit modus and button icon.
		 */
		editModus = visible;
	}

	private void updateInfoLabels() {

		updateLabel(labelEdit, (editModus) ? "Edit On" : "");
		updateLabel(labelSubtract, (subtractModus) ? "Subtract On" : "");
		updateLabel(labelOptimized, (isOptimizedScan()) ? "Optimized" : "");
	}

	private boolean isMassSpectrum() {

		return scan instanceof IScanMSD;
	}

	private boolean isWaveSpectrum() {

		return scan instanceof IScanWSD;
	}

	private boolean isOptimizedScan() {

		IScanMSD optimizedMassSpectrum = getOptimizedScanMSD();
		return optimizedMassSpectrum != null;
	}

	private IScanMSD getScanMSD() {

		if(scan instanceof IScanMSD) {
			return (IScanMSD)scan;
		}
		return null;
	}

	private IScanMSD getOptimizedScanMSD() {

		IScanMSD scanMSD = getScanMSD();
		if(scanMSD != null) {
			IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
			if(optimizedMassSpectrum != null) {
				return optimizedMassSpectrum;
			}
		}
		return null;
	}

	private void updateLabel(CLabel label, String message) {

		label.setText(message);
		if("".equals(message)) {
			label.setBackground(backgroundDefault);
		} else {
			label.setBackground(Colors.YELLOW);
		}
	}

	private void updateButtons() {

		buttonChartGrid.setEnabled(true);
		boolean enabled = isMassSpectrum();
		//
		scanIdentifierUI.setEnabled(enabled || isWaveSpectrum());
		buttonCopyTraces.setEnabled(scan instanceof IScanMSD | scan instanceof IScanWSD);
		buttonSave.setEnabled(enabled);
		buttonDeleteOptimized.setEnabled(enabled && isOptimizedScan() ? true : false);
		buttonSubtractOption.setEnabled(enabled);
		scanFilterUI.setEnabled(enabled);
	}

	private Button createButtonCopyTracesClipboard(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Copy the traces to clipboard.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COPY_CLIPBOARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String traces = null;
				int maxCopyTraces = preferenceStore.getInt(PreferenceConstants.P_MAX_COPY_SCAN_TRACES);
				//
				if(scan instanceof IScanMSD) {
					traces = ScanSupport.extractTracesText((IScanMSD)scan, maxCopyTraces);
				} else if(scan instanceof IScanWSD) {
					traces = WavelengthSupport.extractTracesText((IScanWSD)scan, maxCopyTraces);
				}
				/*
				 * Copy to clipboard
				 */
				if(traces != null) {
					TextTransfer textTransfer = TextTransfer.getInstance();
					Object[] data = new Object[]{traces};
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
		button.setToolTipText("Reset the scan");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setEditToolbarStatus(false);
				setSubtractModus(e.display, false, false);
				updateInfoLabels();
				updateScan(scan);
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageScans.class, PreferencePageSubtract.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				updateScan(scan);
				scanIdentifierUI.updateIdentifier();
			}
		});
	}

	private Button createSaveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Save the scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					if(scan instanceof IScanMSD) {
						IScanMSD scanMSD = (IScanMSD)scan;
						DatabaseFileSupport.saveMassSpectrum(e.display.getActiveShell(), scanMSD, "OriginalScan");
						IScanMSD optimizedScan = scanMSD.getOptimizedMassSpectrum();
						if(optimizedScan != null) {
							DatabaseFileSupport.saveMassSpectrum(e.display.getActiveShell(), optimizedScan, "OptimizedScan");
						}
					}
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
		return button;
	}

	private Button createDeleteOptimizedButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete the optimized scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteOptimizedScan(e.widget.getDisplay());
			}
		});
		return button;
	}

	private void deleteOptimizedScan(Display display) {

		if(scan instanceof IScanMSD) {
			if(MessageDialog.openQuestion(display.getActiveShell(), "Optimized Scan", "Would you like to delete the optimized scan?")) {
				IScanMSD scanMSD = (IScanMSD)scan;
				scanMSD.setOptimizedMassSpectrum(null);
				updateScan(scan);
			}
		}
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createScanChart(Composite parent) {

		ScanChartUI scanChartUI = new ScanChartUI(parent, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		chartControl.set(scanChartUI);
	}

	private void setDetectorSignalType(IScan scan) {

		if(scan instanceof IScanMSD) {
			setSelectionIndex(comboDataType, ScanDataSupport.DATA_TYPES_MSD);
			setSelectionIndex(comboSignalType, ScanDataSupport.SIGNAL_TYPES_MSD);
		} else if(scan instanceof IScanCSD) {
			setSelectionIndex(comboDataType, ScanDataSupport.DATA_TYPES_CSD);
			setSelectionIndex(comboSignalType, ScanDataSupport.SIGNAL_TYPES_CSD);
		} else if(scan instanceof IScanWSD) {
			setSelectionIndex(comboDataType, ScanDataSupport.DATA_TYPES_WSD);
			setSelectionIndex(comboSignalType, ScanDataSupport.SIGNAL_TYPES_WSD);
		} else {
			comboDataType.setItems(ScanDataSupport.DATA_TYPES_DEFAULT);
			comboDataType.select(0);
			comboSignalType.setItems(ScanDataSupport.SIGNAL_TYPES_DEFAULT);
			comboSignalType.select(0);
		}
		/*
		 * Data / Signal Type
		 */
		ScanChartUI scanChartUI = chartControl.get();
		scanChartUI.setDataType(DataType.valueOf(comboDataType.getText()));
		scanChartUI.setSignalType(SignalType.valueOf(comboSignalType.getText()));
	}

	private void setSelectionIndex(Combo combo, String[] items) {

		int index;
		if(combo.getSelectionIndex() == -1) {
			index = 0;
		} else {
			index = (combo.getSelectionIndex() < items.length) ? combo.getSelectionIndex() : 0;
		}
		//
		combo.setItems(items);
		combo.select(index);
	}

	private void fireUpdateChromatogramSelection(Display display, IScan scan) {

		display.asyncExec(new Runnable() {

			@SuppressWarnings({"unchecked", "rawtypes"})
			@Override
			public void run() {

				if(eventBroker != null) {
					DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
					List<Object> objects = dataUpdateSupport.getUpdates(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION);
					if(objects != null && objects.size() > 0) {
						Object object = objects.get(0);
						if(object instanceof IChromatogramSelection) {
							IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
							if(chromatogramSelection != null) {
								if(scan != null) {
									/*
									 * We assume that the subtraction takes place in the same
									 * chromatogram. It could happen, that one scan is selected
									 * and set to edit modus and afterwards another chromatogram
									 * is selected. This could lead to misleading behavior.
									 * But it's unclear how to solve it hear. This is currently
									 * the best way to prevent unwanted behavior. The scan chart shows
									 * data from scans and peaks.
									 */
									IChromatogram<IPeak> chromatogram = chromatogramSelection.getChromatogram();
									int retentionTime = scan.getRetentionTime();
									int scanNumber = chromatogram.getScanNumber(retentionTime);
									IScan scanReference = chromatogram.getScan(scanNumber);
									if(scan == scanReference) {
										chromatogramSelection.setSelectedScan(scan);
									}
								}
								/*
								 * Update the chromatogram
								 */
								chromatogramSelection.update(false);
							}
						}
					}
				}
			}
		});
	}
}
