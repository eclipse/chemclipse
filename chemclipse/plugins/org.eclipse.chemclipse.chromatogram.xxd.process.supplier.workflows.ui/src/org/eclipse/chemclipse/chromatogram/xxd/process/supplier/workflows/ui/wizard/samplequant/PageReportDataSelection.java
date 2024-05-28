/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 *
 * All rights reserved.
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.wizard.samplequant;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

public class PageReportDataSelection extends AbstractExtendedWizardPage {

	private static final Logger logger = Logger.getLogger(PageReportDataSelection.class);
	//
	private ISampleQuantWizardElements wizardElements;
	private Text textAreaPercentReport;
	private Text textQuantitationReport;
	private Text textAdditionalReportData;

	public PageReportDataSelection(ISampleQuantWizardElements wizardElements) {

		//
		super(PageReportDataSelection.class.getName());
		setTitle("Report Files");
		setDescription("Please select the report files.");
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
			if(!wizardElements.getSelectedChromatograms().isEmpty()) {
				/*
				 * Get the chromatogram.
				 */
				String chromatogram = wizardElements.getSelectedChromatograms().get(0);
				wizardElements.getSampleQuantReport().setPathChromatogramEdited(chromatogram);
				File chromatogramFile = new File(chromatogram);
				/*
				 * Try to find the rteres.txt
				 */
				if(textAreaPercentReport.getText().trim().equals("")) {
					File fileAreaPercent = searchForFile(chromatogramFile, "rteres.txt");
					if(fileAreaPercent != null) {
						textAreaPercentReport.setText(fileAreaPercent.getAbsolutePath());
					}
				}
				/*
				 * Try to find the SumRpt.txt
				 */
				if(textQuantitationReport.getText().trim().equals("")) {
					File fileQuantitation = searchForFile(chromatogramFile, "SumRpt.txt");
					if(fileQuantitation != null) {
						textQuantitationReport.setText(fileQuantitation.getAbsolutePath());
					}
				}
			}
			validate();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		createAreaPercentField(composite);
		createQuantitationReportField(composite);
		createAdditionalReportDataField(composite);
		//
		validate();
		//
		setControl(composite);
	}

