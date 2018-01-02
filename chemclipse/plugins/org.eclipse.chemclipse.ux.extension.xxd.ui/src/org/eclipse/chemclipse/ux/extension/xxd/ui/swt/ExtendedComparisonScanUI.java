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
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectrumFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ExtendedComparisonScanUI {

	private static final Logger logger = Logger.getLogger(ExtendedComparisonScanUI.class);
	//
	private static final float NORMALIZATION_FACTOR = 1000.0f;
	//
	private Label labelInfoReference;
	private Composite toolbarInfoUnknown;
	private ScanChartUI scanChartUI;
	private Label labelInfoComparison;
	private Composite toolbarInfoReference;
	//
	private IScanMSD scan1 = null;
	private IScanMSD scan2 = null;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	@Inject
	public ExtendedComparisonScanUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateScan();
	}

	public void update(IScanMSD unknownMassSpectrum, IIdentificationTarget identificationTarget) {

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
		updateScan();
	}

	private void updateScan() {

		if(scan1 != null && scan2 != null) {
			boolean displayDifference = true;
			if(displayDifference) {
				updateScanComparisonDifference(true);
			} else {
				updateScanComparisonNormal(true);
			}
		} else {
			scanChartUI.setInput(scan1);
		}
	}

	private void updateScanComparisonNormal(boolean mirrored) {

		labelInfoReference.setText(getMassSpectrumLabel(scan1, "[U] UNKNOWN MS = "));
		labelInfoComparison.setText(getMassSpectrumLabel(scan2, "[L] REFERENCE MS = "));
		scanChartUI.setInput(scan1, scan2, mirrored);
	}

	private void updateScanComparisonDifference(boolean mirrored) {

		labelInfoReference.setText(getMassSpectrumLabel(scan1, "[U-L] UNKNOWN MS = "));
		labelInfoComparison.setText(getMassSpectrumLabel(scan2, "[U-L] REFERENCE MS = "));
		//
		IExtractedIonSignal extractedIonSignalReference = scan1.getExtractedIonSignal();
		IExtractedIonSignal extractedIonSignalComparison = scan2.getExtractedIonSignal();
		int startIon = (extractedIonSignalReference.getStartIon() < extractedIonSignalComparison.getStartIon()) ? extractedIonSignalReference.getStartIon() : extractedIonSignalComparison.getStartIon();
		int stopIon = (extractedIonSignalReference.getStopIon() > extractedIonSignalComparison.getStopIon()) ? extractedIonSignalReference.getStopIon() : extractedIonSignalComparison.getStopIon();
		//
		scan1.removeAllIons();
		scan2.removeAllIons();
		//
		for(int ion = startIon; ion <= stopIon; ion++) {
			float abundance = extractedIonSignalReference.getAbundance(ion) - extractedIonSignalComparison.getAbundance(ion);
			if(abundance < 0) {
				abundance *= -1;
				scan2.addIon(getIon(ion, abundance));
			} else {
				scan1.addIon(getIon(ion, abundance));
			}
		}
		//
		scanChartUI.setInput(scan1, scan2, mirrored);
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
		createScanChart(parent);
		toolbarInfoReference = createToolbarInfoReference(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfoUnknown, true);
		PartSupport.setCompositeVisibility(toolbarInfoReference, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToggleToolbarInfo(composite);
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

		updateScan();
	}
}
