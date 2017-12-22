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

import java.text.DecimalFormat;

import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.support.FilterSupport;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectrumFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ScanChartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSubtract;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
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

public class ExtendedComparisonScanUI {

	private static final Logger logger = Logger.getLogger(ExtendedComparisonScanUI.class);
	//
	private Label labelInfoReference;
	private Composite toolbarInfoReference;
	private ScanChartUI scanChartUI;
	private Label labelInfoComparison;
	private Composite toolbarInfoComparison;
	//
	private IScanMSD scanMSD;
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

	public void update(Object object) {

		IScanMSD scanMSD = null;
		if(object instanceof IScanMSD) {
			scanMSD = (IScanMSD)object;
		} else if(object instanceof IPeakMSD) {
			IPeakMSD peakMSD = (IPeakMSD)object;
			scanMSD = peakMSD.getExtractedMassSpectrum();
		} else if(object instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)object;
			scanMSD = FilterSupport.getCombinedMassSpectrum(chromatogramSelectionMSD, null, true);
		}
		//
		this.scanMSD = scanMSD;
		labelInfoReference.setText(getMassSpectrumLabel(scanMSD, "UNKNOWN MS = "));
		labelInfoComparison.setText(getMassSpectrumLabel(scanMSD, "LIBRARY MS = "));
		//
		updateScan();
	}

	private void updateScan() {

		IScanMSD referenceMassSpectrum = scanMSD;
		IScanMSD comparisonMassSpectrum = scanMSD; // TODO
		//
		if(referenceMassSpectrum != null && comparisonMassSpectrum != null) {
			try {
				IScanMSD referenceMassSpectrumCopy = referenceMassSpectrum.makeDeepCopy().normalize(1000.0f);
				IScanMSD comparisonMassSpectrumCopy = comparisonMassSpectrum.makeDeepCopy().normalize(1000.0f);
				scanChartUI.setInput(scanMSD);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		} else {
			scanChartUI.setInput(scanMSD);
		}
	}

	private String getMassSpectrumLabel(IScanMSD massSpectrum, String title) {

		StringBuilder builder = new StringBuilder();
		builder.append(title);
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
		//
		return builder.toString();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfoReference = createToolbarInfoReference(parent);
		createScanChart(parent);
		toolbarInfoComparison = createToolbarInfoComparison(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfoReference, true);
		PartSupport.setCompositeVisibility(toolbarInfoComparison, true);
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

	private Composite createToolbarInfoReference(Composite parent) {

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

	private Composite createToolbarInfoComparison(Composite parent) {

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

				PartSupport.toggleCompositeVisibility(toolbarInfoReference);
				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfoComparison);
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
		button.setToolTipText("Save the combined scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					if(scanMSD != null) {
						MassSpectrumFileSupport.saveMassSpectrum(scanMSD);
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
