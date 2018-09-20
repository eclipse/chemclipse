/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.wizard.evaluation;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.chemclipse.xxd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.xxd.process.supplier.BaselineDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramCalculatorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIdentifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.prefs.BackingStoreException;

public class PageProcessing extends AbstractExtendedWizardPage {

	private static final Logger logger = Logger.getLogger(PageProcessing.class);
	private static final int NO_SELECTION = -1;
	//
	private IEvaluationWizardElements wizardElements;
	private ProcessTypeSupport processTypeSupport;
	//
	private Combo comboChromatogramMSDFilter;
	private Combo comboChromatogramFilter;
	private Combo comboBaselineDetector;
	private Combo comboPeakDetector;
	private Combo comboPeakIntegrator;
	private Combo comboChromatogramCalculator;
	private Combo comboPeakIdentifier;

	public PageProcessing(IEvaluationWizardElements wizardElements) {
		//
		super(PageProcessing.class.getName());
		setTitle("Process Parameters");
		setDescription("Please set the process parameters.");
		this.wizardElements = wizardElements;
		processTypeSupport = new ProcessTypeSupport();
	}

	@Override
	public boolean canFinish() {

		return true;
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			//
			validateProcessSettings();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		createFilterMSDSection(composite);
		createFilterSection(composite);
		createBaselineDetectorSection(composite);
		createPeakDetectorSection(composite);
		createPeakIntegratorSection(composite);
		createChromatogramCalculatorSection(composite);
		createPeakIdentifierSection(composite);
		//
		validateProcessSettings();
		//
		setControl(composite);
	}

