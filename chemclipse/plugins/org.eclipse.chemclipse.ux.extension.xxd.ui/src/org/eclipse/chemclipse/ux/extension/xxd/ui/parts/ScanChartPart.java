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
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.MassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.exceptions.NoMassSpectrumFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.FilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.support.ISubtractFilterEvents;
import org.eclipse.chemclipse.chromatogram.msd.identifier.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.notifier.MassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectrumFileSupport;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ScanChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.AbstractScanUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.IScanUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.SignalType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
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

public class ScanChartPart extends AbstractScanUpdateSupport implements IScanUpdateSupport {

	private static final Logger logger = Logger.getLogger(ScanChartPart.class);
	//
	private Composite toolbarInfo;
	private Composite toolbarIdentify;
	private Label labelScan;
	private Button buttonSaveScan;
	private ScanChart scanChart;
	//
	private IScanMSD originalMassSpectrum;
	private IScanMSD clonedMassSpectrum;
	private Button buttonPinScan;
	private Combo comboScanFilter;
	private Combo comboScanIdentifier;
	private boolean isScanPinned = false;
	private List<String> massSpectrumFilterIds;
	private String[] massSpectrumFilterNames;
	private List<String> massSpectrumIdentifierIds;
	private String[] massSpectrumIdentifierNames;

	@Inject
	public ScanChartPart(Composite parent, MPart part) {
		super(part);
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateScan(getScan());
	}

	@Override
	public void updateScan(IScan scan) {

		labelScan.setText(ScanSupport.getScanLabel(scan));
		if(scan instanceof IScanMSD) {
			buttonSaveScan.setEnabled(true);
			try {
				IScanMSD scanMSD = (IScanMSD)scan;
				originalMassSpectrum = scanMSD;
				clonedMassSpectrum = scanMSD.makeDeepCopy();
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
			//
		} else {
			buttonSaveScan.setEnabled(false);
			originalMassSpectrum = null;
			clonedMassSpectrum = null;
		}
		scanChart.setInput(scan);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		setFilterAndIdentifier();
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarIdentify = createToolbarIdentify(parent);
		createScanChart(parent);
		//
		buttonSaveScan.setEnabled(false);
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarIdentify, false);
	}

