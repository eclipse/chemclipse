/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.wizard.samplequant;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.core.SampleQuantProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.swt.SampleQuantTableViewerUI;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.core.ISupplier;
import org.eclipse.chemclipse.support.ui.swt.EnhancedCombo;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

public class PageDataVerification extends AbstractExtendedWizardPage {

	private static final Logger logger = Logger.getLogger(PageDataVerification.class);
	//
	private ISampleQuantWizardElements wizardElements;
	//
	private Button checkBoxValidate;
	private Text textMinMatchQuality;
	private Combo comboScanIdentifier;
	private SampleQuantTableViewerUI sampleQuantTableViewerUI;
	private IMassSpectrumIdentifierSupport massSpectrumIdentifierSupport = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();

	public PageDataVerification(ISampleQuantWizardElements wizardElements) {

		//
		super(PageDataVerification.class.getName());
		setTitle("Quantitation Entries");
		setDescription("Please select/verify the quantitation entries.");
		this.wizardElements = wizardElements;
	}

	@Override
	public boolean canFinish() {

		if(getMessage() != null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			if(sampleQuantTableViewerUI != null) {
				//
				SampleQuantProcessor sampleQuantProcessor = new SampleQuantProcessor();
				File fileAdditionalReportData = new File(wizardElements.getAdditionalReportData());
				File fileAreaPercentReport = new File(wizardElements.getAreaPercentReport());
				File fileQuantitationReport = new File(wizardElements.getQuantitationReport());
				List<ISampleQuantSubstance> sampleQuantSubstances = sampleQuantProcessor.extractSampleQuantSubstances(fileAdditionalReportData, fileAreaPercentReport, fileQuantitationReport);
				/*
				 * Set quant substances
				 */
				ISampleQuantReport sampleQuantReport = wizardElements.getSampleQuantReport();
				sampleQuantReport.getSampleQuantSubstances().clear();
				sampleQuantReport.getSampleQuantSubstances().addAll(sampleQuantSubstances);
				sampleQuantTableViewerUI.setInput(wizardElements.getSampleQuantReport().getSampleQuantSubstances());
			}
			//
			validate();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		/*
		 * Description
		 */
		createCheckBoxField(composite);
		createMatchQualityField(composite);
		createScanIdenfifierSection(composite);
		createInfoField(composite);
		createTableField(composite);
		//
		validate();
		//
		setControl(composite);
	}

	private void createCheckBoxField(Composite composite) {

		checkBoxValidate = new Button(composite, SWT.CHECK);
		checkBoxValidate.setText("Sample data is valid.");
		checkBoxValidate.setSelection(false);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		checkBoxValidate.setLayoutData(gridData);
		checkBoxValidate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				validate();
			}
		});
	}

	private void createMatchQualityField(Composite composite) {

		Label label = new Label(composite, SWT.NONE);
		label.setText("Min Match Quality:");
		//
		textMinMatchQuality = new Text(composite, SWT.BORDER);
		String minMatchQuality = Double.toString(PreferenceSupplier.INSTANCE().getPreferences().getDouble(PreferenceSupplier.P_SAMPLEQUANT_MIN_MATCH_QUALITY, PreferenceSupplier.DEF_SAMPLEQUANT_MIN_MATCH_QUALITY));
		textMinMatchQuality.setText(minMatchQuality);
		textMinMatchQuality.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textMinMatchQuality.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				try {
					double minMatchQuality = Double.parseDouble(textMinMatchQuality.getText().trim());
					if(minMatchQuality >= PreferenceSupplier.SAMPLEQUANT_MATCH_QUALITY_MIN && minMatchQuality <= PreferenceSupplier.SAMPLEQUANT_MATCH_QUALITY_MAX) {
						PreferenceSupplier.INSTANCE().getPreferences().putDouble(PreferenceSupplier.P_SAMPLEQUANT_MIN_MATCH_QUALITY, minMatchQuality);
						wizardElements.getSampleQuantReport().setMinMatchQuality(minMatchQuality);
						sampleQuantTableViewerUI.setInput(wizardElements.getSampleQuantReport().getSampleQuantSubstances());
					}
				} catch(Exception e1) {
					//
				}
				validate();
			}
		});
	}

	private void createScanIdenfifierSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Scan Identifier:");
		//
		comboScanIdentifier = EnhancedCombo.create(parent, SWT.READ_ONLY);
		String[] items;
		try {
			items = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport().getIdentifierNames();
		} catch(NoIdentifierAvailableException e1) {
			items = new String[]{"No scan identifier available."};
		}
		comboScanIdentifier.setItems(items);
		comboScanIdentifier.select(getSelectionIndexScanIdentifier());
		comboScanIdentifier.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboScanIdentifier.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				if(scanIdentifierExists()) {
					try {
						String identifierId = getScanIdentifierId();
						IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
						preferences.put(PreferenceSupplier.P_SAMPLEQUANT_SCAN_IDENTIFIER, identifierId);
						preferences.flush();
					} catch(BackingStoreException e1) {
						logger.warn(e1);
					}
				}
				validate();
			}
		});
	}

	private void createInfoField(Composite composite) {

		Label label = new Label(composite, SWT.NONE);
		label.setText("The following quantitation entries will be used for analysis.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		label.setLayoutData(gridData);
	}

	private void createTableField(Composite composite) {

		sampleQuantTableViewerUI = new SampleQuantTableViewerUI(composite, SWT.BORDER);
		Table table = sampleQuantTableViewerUI.getTable();
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		table.setLayoutData(gridData);
	}

	private boolean scanIdentifierExists() {

		try {
			List<String> identifierIds = massSpectrumIdentifierSupport.getAvailableIdentifierIds();
			for(int i = 0; i < identifierIds.size(); i++) {
				ISupplier supplier = massSpectrumIdentifierSupport.getIdentifierSupplier(identifierIds.get(i));
				if(comboScanIdentifier.getText().trim().equals(supplier.getIdentifierName())) {
					return true;
				}
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}

	private String getScanIdentifierId() {

		try {
			List<String> identifierIds = massSpectrumIdentifierSupport.getAvailableIdentifierIds();
			for(int i = 0; i < identifierIds.size(); i++) {
				ISupplier supplier = massSpectrumIdentifierSupport.getIdentifierSupplier(identifierIds.get(i));
				if(comboScanIdentifier.getText().trim().equals(supplier.getIdentifierName())) {
					return supplier.getId();
				}
			}
		} catch(Exception e) {
			return "";
		}
		return "";
	}

	private int getSelectionIndexScanIdentifier() {

		String identifierId = PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_SAMPLEQUANT_SCAN_IDENTIFIER, PreferenceSupplier.DEF_SAMPLEQUANT_SCAN_IDENTIFIER);
		try {
			List<String> identifierIds = massSpectrumIdentifierSupport.getAvailableIdentifierIds();
			for(int i = 0; i < identifierIds.size(); i++) {
				if(identifierIds.get(i).equals(identifierId)) {
					return i;
				}
			}
		} catch(Exception e) {
			return -1;
		}
		return -1;
	}

	private void validate() {

		String message = null;
		//
		try {
			double minMatchQuality = Double.parseDouble(textMinMatchQuality.getText().trim());
			if(minMatchQuality < PreferenceSupplier.SAMPLEQUANT_MATCH_QUALITY_MIN || minMatchQuality > PreferenceSupplier.SAMPLEQUANT_MATCH_QUALITY_MAX) {
				message = "The min match quality must be in the range of " + PreferenceSupplier.SAMPLEQUANT_MATCH_QUALITY_MIN + " - " + PreferenceSupplier.SAMPLEQUANT_MATCH_QUALITY_MAX + ".";
			}
		} catch(Exception e) {
			message = "Please set a valid min match quality.";
		}
		//
		if(message == null) {
			if(!scanIdentifierExists()) {
				message = "Please select a valid scan identifier.";
			}
		}
		//
		if(message == null) {
			wizardElements.setDataVerified(checkBoxValidate.getSelection());
			if(!checkBoxValidate.getSelection()) {
				message = "Please verify the data and activate the check box.";
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