	private void createFilterMSDSection(Composite parent) {

		String category = ChromatogramFilterTypeSupplierMSD.CATEGORY;
		GridData gridData;
		/*
		 * Analysis system.
		 */
		Label label = new Label(parent, SWT.NONE);
		label.setText(category);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 5;
		label.setLayoutData(gridData);
		//
		String id = PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_EVALUATION_CHROMATOGRAM_MSD_FILTER, PreferenceSupplier.DEF_EVALUATION_CHROMATOGRAM_MSD_FILTER);
		String[][] elements = PreferenceSupplier.getChromatogramFilterMSD();
		int index = getIndex(id, elements);
		//
		comboChromatogramMSDFilter = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		comboChromatogramMSDFilter.setItems(processTypeSupport.getProcessorNames(category, processTypeSupport.getPluginIds(category)));
		comboChromatogramMSDFilter.select(index);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		comboChromatogramMSDFilter.setLayoutData(gridData);
		comboChromatogramMSDFilter.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateProcessSettings();
			}
		});
	}

	private void createFilterSection(Composite parent) {

		String category = ChromatogramFilterTypeSupplier.CATEGORY;
		GridData gridData;
		/*
		 * Analysis system.
		 */
		Label label = new Label(parent, SWT.NONE);
		label.setText(category);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 5;
		label.setLayoutData(gridData);
		//
		String id = PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_EVALUATION_CHROMATOGRAM_FILTER, PreferenceSupplier.DEF_EVALUATION_CHROMATOGRAM_FILTER);
		String[][] elements = PreferenceSupplier.getChromatogramFilter();
		int index = getIndex(id, elements);
		//
		comboChromatogramFilter = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		comboChromatogramFilter.setItems(processTypeSupport.getProcessorNames(category, processTypeSupport.getPluginIds(category)));
		comboChromatogramFilter.select(index);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		comboChromatogramFilter.setLayoutData(gridData);
		comboChromatogramFilter.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateProcessSettings();
			}
		});
	}

	private void createBaselineDetectorSection(Composite parent) {

		String category = BaselineDetectorTypeSupplier.CATEGORY;
		GridData gridData;
		/*
		 * Analysis system.
		 */
		Label label = new Label(parent, SWT.NONE);
		label.setText(category);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 5;
		label.setLayoutData(gridData);
		//
		String id = PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_EVALUATION_BASELINE_DETECTOR, PreferenceSupplier.DEF_EVALUATION_BASELINE_DETECTOR);
		String[][] elements = PreferenceSupplier.getBaselineDetectors();
		int index = getIndex(id, elements);
		//
		comboBaselineDetector = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		comboBaselineDetector.setItems(processTypeSupport.getProcessorNames(category, processTypeSupport.getPluginIds(category)));
		comboBaselineDetector.select(index);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		comboBaselineDetector.setLayoutData(gridData);
		comboBaselineDetector.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateProcessSettings();
			}
		});
	}

	private void createPeakDetectorSection(Composite parent) {

		String category = PeakDetectorTypeSupplier.CATEGORY;
		GridData gridData;
		/*
		 * Analysis system.
		 */
		Label label = new Label(parent, SWT.NONE);
		label.setText(category);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 5;
		label.setLayoutData(gridData);
		//
		String id = PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_EVALUATION_PEAK_DETECTOR, PreferenceSupplier.DEF_EVALUATION_PEAK_DETECTOR);
		String[][] elements = PreferenceSupplier.getPeakDetectors();
		int index = getIndex(id, elements);
		//
		comboPeakDetector = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		comboPeakDetector.setItems(processTypeSupport.getProcessorNames(category, processTypeSupport.getPluginIds(category)));
		comboPeakDetector.select(index);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		comboPeakDetector.setLayoutData(gridData);
		comboPeakDetector.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateProcessSettings();
			}
		});
	}

	private void createPeakIntegratorSection(Composite parent) {

		String category = PeakIntegratorTypeSupplier.CATEGORY;
		GridData gridData;
		/*
		 * Analysis system.
		 */
		Label label = new Label(parent, SWT.NONE);
		label.setText(category);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 5;
		label.setLayoutData(gridData);
		//
		String id = PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_EVALUATION_PEAK_INTEGRATOR, PreferenceSupplier.DEF_EVALUATION_PEAK_INTEGRATOR);
		String[][] elements = PreferenceSupplier.getPeakIntegrators();
		int index = getIndex(id, elements);
		//
		comboPeakIntegrator = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		comboPeakIntegrator.setItems(processTypeSupport.getProcessorNames(category, processTypeSupport.getPluginIds(category)));
		comboPeakIntegrator.select(index);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		comboPeakIntegrator.setLayoutData(gridData);
		comboPeakIntegrator.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateProcessSettings();
			}
		});
	}

	private void createChromatogramCalculatorSection(Composite parent) {

		String category = ChromatogramCalculatorTypeSupplier.CATEGORY;
		GridData gridData;
		/*
		 * Calculators
		 */
		Label label = new Label(parent, SWT.NONE);
		label.setText(category);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 5;
		label.setLayoutData(gridData);
		//
		String id = PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_EVALUATION_CHROMATOGRAM_CALCULATOR, PreferenceSupplier.DEF_EVALUATION_CHROMATOGRAM_CALCULATOR);
		String[][] elements = PreferenceSupplier.getChromatogramCalculators();
		int index = getIndex(id, elements);
		//
		comboChromatogramCalculator = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		comboChromatogramCalculator.setItems(processTypeSupport.getProcessorNames(category, processTypeSupport.getPluginIds(category)));
		comboChromatogramCalculator.select(index);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		comboChromatogramCalculator.setLayoutData(gridData);
		comboChromatogramCalculator.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateProcessSettings();
			}
		});
	}

	private void createPeakIdentifierSection(Composite parent) {

		String category = PeakIdentifierTypeSupplier.CATEGORY;
		GridData gridData;
		/*
		 * Analysis system.
		 */
		Label label = new Label(parent, SWT.NONE);
		label.setText(category);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 5;
		label.setLayoutData(gridData);
		//
		String id = PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_EVALUATION_PEAK_IDENTIFIER, PreferenceSupplier.DEF_EVALUATION_PEAK_IDENTIFIER);
		String[][] elements = PreferenceSupplier.getPeakIdentifier();
		int index = getIndex(id, elements);
		//
		comboPeakIdentifier = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		comboPeakIdentifier.setItems(processTypeSupport.getProcessorNames(category, processTypeSupport.getPluginIds(category)));
		comboPeakIdentifier.select(index);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		comboPeakIdentifier.setLayoutData(gridData);
		comboPeakIdentifier.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateProcessSettings();
			}
		});
	}

	private void validateProcessSettings() {

		String message = null;
		message = validateComboSelection(ChromatogramFilterTypeSupplierMSD.CATEGORY, comboChromatogramMSDFilter.getText().trim());
		if(message == null) {
			message = validateComboSelection(ChromatogramFilterTypeSupplier.CATEGORY, comboChromatogramFilter.getText().trim());
		}
		if(message == null) {
			message = validateComboSelection(BaselineDetectorTypeSupplier.CATEGORY, comboBaselineDetector.getText().trim());
		}
		if(message == null) {
			message = validateComboSelection(PeakDetectorTypeSupplier.CATEGORY, comboPeakDetector.getText().trim());
		}
		if(message == null) {
			message = validateComboSelection(PeakIntegratorTypeSupplier.CATEGORY, comboPeakIntegrator.getText().trim());
		}
		if(message == null) {
			message = validateComboSelection(ChromatogramCalculatorTypeSupplier.CATEGORY, comboChromatogramCalculator.getText().trim());
		}
		if(message == null) {
			message = validateComboSelection(PeakIdentifierTypeSupplier.CATEGORY, comboPeakIdentifier.getText().trim());
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}

	private String validateComboSelection(String category, String processorName) {

		String message = null;
		//
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		IChromatogramProcessEntry chromatogramProcessEntry = wizardElements.getProcessEntry(category);
		String[] processorIds = processTypeSupport.getPluginIds(category);
		String[] processorNames = processTypeSupport.getProcessorNames(category, processorIds);
		/*
		 * Checks
		 */
		if(chromatogramProcessEntry == null) {
			message = "The " + category + " is not available.";
		} else {
			if(processorName.equals("")) {
				chromatogramProcessEntry.setProcessorId("");
			} else {
				/*
				 * Check that the selection exists.
				 */
				boolean isElementInList = false;
				exitloop:
				for(String entry : processorNames) {
					if(processorName.equals(entry)) {
						isElementInList = true;
						break exitloop;
					}
				}
				//
				if(isElementInList) {
					int index = getIndexProcessorId(processorName, processorNames);
					if(index > NO_SELECTION) {
						chromatogramProcessEntry.setProcessorId(processorIds[index]);
						try {
							preferences.put(category, processorIds[index]);
							preferences.flush();
						} catch(BackingStoreException e) {
							logger.warn(e);
						}
					} else {
						chromatogramProcessEntry.setProcessorId("");
					}
				} else {
					chromatogramProcessEntry.setProcessorId("");
					message = "The " + processorName + " is not available.";
				}
			}
		}
		return message;
	}

	private int getIndexProcessorId(String element, String[] elementIds) {

		int result = NO_SELECTION;
		for(int index = 0; index < elementIds.length; index++) {
			if(elementIds[index].equals(element)) {
				result = index;
			}
		}
		return result;
	}

	private int getIndex(String id, String[][] elements) {

		for(int i = 0; i < elements.length; i++) {
			if(elements[i][1].equals(id)) {
				return i;
			}
		}
		return NO_SELECTION;
	}
}