	private void setFilterAndIdentifier() {

		/*
		 * Mass Spectrum Filter
		 */
		try {
			IMassSpectrumFilterSupport massSpectrumFilterSupport = MassSpectrumFilter.getMassSpectrumFilterSupport();
			massSpectrumFilterIds = massSpectrumFilterSupport.getAvailableFilterIds();
			massSpectrumFilterNames = massSpectrumFilterSupport.getFilterNames();
		} catch(NoMassSpectrumFilterSupplierAvailableException e) {
			massSpectrumFilterIds = new ArrayList<String>();
			massSpectrumFilterNames = new String[0];
		}
		/*
		 * Mass Spectrum Identifier
		 */
		try {
			IMassSpectrumIdentifierSupport massSpectrumIdentifierSupport = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();
			massSpectrumIdentifierIds = massSpectrumIdentifierSupport.getAvailableIdentifierIds();
			massSpectrumIdentifierNames = massSpectrumIdentifierSupport.getIdentifierNames();
		} catch(NoIdentifierAvailableException e) {
			massSpectrumIdentifierIds = new ArrayList<String>();
			massSpectrumIdentifierNames = new String[0];
		}
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(8, false));
		//
		createButtonToggleToolbarInfo(composite);
		createDetectorType(composite);
		createSignalType(composite);
		createButtonToggleToolbarIdentify(composite);
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
		createSaveButton(composite);
	}

	private Composite createToolbarIdentify(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		composite.setVisible(false);
		//
		createPinButton(composite);
		createSubstractMassSpectrumButton(composite);
		createFilterCombo(composite);
		createFilterButton(composite);
		createIdentifierCombo(composite);
		createIdentifierButton(composite);
		//
		return composite;
	}

	private void createPinButton(Composite parent) {

		buttonPinScan = new Button(parent, SWT.PUSH);
		setPinButtonText();
		buttonPinScan.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				isScanPinned = isScanPinned ? false : true;
				setPinButtonText();
			}
		});
	}

	private void createSubstractMassSpectrumButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_ADD_SELECTED_SCAN, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(clonedMassSpectrum != null) {
					String viewId = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.part.sessionSubtractMassSpectrum";
					List<String> views = new ArrayList<String>();
					views.add(viewId);
					PerspectiveSwitchHandler.focusViews(views);
					/*
					 * Clears the currently used subtract mass spectrum
					 */
					PreferenceSupplier.setSessionSubtractMassSpectrum(null);
					/*
					 * Add the selected scan as the subtract mass spectrum.
					 */
					IScanMSD massSpectrum1 = PreferenceSupplier.getSessionSubtractMassSpectrum();
					boolean useNormalize = PreferenceSupplier.isUseNormalize();
					IScanMSD normalizedMassSpectrum = FilterSupport.getCombinedMassSpectrum(massSpectrum1, clonedMassSpectrum, null, useNormalize);
					PreferenceSupplier.setSessionSubtractMassSpectrum(normalizedMassSpectrum);
					/*
					 * Update all listeners
					 */
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(ISubtractFilterEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
				}
			}
		});
	}

	private void createFilterCombo(Composite parent) {

		comboScanFilter = new Combo(parent, SWT.NONE);
		comboScanFilter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboScanFilter.setItems(massSpectrumFilterNames);
	}

	private void createFilterButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Filter");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboScanFilter.getSelectionIndex();
				if(index >= 0 && index < massSpectrumFilterIds.size()) {
					String filterId = massSpectrumFilterIds.get(index);
					if(clonedMassSpectrum != null) {
						/*
						 * Clear all identification results and
						 * then run apply the filter.
						 */
						clonedMassSpectrum.getTargets().clear();
						MassSpectrumFilter.applyFilter(clonedMassSpectrum, filterId, new NullProgressMonitor());
						updateAfterProcessing();
					}
				}
			}
		});
	}

	private void createIdentifierCombo(Composite parent) {

		comboScanIdentifier = new Combo(parent, SWT.NONE);
		comboScanIdentifier.setItems(massSpectrumIdentifierNames);
		comboScanIdentifier.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createIdentifierButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Identify");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboScanIdentifier.getSelectionIndex();
				if(index >= 0 && index < massSpectrumIdentifierIds.size()) {
					final String identifierId = massSpectrumIdentifierIds.get(index);
					if(clonedMassSpectrum != null) {
						IRunnableWithProgress runnable = new IRunnableWithProgress() {

							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

								try {
									monitor.beginTask("Mass Spectrum Identification", IProgressMonitor.UNKNOWN);
									MassSpectrumIdentifier.identify(clonedMassSpectrum, identifierId, monitor);
									MassSpectrumSelectionUpdateNotifier.fireUpdateChange(clonedMassSpectrum, true);
									originalMassSpectrum.setOptimizedMassSpectrum(clonedMassSpectrum);
								} finally {
									monitor.done();
								}
							}
						};
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
						try {
							/*
							 * Use true, true ... instead of false, true ... if the progress bar
							 * should be shown in action.
							 */
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
	}

	private void updateAfterProcessing() {

		updateScan(clonedMassSpectrum);
	}

	private void setPinButtonText() {

		buttonPinScan.setText("");
		if(isScanPinned) {
			buttonPinScan.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_UNPIN_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		} else {
			buttonPinScan.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PIN_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		}
	}

	private void createDetectorType(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Detector Type (MS, MS/MS, FID, DAD, ...)");
		combo.setItems(new String[]{DataType.AUTO_DETECT.toString(), DataType.MSD_NOMINAL.toString(), DataType.MSD_TANDEM.toString(), DataType.MSD_HIGHRES.toString(), DataType.CSD.toString(), DataType.WSD.toString()});
		combo.select(0);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String selection = combo.getText();
				if(!selection.equals(DataType.AUTO_DETECT)) {
					scanChart.setDataType(DataType.valueOf(selection));
					scanChart.setInput(getScan());
				}
			}
		});
	}

	private void createSignalType(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Signal Type (Centroid: Bar Series, Profile: Line Series)");
		combo.setItems(new String[]{SignalType.AUTO_DETECT.toString(), SignalType.CENTROID.toString(), SignalType.PROFILE.toString()});
		combo.select(0);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String selection = combo.getText();
				if(!selection.equals(SignalType.AUTO_DETECT.toString())) {
					scanChart.setSignalType(SignalType.valueOf(selection));
					scanChart.setInput(getScan());
				}
			}
		});
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
		button.setToolTipText("Toggle edit toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarIdentify);
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

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				scanChart.toggleSeriesLegendVisibility();
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

				IPreferencePage preferencePage = new PreferencePageScans();
				preferencePage.setTitle("Scan Settings");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
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

	private void createSaveButton(Composite parent) {

		buttonSaveScan = new Button(parent, SWT.PUSH);
		buttonSaveScan.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		buttonSaveScan.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					MassSpectrumFileSupport.saveMassSpectrum(clonedMassSpectrum);
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
	}

	private void reset() {

		updateScan(getScan());
	}

	private void applySettings() {

		updateScan(getScan());
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

		scanChart = new ScanChart(parent, SWT.BORDER);
		scanChart.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
