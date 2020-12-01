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
 * Christoph Läubrich - make this configureable, null check for scan
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.runnables.LibraryServiceRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanDataSupport;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
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

public class ExtendedComparisonScanUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedComparisonScanUI.class);
	//
	private static final float NORMALIZATION_FACTOR = 1000.0f;
	//
	private static final String OPTION_UPDATE_SCAN_1 = "UPDATE_SCAN_1";
	private static final String OPTION_UPDATE_SCAN_2 = "UPDATE_SCAN_2";
	private static final String OPTION_LIBRARY_SEARCH = "LIBRARY_SEARCH";
	//
	private static final String PREFIX_U = "[U]";
	private static final String PREFIX_R = "[R]";
	private static final String PREFIX_UR = "[U-R]";
	private static final String LABEL_UR_NORMAL = "[U,R]";
	private static final String LABEL_UR_DIFFERENCE = PREFIX_UR;
	private static final String TITLE_UNKNOWN = "UNKNOWN MS";
	private static final String TITLE_REFERENCE = "REFERENCE MS";
	private static final String POSTFIX_NONE = "";
	private static final String POSTFIX_SHIFTED = " SHIFTED (+1)";
	//
	private Button buttonOptimizedScan;
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfoTop = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfoBottom = new AtomicReference<>();
	private ScanChartUI scanChartUI;
	private Button buttonToolbarEdit;
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	//
	private IScanMSD scan1 = null;
	private IScanMSD scan2 = null;
	private IScanMSD scan1Optimized = null;
	private IScanMSD scan2Optimized = null;
	//
	private String displayOption = OPTION_LIBRARY_SEARCH;
	private boolean displayDifference = false;
	private boolean displayMirrored = true;
	private boolean displayShifted = false;
	//
	private final ScanDataSupport scanDataSupport = new ScanDataSupport();

	@Inject
	public ExtendedComparisonScanUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Focus
	public boolean setFocus() {

		Display.getDefault().asyncExec(this::updateChart);
		return true;
	}

	public void clear() {

		IScanMSD scanMSD = null;
		update(scanMSD);
	}

	public void update(IScanMSD scanMSD) {

		if(displayOption.equals(OPTION_UPDATE_SCAN_1)) {
			scan1Optimized = null;
			if(scanMSD == null) {
				scan1 = null;
				buttonOptimizedScan.setEnabled(false);
			} else {
				try {
					scan1 = scanMSD.makeDeepCopy().normalize(NORMALIZATION_FACTOR);
					buttonOptimizedScan.setEnabled(true);
				} catch(CloneNotSupportedException e) {
					logger.warn(e);
				}
			}
			Display.getDefault().asyncExec(this::updateChart);
		} else if(displayOption.equals(OPTION_UPDATE_SCAN_2)) {
			scan2Optimized = null;
			if(scanMSD == null) {
				scan2 = null;
				buttonOptimizedScan.setEnabled(false);
			} else {
				try {
					scan2 = scanMSD.makeDeepCopy().normalize(NORMALIZATION_FACTOR);
					buttonOptimizedScan.setEnabled(true);
				} catch(CloneNotSupportedException e) {
					logger.warn(e);
				}
			}
			Display.getDefault().asyncExec(this::updateChart);
		}
	}

	public void update(IIdentificationTarget identificationTarget) {

		if(displayOption.equals(OPTION_LIBRARY_SEARCH)) {
			updateIdentificationTarget(identificationTarget);
		}
	}

	public void update(IScanMSD unknownMassSpectrum, IIdentificationTarget identificationTarget) {

		if(displayOption.equals(OPTION_LIBRARY_SEARCH)) {
			scan1 = copyScan(unknownMassSpectrum);
			updateIdentificationTarget(identificationTarget);
		}
	}

	public void update(IScanMSD unknownMassSpectrum, IScanMSD referenceMassSpectrum) {

		scan1 = copyScan(unknownMassSpectrum);
		scan2 = copyScan(referenceMassSpectrum);
		Display.getDefault().asyncExec(this::updateChart);
	}

	private void updateIdentificationTarget(IIdentificationTarget identificationTarget) {

		scan1Optimized = null;
		scan2Optimized = null;
		LibraryServiceRunnable runnable = new LibraryServiceRunnable(identificationTarget, new Consumer<IScanMSD>() {

			@Override
			public void accept(IScanMSD referenceMassSpectrum) {

				scan2 = copyScan(referenceMassSpectrum);
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						buttonOptimizedScan.setEnabled(true);
						updateChart();
					}
				});
			}
		});
		/*
		 * 
		 */
		try {
			if(runnable.requireProgressMonitor()) {
				DisplayUtils.executeInUserInterfaceThread(() -> {
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(scanChartUI.getShell());
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
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, getClass().getName(), "Update scan failed.", e));
		}
	}

	private static IScanMSD copyScan(IScanMSD scan) {

		if(scan != null) {
			try {
				return scan.makeDeepCopy().normalize(NORMALIZATION_FACTOR);
			} catch(CloneNotSupportedException e) {
			}
		}
		return null;
	}

	private void updateChart() {

		if(scan1 != null && scan2 != null) {
			if(displayDifference) {
				updateScanComparisonDifference(displayMirrored, displayShifted);
			} else {
				updateScanComparisonNormal(displayMirrored, displayShifted);
			}
		} else {
			updateScanNormal();
		}
	}

	private void updateScanComparisonNormal(boolean mirrored, boolean shifted) {

		IScanMSD scan_1 = (scan1Optimized != null) ? scan1Optimized : scan1;
		IScanMSD scan_2 = (scan2Optimized != null) ? scan2Optimized : scan2;
		//
		toolbarInfoTop.get().setText(scanDataSupport.getMassSpectrumLabel(scan_1, PREFIX_U, TITLE_UNKNOWN, POSTFIX_NONE));
		toolbarInfoBottom.get().setText(scanDataSupport.getMassSpectrumLabel(scan_2, PREFIX_R, TITLE_REFERENCE, shifted ? POSTFIX_SHIFTED : POSTFIX_NONE));
		//
		if(shifted) {
			IScanMSD scan2Shifted = new ScanMSD();
			IExtractedIonSignal extractedIonSignalScan2 = scan_2.getExtractedIonSignal();
			int startIon = extractedIonSignalScan2.getStartIon();
			int stopIon = extractedIonSignalScan2.getStopIon();
			for(int ion = startIon; ion <= stopIon; ion++) {
				float abundance = extractedIonSignalScan2.getAbundance(ion);
				if(abundance > 0) {
					scan2Shifted.addIon(getIon(ion + 1, abundance));
				}
			}
			scanChartUI.setInput(scan_1, scan2Shifted, mirrored);
		} else {
			scanChartUI.setInput(scan_1, scan_2, mirrored);
		}
	}

	private void updateScanComparisonDifference(boolean mirrored, boolean shifted) {

		IScanMSD scan_1 = (scan1Optimized != null) ? scan1Optimized : scan1;
		IScanMSD scan_2 = (scan2Optimized != null) ? scan2Optimized : scan2;
		//
		toolbarInfoTop.get().setText(scanDataSupport.getMassSpectrumLabel(scan_1, PREFIX_UR, TITLE_UNKNOWN, POSTFIX_NONE));
		toolbarInfoBottom.get().setText(scanDataSupport.getMassSpectrumLabel(scan_2, PREFIX_UR, TITLE_REFERENCE, shifted ? POSTFIX_SHIFTED : POSTFIX_NONE));
		//
		IExtractedIonSignal extractedIonSignalReference = scan_1.getExtractedIonSignal();
		IExtractedIonSignal extractedIonSignalComparison = scan_2.getExtractedIonSignal();
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

	private void updateScanNormal() {

		toolbarInfoTop.get().setText("");
		toolbarInfoBottom.get().setText("");
		//
		if(scan1 != null) {
			IScanMSD scan_1 = (scan1Optimized != null) ? scan1Optimized : scan1;
			toolbarInfoTop.get().setText(scanDataSupport.getMassSpectrumLabel(scan_1, PREFIX_U, TITLE_UNKNOWN, POSTFIX_NONE));
			scanChartUI.setInput(scan_1);
		} else if(scan2 != null) {
			IScanMSD scan_2 = (scan2Optimized != null) ? scan2Optimized : scan2;
			toolbarInfoTop.get().setText(scanDataSupport.getMassSpectrumLabel(scan_2, PREFIX_U, TITLE_UNKNOWN, POSTFIX_NONE));
			scanChartUI.setInput(scan_2);
		} else {
			scanChartUI.setInput(null);
		}
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

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfoTop(this);
		createToolbarOptions(this);
		createScanChart(this);
		createToolbarInfoBottom(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfoTop, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarInfoBottom, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarEdit, buttonToolbarEdit, IApplicationImage.IMAGE_EDIT, TOOLTIP_EDIT, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, Arrays.asList(toolbarInfoTop, toolbarInfoBottom), IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarEdit = createButtonToggleToolbar(composite, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT);
		createResetButton(composite);
		createSaveButton(composite);
		buttonOptimizedScan = createOptimizedScanButton(composite);
		createSettingsButton(composite);
	}

	private void createToolbarOptions(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createUpdateGroup(composite);
		createDisplayGroup(composite);
		createMirrorOptionSection(composite);
		//
		toolbarEdit.set(composite);
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

				displayOption = OPTION_UPDATE_SCAN_2;
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

				displayOption = OPTION_LIBRARY_SEARCH;
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
		buttonNormalModus.setText(LABEL_UR_NORMAL);
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
		buttonDifferenceModus.setText(LABEL_UR_DIFFERENCE);
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
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
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
		buttonShifted.setImage(ApplicationImageFactory.getInstance().getImage(displayShifted ? IApplicationImage.IMAGE_SHIFTED_MASS_SPECTRUM : IApplicationImage.IMAGE_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		buttonShifted.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				displayShifted = !displayShifted;
				buttonShifted.setImage(ApplicationImageFactory.getInstance().getImage(displayShifted ? IApplicationImage.IMAGE_SHIFTED_MASS_SPECTRUM : IApplicationImage.IMAGE_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
				updateChart();
			}
		});
	}

	private void createScanChart(Composite parent) {

		scanChartUI = new ScanChartUI(parent, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
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
		//
		return informationUI;
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chart.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					if(scan1 != null) {
						DatabaseFileSupport.saveMassSpectrum(DisplayUtils.getShell(), scan1, "UnknownMS");
					}
					if(scan2 != null) {
						DatabaseFileSupport.saveMassSpectrum(DisplayUtils.getShell(), scan2, "ReferenceMS");
					}
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

				try {
					/*
					 * Scan 1
					 */
					if(scan1.getOptimizedMassSpectrum() != null) {
						scan1Optimized = scan1.getOptimizedMassSpectrum().makeDeepCopy().normalize(NORMALIZATION_FACTOR);
					}
					/*
					 * Scan 2
					 */
					if(scan2.getOptimizedMassSpectrum() != null) {
						scan2Optimized = scan2.getOptimizedMassSpectrum().makeDeepCopy().normalize(NORMALIZATION_FACTOR);
					}
					//
					button.setEnabled(false);
					updateChart();
				} catch(CloneNotSupportedException e1) {
					logger.warn(e1);
				}
			}
		});
		return button;
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageScans.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void reset() {

		scan1Optimized = null;
		scan2Optimized = null;
		buttonOptimizedScan.setEnabled(true);
		updateChart();
	}

	private void applySettings() {

		updateChart();
	}
}