	private void createAreaPercentField(Composite composite) {

		textAreaPercentReport = new Text(composite, SWT.BORDER);
		textAreaPercentReport.setText("");
		textAreaPercentReport.setLayoutData(getGridDataText());
		textAreaPercentReport.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				validate();
			}
		});
		//
		Button buttonSelect = new Button(composite, SWT.PUSH);
		buttonSelect.setText("rteres.txt");
		buttonSelect.setLayoutData(getGridDataButton());
		buttonSelect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				FileDialog fileDialog = new FileDialog(shell, SWT.READ_ONLY);
				fileDialog.setText("Select the peaks area percent report.");
				fileDialog.setFilterExtensions(new String[]{"*.txt"});
				fileDialog.setFilterNames(new String[]{"rteres.txt"});
				fileDialog.setFilterPath(PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_SAMPLEQUANT_FILTER_PATH_RTERES, PreferenceSupplier.DEF_SAMPLEQUANT_FILTER_PATH_RTERES));
				String pathname = fileDialog.open();
				if(pathname != null) {
					try {
						IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
						preferences.put(PreferenceSupplier.P_SAMPLEQUANT_FILTER_PATH_RTERES, fileDialog.getFilterPath());
						preferences.flush();
					} catch(BackingStoreException e1) {
						logger.warn(e1);
					}
					textAreaPercentReport.setText(pathname);
				}
			}
		});
	}

	private void createQuantitationReportField(Composite composite) {

		textQuantitationReport = new Text(composite, SWT.BORDER);
		textQuantitationReport.setText("");
		textQuantitationReport.setLayoutData(getGridDataText());
		textQuantitationReport.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				validate();
			}
		});
		//
		Button buttonSelect = new Button(composite, SWT.PUSH);
		buttonSelect.setText("SumRpt.txt");
		buttonSelect.setLayoutData(getGridDataButton());
		buttonSelect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				FileDialog fileDialog = new FileDialog(shell, SWT.READ_ONLY);
				fileDialog.setText("Selec the quantitation report.");
				fileDialog.setFilterExtensions(new String[]{"*.txt"});
				fileDialog.setFilterNames(new String[]{"SumRpt.txt"});
				fileDialog.setFilterPath(PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_SAMPLEQUANT_FILTER_PATH_SUMRPT, PreferenceSupplier.DEF_SAMPLEQUANT_FILTER_PATH_SUMRPT));
				String pathname = fileDialog.open();
				if(pathname != null) {
					try {
						IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
						preferences.put(PreferenceSupplier.P_SAMPLEQUANT_FILTER_PATH_SUMRPT, fileDialog.getFilterPath());
						preferences.flush();
					} catch(BackingStoreException e1) {
						logger.warn(e1);
					}
					textQuantitationReport.setText(pathname);
				}
			}
		});
	}

	private void createAdditionalReportDataField(Composite composite) {

		textAdditionalReportData = new Text(composite, SWT.BORDER);
		textAdditionalReportData.setText(PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_SAMPLEQUANT_FILTER_PATH_TARGETS, PreferenceSupplier.DEF_SAMPLEQUANT_FILTER_PATH_TARGETS));
		textAdditionalReportData.setLayoutData(getGridDataText());
		textAdditionalReportData.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				validate();
			}
		});
		//
		Button buttonSelect = new Button(composite, SWT.PUSH);
		buttonSelect.setText("targets.txt");
		buttonSelect.setLayoutData(getGridDataButton());
		buttonSelect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				FileDialog fileDialog = new FileDialog(shell, SWT.READ_ONLY);
				fileDialog.setText("Selec the additional report data.");
				fileDialog.setFilterExtensions(new String[]{"*.txt"});
				fileDialog.setFilterNames(new String[]{"targets.txt"});
				fileDialog.setFilterPath(PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_SAMPLEQUANT_FILTER_PATH_TARGETS, PreferenceSupplier.DEF_SAMPLEQUANT_FILTER_PATH_TARGETS));
				String pathname = fileDialog.open();
				if(pathname != null) {
					try {
						IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
						String filterPathTargets = fileDialog.getFilterPath() + File.separator + fileDialog.getFileName();
						preferences.put(PreferenceSupplier.P_SAMPLEQUANT_FILTER_PATH_TARGETS, filterPathTargets);
						preferences.flush();
					} catch(BackingStoreException e1) {
						logger.warn(e1);
					}
					textAdditionalReportData.setText(pathname);
				}
			}
		});
	}

	/**
	 * Use this grid data for the button.
	 * 
	 * @return GridData
	 */
	protected GridData getGridDataText() {

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalIndent = 5;
		gridData.grabExcessHorizontalSpace = true;
		return gridData;
	}

	/**
	 * Use this grid data for the button.
	 * 
	 * @return GridData
	 */
	protected GridData getGridDataButton() {

		GridData gridData = new GridData();
		gridData.verticalIndent = 5;
		gridData.widthHint = 130;
		return gridData;
	}

	/**
	 * May return null.
	 * 
	 * @param file
	 * @param name
	 * @return File
	 */
	private File searchForFile(File file, String name) {

		if(file.isDirectory()) {
			for(File dirFile : file.listFiles()) {
				File fileFound = searchForFile(dirFile, name);
				if(fileFound != null) {
					return fileFound;
				}
			}
		} else {
			if(file.getName().equals(name)) {
				return file;
			}
		}
		return null;
	}

	private void validate() {

		/*
		 * Additional data may be null.
		 */
		String message = null;
		/*
		 * targets.txt - no check
		 */
		String additionalReportData = textAdditionalReportData.getText().trim();
		if(!additionalReportData.equals("")) {
			File file = new File(additionalReportData);
			if(file.exists()) {
				wizardElements.setAdditionalReportData(additionalReportData);
			}
		}
		/*
		 * Chromatogram
		 */
		if(wizardElements.getSelectedChromatograms().isEmpty()) {
			message = "Please select a chromatogram.";
		}
		/*
		 * rteres.txt
		 */
		if(message == null) {
			String areaPercentReport = textAreaPercentReport.getText().trim();
			if(areaPercentReport.equals("")) {
				message = "Please select a valid area percent report.";
			} else {
				File file = new File(areaPercentReport);
				if(file.exists()) {
					wizardElements.setAreaPercentReport(areaPercentReport);
				} else {
					message = "The selected area percent report doesn't exist.";
				}
			}
		}
		/*
		 * SumRpt.txt
		 */
		if(message == null) {
			String quantitationReport = textQuantitationReport.getText().trim();
			if(quantitationReport.equals("")) {
				message = "Please select a valid quantitation report.";
			} else {
				File file = new File(quantitationReport);
				if(file.exists()) {
					wizardElements.setQuantitationReport(quantitationReport);
				} else {
					message = "The selected quantitation report doesn't exist.";
				}
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
