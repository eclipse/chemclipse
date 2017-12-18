/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.MassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.exceptions.NoMassSpectrumFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.identifier.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.support.FilterSupport;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectrumFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ScanChartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.SignalType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.parts.ScanChartPart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSubtract;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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

public class ExtendedScanChartUI {

	private static final Logger logger = Logger.getLogger(ScanChartPart.class);
	//
	private Composite toolbarInfo;
	private Composite toolbarIdentify;
	private Label labelScan;
	private Button buttonSaveScan;
	private Button buttonOptimizedScan;
	private ScanChartUI scanChartUI;
	//
	private Combo comboDataType;
	private Combo comboSignalType;
	//
	private Button buttonPinScan;
	private Button buttonSubstractScan;
	private Combo comboScanFilter;
	private Button buttonScanFilter;
	private Combo comboScanIdentifier;
	private Button buttonScanIdentifier;
	//
	private IScan scan;
	private IScanMSD originalScan;
	private IScanMSD optimizedScan;
	//
	private boolean isScanPinned = false;
	private List<String> scanFilterIds;
	private String[] scanFilterNames;
	private List<String> scanIdentifierIds;
	private String[] scanIdentifierNames;

	private class MassSpectrumIdentifierRunnable implements IRunnableWithProgress {

		private String identifierId;

