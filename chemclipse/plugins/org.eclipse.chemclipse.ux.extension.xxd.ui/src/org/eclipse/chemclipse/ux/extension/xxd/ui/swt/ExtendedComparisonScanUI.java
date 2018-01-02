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
import java.text.DecimalFormat;

import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectrumFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ScanChartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.LibraryServiceRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSubtract;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ExtendedComparisonScanUI {

	private static final Logger logger = Logger.getLogger(ExtendedComparisonScanUI.class);
	//
	private static final float NORMALIZATION_FACTOR = 1000.0f;
	private static final String OPTION_UPDATE_SCAN_1 = "UPDATE_SCAN_1";
	private static final String OPTION_UPDATE_SCAN_2 = "UPDATE_SCAN_2";
	private static final String OPTION_LIBRARY_SEARCH = "LIBRARY_SEARCH";
	//
	private Label labelInfoReference;
	private Composite toolbarInfoUnknown;
	private ScanChartUI scanChartUI;
	private Label labelInfoComparison;
	private Composite toolbarInfoReference;
	private Composite toolbarOptions;
	//
	private IScanMSD scan1 = null;
	private IScanMSD scan2 = null;
	//
	private String displayOption = OPTION_LIBRARY_SEARCH;
	private boolean displayDifference = true;
	private boolean displayMirrored = true;
	private boolean displayShifted = false;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	@Inject
	public ExtendedComparisonScanUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateChart();
	}

	public void update(IScanMSD scanMSD) {

		if(displayOption.equals(OPTION_UPDATE_SCAN_1)) {
			scan1 = scanMSD;
		} else if(displayOption.equals(OPTION_UPDATE_SCAN_2)) {
			scan2 = scanMSD;
		} else {
			scan1 = null;
			scan2 = null;
		}
		//
		updateChart();
	}

	public void update(IScanMSD unknownMassSpectrum, IIdentificationTarget identificationTarget) {

		if(displayOption.equals(OPTION_LIBRARY_SEARCH)) {
			//
			scan1 = null;
			scan2 = null;
			//
			LibraryServiceRunnable runnable = new LibraryServiceRunnable(identificationTarget);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			try {
				monitor.run(true, true, runnable);
				scan1 = unknownMassSpectrum.makeDeepCopy().normalize(NORMALIZATION_FACTOR);
				IScanMSD referenceMassSpectrum = runnable.getLibraryMassSpectrum();
				if(referenceMassSpectrum != null) {
					scan2 = referenceMassSpectrum.makeDeepCopy().normalize(NORMALIZATION_FACTOR);
				}
			} catch(InvocationTargetException e) {
				logger.warn(e);
			} catch(InterruptedException e) {
				logger.warn(e);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
			//
			updateChart();
		}
	}

	private void updateChart() {

		if(scan1 != null && scan2 != null) {
			if(displayDifference) {
				updateScanComparisonDifference(displayMirrored, displayShifted);
			} else {
				updateScanComparisonNormal(displayMirrored, displayShifted);
			}
		} else {
			scanChartUI.setInput(scan1);
		}
	}

	private void updateScanComparisonNormal(boolean mirrored, boolean shifted) {

		labelInfoReference.setText(getMassSpectrumLabel(scan1, "[U] UNKNOWN MS = "));
		labelInfoComparison.setText(getMassSpectrumLabel(scan2, "[L] REFERENCE MS = "));
		if(shifted) {
			IScanMSD scan2Shifted = new ScanMSD();
			IExtractedIonSignal extractedIonSignalScan2 = scan2.getExtractedIonSignal();
			int startIon = extractedIonSignalScan2.getStartIon();
			int stopIon = extractedIonSignalScan2.getStopIon();
			for(int ion = startIon; ion <= stopIon; ion++) {
				float abundance = extractedIonSignalScan2.getAbundance(ion);
				if(abundance > 0) {
					scan2Shifted.addIon(getIon(ion + 1, abundance));
				}
			}
		} else {
			scanChartUI.setInput(scan1, scan2, mirrored);
		}
	}

	private void updateScanComparisonDifference(boolean mirrored, boolean shifted) {

		labelInfoReference.setText(getMassSpectrumLabel(scan1, "[U-L] UNKNOWN MS = "));
		labelInfoComparison.setText(getMassSpectrumLabel(scan2, "[U-L] REFERENCE MS = "));
		//
		IExtractedIonSignal extractedIonSignalReference = scan1.getExtractedIonSignal();
		IExtractedIonSignal extractedIonSignalComparison = scan2.getExtractedIonSignal();
		int startIon = (extractedIonSignalReference.getStartIon() < extractedIonSignalComparison.getStartIon()) ? extractedIonSignalReference.getStartIon() : extractedIonSignalComparison.getStartIon();
		int stopIon = (extractedIonSignalReference.getStopIon() > extractedIonSignalComparison.getStopIon()) ? extractedIonSignalReference.getStopIon() : extractedIonSignalComparison.getStopIon();
		//
		IScanMSD scanDifference1 = new ScanMSD();
		IScanMSD scanDifference2 = new ScanMSD();
		//
		for(int ion = startIon; ion <= stopIon; ion++) {
			float abundance = extractedIonSignalReference.getAbundance(ion) - extractedIonSignalComparison.getAbundance(ion);
			if(abundance > 0) {
				scanDifference1.addIon(getIon(ion, abundance));
			} else if(abundance < 0) {
				abundance *= -1;
				if(shifted) {
					scanDifference2.addIon(getIon(ion + 1, abundance));
				} else {
					scanDifference2.addIon(getIon(ion, abundance));
				}
			}
		}
		//
		scanChartUI.setInput(scanDifference1, scanDifference2, mirrored);
	}

	private IIon getIon(int mz, float abundance) {

		IIon ion = null;
		try {
			ion = new Ion(mz, abundance);
		} catch(AbundanceLimitExceededException e) {
			logger.warn(e);
		} catch(IonLimitExceededException e) {
			logger.warn(e);
		}
		return ion;
	}

	private String getMassSpectrumLabel(IScanMSD massSpectrum, String title) {

		StringBuilder builder = new StringBuilder();
		builder.append(title);
		if(massSpectrum != null) {
			if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
				IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
				ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
				builder.append("NAME: ");
				builder.append(libraryInformation.getName());
				builder.append(" | ");
				builder.append("CAS: ");
				builder.append(libraryInformation.getCasNumber());
				builder.append(" | ");
			}
			builder.append("RT: ");
			builder.append(decimalFormat.format(massSpectrum.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
			builder.append(" | ");
			builder.append("RI: ");
			if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
				builder.append(Integer.toString((int)massSpectrum.getRetentionIndex()));
			} else {
				builder.append(decimalFormat.format(massSpectrum.getRetentionIndex()));
			}
		}
		return builder.toString();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfoUnknown = createToolbarInfoUnknown(parent);
		toolbarOptions = createToolbarOptions(parent);
		createScanChart(parent);
		toolbarInfoReference = createToolbarInfoReference(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfoUnknown, true);
		PartSupport.setCompositeVisibility(toolbarInfoReference, true);
		PartSupport.setCompositeVisibility(toolbarOptions, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(4, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarOptions(composite);
		createSaveButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarInfoUnknown(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfoReference = new Label(composite, SWT.NONE);
		labelInfoReference.setText("");
		labelInfoReference.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarOptions(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createUpdateGroup(composite);
		createDisplayGroup(composite);
		createMirrorOptionSection(composite);
		//
		return composite;
	}

	private void createUpdateGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setBackground(Colors.WHITE);
		group.setText("");
		group.setToolTipText("Select the display option.");
		group.setLayout(new RowLayout(SWT.HORIZONTAL));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Button updateScan1 = new Button(group, SWT.RADIO);
		updateScan1.setText("Scan 1");
		updateScan1.setSelection(displayOption.equals(OPTION_UPDATE_SCAN_1) ? true : false);
		updateScan1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				displayOption = OPTION_UPDATE_SCAN_1;
				updateChart();
			}
		});
		//
		Button updateScan2 = new Button(group, SWT.RADIO);
		updateScan2.setText("Scan 2");
		updateScan2.setSelection(displayOption.equals(OPTION_UPDATE_SCAN_2) ? true : false);
		updateScan2.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				displayDifference = false;
				updateChart();
			}
		});
		//
		Button updateLibraryScan = new Button(group, SWT.RADIO);
		updateLibraryScan.setText("Library Search");
		updateLibraryScan.setSelection(displayOption.equals(OPTION_LIBRARY_SEARCH) ? true : false);
		updateLibraryScan.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				displayDifference = false;
				updateChart();
			}
		});
	}

	private void createDisplayGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText("");
		group.setToolTipText("Select the display pre-processing.");
		group.setBackground(Colors.WHITE);
		group.setText("");
		group.setLayout(new RowLayout(SWT.HORIZONTAL));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Button buttonNormalModus = new Button(group, SWT.RADIO);
		buttonNormalModus.setText("[U,L]");
		buttonNormalModus.setToolTipText("Use the normal modus.");
		buttonNormalModus.setSelection(!displayDifference);
		buttonNormalModus.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				displayDifference = false;
				updateChart();
			}
		});
		//
		Button buttonDifferenceModus = new Button(group, SWT.RADIO);
		buttonDifferenceModus.setText("[U-L]");
		buttonDifferenceModus.setToolTipText("Use the difference modus.");
		buttonDifferenceModus.setSelection(displayDifference);
		buttonDifferenceModus.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				displayDifference = true;
				updateChart();
			}
		});
	}

	private void createMirrorOptionSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(2, false));
		//
		Button buttonMirrored = new Button(composite, SWT.PUSH);
		buttonMirrored.setText("");
		buttonMirrored.setToolTipText("Set whether the data shall be displayed normal or mirrored.");
		buttonMirrored.setImage(ApplicationImageFactory.getInstance().getImage(displayMirrored ? IApplicationImage.IMAGE_MIRRORED_MASS_SPECTRUM : IApplicationImage.IMAGE_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		buttonMirrored.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				displayMirrored = !displayMirrored;
				buttonMirrored.setImage(ApplicationImageFactory.getInstance().getImage(displayMirrored ? IApplicationImage.IMAGE_MIRRORED_MASS_SPECTRUM : IApplicationImage.IMAGE_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
				updateChart();
			}
		});
		//
		Button buttonShifted = new Button(composite, SWT.PUSH);
		buttonShifted.setText("");
		buttonShifted.setToolTipText("Set whether the data shall be shifted or not.");
		buttonShifted.setImage(ApplicationImageFactory.getInstance().getImage(displayShifted ? IApplicationImage.IMAGE_MIRRORED_MASS_SPECTRUM : IApplicationImage.IMAGE_UNPIN_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		buttonShifted.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				displayShifted = !displayShifted;
				buttonShifted.setImage(ApplicationImageFactory.getInstance().getImage(displayShifted ? IApplicationImage.IMAGE_MIRRORED_MASS_SPECTRUM : IApplicationImage.IMAGE_UNPIN_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
				updateChart();
			}
		});
	}

	private void createScanChart(Composite parent) {

		scanChartUI = new ScanChartUI(parent, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private Composite createToolbarInfoReference(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfoComparison = new Label(composite, SWT.NONE);
		labelInfoComparison.setText("");
		labelInfoComparison.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the reference/comparison info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.toggleCompositeVisibility(toolbarInfoUnknown);
				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfoReference);
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

	private Button createButtonToggleToolbarOptions(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the options toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarOptions);
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

	private Button createSaveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Save both mass spectra.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					Shell shell = Display.getDefault().getActiveShell();
					if(scan1 != null) {
						MassSpectrumFileSupport.saveMassSpectrum(shell, scan1, "UnknownMS");
					}
					if(scan2 != null) {
						MassSpectrumFileSupport.saveMassSpectrum(shell, scan2, "ReferenceMS");
					}
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
		return button;
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

	private void applySettings() {

		updateChart();
	}
}
