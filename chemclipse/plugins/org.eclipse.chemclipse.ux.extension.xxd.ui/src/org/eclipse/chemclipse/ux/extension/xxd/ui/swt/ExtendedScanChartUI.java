/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.MassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.calculator.SubtractCalculator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.MassSpectrumIdentifierRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChartConfigSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.SignalType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.parts.ScanChartPart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSubtract;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.AxisConfig.ChartAxis;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ExtendedScanChartUI implements ConfigurableUI<ScanChartUIConfig> {

	private static final Logger logger = Logger.getLogger(ScanChartPart.class);
	//
	private final IEventBroker eventBroker; // Could be null
	//
	private Composite toolbarMain;
	private Composite toolbarInfo;
	private Composite toolbarIdentify;
	private Composite toolbarTypes;
	//
	private CLabel labelEdit;
	private CLabel labelSubtract;
	private CLabel labelOptimized;
	//
	private Label labelScan;
	private Button buttonSaveScan;
	private Button buttonDeleteOptimized;
	private ScanChartUI scanChartUI;
	//
	private Combo comboDataType;
	private Combo comboSignalType;
	//
	private Button buttonSubstractOption;
	private ComboViewer comboViewerFilter;
	private Button buttonRunFilter;
	private ComboViewer comboViewerIdentifier;
	private Button buttonRunIdentifier;
	//
	private IScan scan;
	//
	private final ScanDataSupport scanDataSupport = new ScanDataSupport();
	//
	private boolean editModus = false;
	private boolean subtractModus = false;
	private Color backgroundDefault;

	public ExtendedScanChartUI(Composite parent, IEventBroker eventBroker) {
		this.eventBroker = eventBroker;
		initialize(parent);
	}

	/**
	 * Getting the updates from the system.
	 * 
	 * @param scan
	 */
	public void update(IScan scan) {

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
						subtractScanMSD(scanSource, scanSubtract);
						subtractModus = false;
						updateScan(scan);
					}
				}
			}
		} else {
			updateScan(scan);
		}
	}

	private void updateScan(IScan scan) {

		this.scan = scan;
		labelScan.setText(scanDataSupport.getScanLabel(scan));
		setDetectorSignalType(scan);
		updateScanChart(scan);
		updateInfoLabels();
		updateButtons();
	}

	private void updateScanChart(IScan scan) {

		if(scan instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)scan;
			IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
			if(optimizedMassSpectrum != null) {
				scanChartUI.setInput(optimizedMassSpectrum);
			} else {
				scanChartUI.setInput(scanMSD);
			}
		} else {
			scanChartUI.setInput(scan);
		}
	}

	private void initialize(Composite parent) {

		Composite container = createContainer(parent);
		backgroundDefault = container.getBackground();
		//
		toolbarMain = createToolbarMain(container);
		toolbarInfo = createToolbarInfo(container);
		toolbarTypes = createToolbarTypes(container);
		toolbarIdentify = createToolbarIdentify(container);
		scanChartUI = createScanChart(container);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarTypes, false);
		PartSupport.setCompositeVisibility(toolbarIdentify, false);
		/*
		 * Filter Supplier
		 */
		IMassSpectrumFilterSupport massSpectrumFilterSupport = MassSpectrumFilter.getMassSpectrumFilterSupport();
		comboViewerFilter.setInput(massSpectrumFilterSupport.getSuppliers());
		/*
		 * Identifier Supplier
		 */
		IMassSpectrumIdentifierSupport massSpectrumIdentifierSupport = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();
		comboViewerIdentifier.setInput(massSpectrumIdentifierSupport.getSuppliers());
		//
		updateButtons();
	}

	private Composite createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(10, false));
		//
		labelEdit = createInfoLabel(composite);
		labelSubtract = createInfoLabel(composite);
		labelOptimized = createInfoLabel(composite);
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarTypes(composite);
		createButtonToggleToolbarIdentify(composite);
		createResetButton(composite);
		buttonSaveScan = createSaveButton(composite);
		buttonDeleteOptimized = createDeleteOptimizedButton(composite);
		createSettingsButton(composite);
		//
		return composite;
	}

	private CLabel createInfoLabel(Composite parent) {

		CLabel label = new CLabel(parent, SWT.CENTER);
		label.setForeground(Colors.RED);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return label;
	}

	private Composite createToolbarTypes(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(2, true));
		//
		comboDataType = createDataType(composite);
		comboDataType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboSignalType = createSignalType(composite);
		comboSignalType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarIdentify(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(5, false));
		//
		buttonSubstractOption = createButtonSubtractOption(composite);
		comboViewerFilter = createFilterComboViewer(composite);
		buttonRunFilter = createScanFilterButton(composite);
		comboViewerIdentifier = createIdentifierComboViewer(composite);
		buttonRunIdentifier = createRunIdentifierButton(composite);
		//
		return composite;
	}

	private Button createButtonSubtractOption(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Enable/Disable the interactive subtract option.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_ADD_SELECTED_SCAN, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				subtractModus = !subtractModus;
				updateInfoLabels();
			}
		});
		return button;
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
		try {
			/*
			 * Work on the optimized scan.
			 */
			IScanMSD optimizedMassSpectrum = scanSource.getOptimizedMassSpectrum();
			if(optimizedMassSpectrum == null) {
				optimizedMassSpectrum = scanSource.makeDeepCopy();
				scanSource.setOptimizedMassSpectrum(optimizedMassSpectrum);
			}
			//
			List<IScanMSD> massSpectra = new ArrayList<>();
			massSpectra.add(optimizedMassSpectrum);
			SubtractCalculator subtractCalculator = new SubtractCalculator();
			subtractCalculator.subtractMassSpectra(massSpectra, settings);
		} catch(CloneNotSupportedException e) {
			logger.warn(e);
		}
	}

	private ComboViewer createFilterComboViewer(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IMassSpectrumFilterSupplier) {
					IMassSpectrumFilterSupplier supplier = (IMassSpectrumFilterSupplier)element;
					return supplier.getFilterName();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a scan filter.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return comboViewer;
	}

	private Button createScanFilterButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Filter the currently selected scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Filter
				 */
				Object object = comboViewerFilter.getStructuredSelection().getFirstElement();
				if(object instanceof IMassSpectrumFilterSupplier) {
					IMassSpectrumFilterSupplier supplier = (IMassSpectrumFilterSupplier)object;
					if(scan instanceof IScanMSD) {
						/*
						 * Get or create an optimized scan.
						 */
						IScanMSD scanMSD = (IScanMSD)scan;
						IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
						if(optimizedMassSpectrum == null) {
							try {
								optimizedMassSpectrum = scanMSD.makeDeepCopy();
								scanMSD.setOptimizedMassSpectrum(optimizedMassSpectrum);
							} catch(CloneNotSupportedException e1) {
								logger.warn(e1);
							}
						}
						//
						if(optimizedMassSpectrum != null) {
							/*
							 * Clear all identification results and
							 * then run apply the filter.
							 */
							optimizedMassSpectrum.getTargets().clear();
							MassSpectrumFilter.applyFilter(optimizedMassSpectrum, supplier.getId(), new NullProgressMonitor());
							updateScan(scan);
						}
					}
				}
			}
		});
		return button;
	}

	private ComboViewer createIdentifierComboViewer(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IMassSpectrumIdentifierSupplier) {
					IMassSpectrumIdentifierSupplier supplier = (IMassSpectrumIdentifierSupplier)element;
					return supplier.getIdentifierName();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a scan identifier.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return comboViewer;
	}

	private Button createRunIdentifierButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Identify the currently selected scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(scan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)scan;
					IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
					IScanMSD massSpectrum;
					//
					if(optimizedMassSpectrum != null) {
						massSpectrum = optimizedMassSpectrum;
					} else {
						massSpectrum = scanMSD;
					}
					/*
					 * Identification
					 */
					Object object = comboViewerIdentifier.getStructuredSelection().getFirstElement();
					if(object instanceof IMassSpectrumIdentifierSupplier) {
						IMassSpectrumIdentifierSupplier supplier = (IMassSpectrumIdentifierSupplier)object;
						IRunnableWithProgress runnable = new MassSpectrumIdentifierRunnable(massSpectrum, supplier.getId());
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(e.display.getActiveShell());
						try {
							monitor.run(true, true, runnable);
							/*
							 * Update the scan
							 */
							e.display.asyncExec(new Runnable() {

								@Override
								public void run() {

									if(eventBroker != null) {
										eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, massSpectrum);
									}
								}
							});
							/*
							 * Update the active chromatogram
							 */
							e.display.asyncExec(new Runnable() {

								@Override
								public void run() {

									if(eventBroker != null) {
										EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
										@SuppressWarnings("rawtypes")
										IChromatogramSelection chromatogramSelection = editorUpdateSupport.getActiveEditorSelection();
										if(chromatogramSelection != null) {
											chromatogramSelection.update(false);
										}
									}
								}
							});
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

	private Combo createDataType(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Data Type (MS, MS/MS, FID, DAD, ...)");
		combo.setItems(ScanDataSupport.DATA_TYPES_DEFAULT);
		combo.select(0);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String selection = combo.getText();
				scanChartUI.setDataType(DataType.valueOf(selection));
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
				scanChartUI.setSignalType(SignalType.valueOf(selection));
				updateScanChart(scan);
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

	private Button createButtonToggleToolbarTypes(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle type toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TYPES, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarTypes);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TYPES, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TYPES, IApplicationImage.SIZE_16x16));
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
					editModus = true;
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IDENTIFY_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
				} else {
					editModus = false;
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IDENTIFY_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
				}
				updateInfoLabels();
			}
		});
		//
		return button;
	}

	private void updateInfoLabels() {

		updateLabel(labelEdit, (editModus) ? "Edit On" : "");
		updateLabel(labelSubtract, (subtractModus) ? "Subtract On" : "");
		updateLabel(labelOptimized, (isOptimizedScan()) ? "Optimized" : "");
	}

	private boolean isMassSpectrum() {

		return scan instanceof IScanMSD;
	}

	private boolean isOptimizedScan() {

		if(scan instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)scan;
			IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
			if(optimizedMassSpectrum != null) {
				return true;
			}
		}
		return false;
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

		boolean enabled = isMassSpectrum();
		buttonSaveScan.setEnabled(isMassSpectrum());
		buttonDeleteOptimized.setEnabled(isOptimizedScan() ? true : false);
		//
		buttonSubstractOption.setEnabled(enabled);
		comboViewerFilter.getCombo().setEnabled(enabled);
		buttonRunFilter.setEnabled(enabled);
		comboViewerIdentifier.getCombo().setEnabled(enabled);
		buttonRunIdentifier.setEnabled(enabled);
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the scan");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateScan(scan);
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

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePageScans()));
				preferenceManager.addToRoot(new PreferenceNode("2", new PreferencePageSubtract()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						updateScan(scan);
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
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

				if(scan instanceof IScanMSD) {
					if(MessageDialog.openQuestion(e.widget.getDisplay().getActiveShell(), "Optimized Scan", "Would you like to delete the optimized scan?")) {
						IScanMSD scanMSD = (IScanMSD)scan;
						scanMSD.setOptimizedMassSpectrum(null);
						updateScan(scan);
					}
				}
			}
		});
		return button;
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelScan = new Label(composite, SWT.NONE);
		labelScan.setText("");
		labelScan.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private ScanChartUI createScanChart(Composite parent) {

		ScanChartUI scanChartUI = new ScanChartUI(parent, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		return scanChartUI;
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

	@Override
	public ScanChartUIConfig getConfig() {

		return new ScanChartUIConfig() {

			ChartConfigSupport axisSupport = new ChartConfigSupport(scanChartUI, EnumSet.of(ChartAxis.PRIMARY_X, ChartAxis.PRIMARY_Y, ChartAxis.SECONDARY_Y));

			@Override
			public void setToolbarVisible(boolean visible) {

				PartSupport.setCompositeVisibility(toolbarMain, visible);
			}

			@Override
			public boolean isToolbarVisible() {

				return toolbarMain.isVisible();
			}

			@Override
			public boolean hasToolbarInfo() {

				return true;
			}

			@Override
			public void setToolbarInfoVisible(boolean visible) {

				PartSupport.setCompositeVisibility(toolbarInfo, visible);
			}

			@Override
			public void setAxisLabelVisible(ChartAxis axis, boolean visible) {

				axisSupport.setAxisLabelVisible(axis, visible);
			}

			@Override
			public void setAxisVisible(ChartAxis axis, boolean visible) {

				axisSupport.setAxisVisible(axis, visible);
			}

			@Override
			public boolean hasAxis(ChartAxis axis) {

				return axisSupport.hasAxis(axis);
			};
		};
	}
}