		public MassSpectrumIdentifierRunnable(String identifierId) {
			this.identifierId = identifierId;
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

			try {
				monitor.beginTask("Scan Identification", IProgressMonitor.UNKNOWN);
				MassSpectrumIdentifier.identify(optimizedScan, identifierId, monitor);
				originalScan.setOptimizedMassSpectrum(optimizedScan);
				//
				IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
				eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, originalScan.getOptimizedMassSpectrum());
			} finally {
				monitor.done();
			}
		}
	}

	@Inject
	public ExtendedScanChartUI(Composite parent, MPart part) {
		initializeSettings();
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateScan();
	}

	public void update(IScan scan) {

		if(!isScanPinned) {
			this.scan = scan;
			updateScan();
		}
	}

	private void updateScan() {

		if(!isScanPinned) {
			labelScan.setText(ScanSupport.getScanLabel(scan));
			enableIdentifierSettings(scan);
			setDetectorSignalType(scan);
			scanChartUI.setInput(scan);
		}
	}

	private void initializeSettings() {

		initializeScanFilter();
		initializeScanIdentifier();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarIdentify = createToolbarIdentify(parent);
		createScanChart(parent);
		//
		enableToolbarButtons(false);
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarIdentify, false);
	}

	private void initializeScanFilter() {

		try {
			IMassSpectrumFilterSupport massSpectrumFilterSupport = MassSpectrumFilter.getMassSpectrumFilterSupport();
			scanFilterIds = massSpectrumFilterSupport.getAvailableFilterIds();
			scanFilterNames = massSpectrumFilterSupport.getFilterNames();
		} catch(NoMassSpectrumFilterSupplierAvailableException e) {
			scanFilterIds = new ArrayList<String>();
			scanFilterNames = new String[0];
		}
	}

	private void initializeScanIdentifier() {

		try {
			IMassSpectrumIdentifierSupport massSpectrumIdentifierSupport = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();
			scanIdentifierIds = massSpectrumIdentifierSupport.getAvailableIdentifierIds();
			scanIdentifierNames = massSpectrumIdentifierSupport.getIdentifierNames();
		} catch(NoIdentifierAvailableException e) {
			scanIdentifierIds = new ArrayList<String>();
			scanIdentifierNames = new String[0];
		}
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(9, false));
		//
		createButtonToggleToolbarInfo(composite);
		comboDataType = createDataType(composite);
		comboSignalType = createSignalType(composite);
		createButtonToggleToolbarIdentify(composite);
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		buttonSaveScan = createSaveButton(composite);
		buttonOptimizedScan = createOptimizedScanButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarIdentify(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		composite.setVisible(false);
		//
		buttonPinScan = createPinButton(composite);
		buttonSubstractScan = createSubstractScanButton(composite);
		comboScanFilter = createScanFilterCombo(composite);
		buttonScanFilter = createScanFilterButton(composite);
		comboScanIdentifier = createScanIdentifierCombo(composite);
		buttonScanIdentifier = createScanIdentifierButton(composite);
		//
		setPinButtonText();
		//
		return composite;
	}

	private Button createPinButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Pin the scan.");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				isScanPinned = isScanPinned ? false : true;
				setPinButtonText();
			}
		});
		return button;
	}

	private Button createSubstractScanButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Mark the current scan as background for subtraction.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_ADD_SELECTED_SCAN, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(optimizedScan != null) {
					/*
					 * Display the subtract mass spectrum.
					 */
					PreferenceSupplier.setSessionSubtractMassSpectrum(null);
					IScanMSD massSpectrum1 = null;
					boolean useNormalize = PreferenceSupplier.isUseNormalizedScan();
					IScanMSD subtractMassSpectrum = FilterSupport.getCombinedMassSpectrum(massSpectrum1, optimizedScan, null, useNormalize);
					PreferenceSupplier.setSessionSubtractMassSpectrum(subtractMassSpectrum);
					/*
					 * Update all listeners
					 */
					IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
					boolean autofocuSubtractScanPart = preferenceStore.getBoolean(PreferenceConstants.P_AUTOFOCUS_SUBTRACT_SCAN_PART);
					if(autofocuSubtractScanPart) {
						String partStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_CHART);
						PartSupport.showPart(PartSupport.PARTDESCRIPTOR_SUBTRACT_SCAN, partStackId);
					}
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
				}
			}
		});
		return button;
	}

	private Combo createScanFilterCombo(Composite parent) {

		Combo combo = new Combo(parent, SWT.NONE);
		combo.setToolTipText("Scan Filter");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.setItems(scanFilterNames);
		if(scanFilterNames.length > 0) {
			combo.select(0);
		}
		return combo;
	}

	private Button createScanFilterButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Filter");
		button.setToolTipText("Filter the current scan.");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboScanFilter.getSelectionIndex();
				if(index >= 0 && index < scanFilterIds.size()) {
					String filterId = scanFilterIds.get(index);
					if(optimizedScan != null) {
						/*
						 * Clear all identification results and
						 * then run apply the filter.
						 */
						isScanPinned = true;
						setPinButtonText();
						//
						optimizedScan.getTargets().clear();
						MassSpectrumFilter.applyFilter(optimizedScan, filterId, new NullProgressMonitor());
						scanChartUI.setInput(optimizedScan);
					}
				}
			}
		});
		return button;
	}

	private Combo createScanIdentifierCombo(Composite parent) {

		Combo combo = new Combo(parent, SWT.NONE);
		combo.setToolTipText("Scan Identifier");
		combo.setItems(scanIdentifierNames);
		if(scanIdentifierNames.length > 0) {
			combo.select(0);
		}
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return combo;
	}

	private Button createScanIdentifierButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Identify");
		button.setToolTipText("Identify the scan.");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(originalScan != null && optimizedScan != null) {
					int index = comboScanIdentifier.getSelectionIndex();
					if(index >= 0 && index < scanIdentifierIds.size()) {
						/*
						 * Run the identification.
						 */
						String identifierId = scanIdentifierIds.get(index);
						IRunnableWithProgress runnable = new MassSpectrumIdentifierRunnable(identifierId);
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
						try {
							monitor.run(true, true, runnable);
						} catch(InvocationTargetException e1) {
							logger.warn(e1);
						} catch(InterruptedException e1) {
							logger.warn(e1);
						}
					}
				}
			}
		});
		return button;
	}

	private void setPinButtonText() {

		buttonPinScan.setText("");
		if(isScanPinned) {
			buttonPinScan.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_UNPIN_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		} else {
			buttonPinScan.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PIN_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		}
	}

	private Combo createDataType(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Data Type (MS, MS/MS, FID, DAD, ...)");
		combo.setItems(ScanSupport.DATA_TYPES_DEFAULT);
		combo.select(0);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String selection = combo.getText();
				scanChartUI.setDataType(DataType.valueOf(selection));
				scanChartUI.setInput(scan);
			}
		});
		return combo;
	}

	private Combo createSignalType(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Signal Type (Centroid: Bar Series, Profile: Line Series)");
		combo.setItems(ScanSupport.SIGNAL_TYPES_DEFAULT);
		combo.select(0);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String selection = combo.getText();
				scanChartUI.setSignalType(SignalType.valueOf(selection));
				scanChartUI.setInput(scan);
			}
		});
		return combo;
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

	private Button createButtonToggleToolbarIdentify(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle identify toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IDENTIFY_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarIdentify);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IDENTIFY_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IDENTIFY_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void enableToolbarButtons(boolean enabled) {

		buttonSaveScan.setEnabled(enabled);
		buttonOptimizedScan.setEnabled(enabled);
		//
		toolbarIdentify.setEnabled(enabled);
		buttonPinScan.setEnabled(enabled);
		buttonSubstractScan.setEnabled(enabled);
		comboScanFilter.setEnabled(enabled);
		buttonScanFilter.setEnabled(enabled);
		comboScanIdentifier.setEnabled(enabled);
		buttonScanIdentifier.setEnabled(enabled);
	}

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				scanChartUI.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the scan chart.");
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

				IPreferencePage preferencePageScans = new PreferencePageScans();
				preferencePageScans.setTitle("Scan Settings");
				IPreferencePage preferencePageSubtract = new PreferencePageSubtract();
				preferencePageSubtract.setTitle("Subtract Settings");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageScans));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageSubtract));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
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
					MassSpectrumFileSupport.saveMassSpectrum(optimizedScan);
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
		return button;
	}

	private Button createOptimizedScanButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Show optimized scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PLUS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(scan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)scan;
					IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
					if(optimizedMassSpectrum != null) {
						IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
						eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, optimizedMassSpectrum);
					}
				}
			}
		});
		return button;
	}

	private void reset() {

		updateScan();
	}

	private void applySettings() {

		updateScan();
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		composite.setVisible(false);
		//
		labelScan = new Label(composite, SWT.NONE);
		labelScan.setText("");
		labelScan.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private void createScanChart(Composite parent) {

		scanChartUI = new ScanChartUI(parent, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void enableIdentifierSettings(IScan scan) {

		if(scan instanceof IScanMSD) {
			enableToolbarButtons(true);
			IScanMSD scanMSD = (IScanMSD)scan;
			IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
			if(optimizedMassSpectrum != null) {
				buttonOptimizedScan.setEnabled(true);
			} else {
				buttonOptimizedScan.setEnabled(false);
			}
			/*
			 * Clone
			 */
			try {
				originalScan = scanMSD;
				optimizedScan = originalScan.makeDeepCopy();
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		} else {
			enableToolbarButtons(false);
			originalScan = null;
			optimizedScan = null;
		}
	}

	private void setDetectorSignalType(IScan scan) {

		if(scan instanceof IScanMSD) {
			setSelectionIndex(comboDataType, ScanSupport.DATA_TYPES_MSD);
			setSelectionIndex(comboSignalType, ScanSupport.SIGNAL_TYPES_MSD);
		} else if(scan instanceof IScanCSD) {
			setSelectionIndex(comboDataType, ScanSupport.DATA_TYPES_CSD);
			setSelectionIndex(comboSignalType, ScanSupport.SIGNAL_TYPES_CSD);
		} else if(scan instanceof IScanWSD) {
			setSelectionIndex(comboDataType, ScanSupport.DATA_TYPES_WSD);
			setSelectionIndex(comboSignalType, ScanSupport.SIGNAL_TYPES_WSD);
		} else {
			comboDataType.setItems(ScanSupport.DATA_TYPES_DEFAULT);
			comboDataType.select(0);
			comboSignalType.setItems(ScanSupport.SIGNAL_TYPES_DEFAULT);
			comboSignalType.select(0);
		}
		/*
		 * Data / Signal Type
		 */
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
}
