/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.xxd.process.comparators.CategoryComparator;
import org.eclipse.chemclipse.xxd.process.comparators.NameComparator;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ProcessingWizardPage extends WizardPage {

	private static final Logger logger = Logger.getLogger(ProcessingWizardPage.class);
	//
	private Button checkboxCSD;
	private Button checkboxMSD;
	private Button checkboxWSD;
	private ComboViewer comboViewerCategory;
	private ComboViewer comboViewerProcessor;
	private ProcessTypeSupport processTypeSupport;
	private IProcessTypeSupplier processTypeSupplier = null;
	//
	private CategoryComparator categoryComparator = new CategoryComparator();
	private NameComparator nameComparator = new NameComparator();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	protected ProcessingWizardPage(ProcessTypeSupport processTypeSupport) {
		super("ProcessingWizardPage");
		this.processTypeSupport = processTypeSupport;
		setTitle("Process Entry");
		setDescription("Select a chromatogram filter, integrator, identifier ... .");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		createLabel(composite, "Data Types");
		createDataTypeSelection(composite);
		createLabel(composite, "Process Category");
		comboViewerCategory = createComboCategory(composite);
		createLabel(composite, "Processor");
		comboViewerProcessor = createComboProcessor(composite);
		//
		updateComboCategoryItems();
		setControl(composite);
		validate();
	}

	public IProcessEntry getProcessEntry() {

		IProcessEntry processEntry = null;
		if(processTypeSupplier != null) {
			Object object = comboViewerProcessor.getStructuredSelection().getFirstElement();
			if(object instanceof ProcessorSupplier) {
				try {
					ProcessorSupplier processorSupplier = (ProcessorSupplier)object;
					processEntry = new ProcessEntry();
					processEntry.setProcessorId(processorSupplier.getId());
					processEntry.setName(processorSupplier.getName());
					processEntry.setDescription(processorSupplier.getDescription());
					processEntry.getSupportedDataTypes().addAll(processTypeSupplier.getSupportedDataTypes());
					processEntry.setProcessSettingsClass(processorSupplier.getSettingsClass());
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		}
		return processEntry;
	}

	private void createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
	}

	private void createDataTypeSelection(Composite parent) {

		checkboxCSD = createDataTypeCheckbox(parent, "CSD (FID, PPD, ...)", "Select the csd processor items", preferenceStore.getBoolean(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_CSD));
		checkboxMSD = createDataTypeCheckbox(parent, "MSD (Quadrupole, IonTrap, ...)", "Select the msd processor items", preferenceStore.getBoolean(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_MSD));
		checkboxWSD = createDataTypeCheckbox(parent, "WSD (UV/Vis, DAD, ...)", "Select the wsd processor items", preferenceStore.getBoolean(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_WSD));
	}

	private Button createDataTypeCheckbox(Composite parent, String text, String tooltip, boolean selection) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText(text);
		button.setToolTipText(tooltip);
		button.setSelection(selection);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateComboCategoryItems();
			}
		});
		//
		return button;
	}

	private void updateComboCategoryItems() {

		List<DataType> dataTypes = new ArrayList<>();
		addSelectedTypeAndPersist(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_CSD, checkboxCSD, dataTypes, DataType.CSD);
		addSelectedTypeAndPersist(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_MSD, checkboxMSD, dataTypes, DataType.MSD);
		addSelectedTypeAndPersist(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_WSD, checkboxWSD, dataTypes, DataType.WSD);
		//
		List<IProcessTypeSupplier> processTypeSuppliers = processTypeSupport.getProcessorTypeSuppliers(dataTypes);
		Collections.sort(processTypeSuppliers, categoryComparator);
		comboViewerCategory.setInput(processTypeSuppliers);
	}

	private void addSelectedTypeAndPersist(String name, Button checkbox, List<DataType> dataTypes, DataType dataType) {

		preferenceStore.setValue(name, checkbox.getSelection());
		if(checkbox.getSelection()) {
			dataTypes.add(dataType);
		}
	}

	private ComboViewer createComboCategory(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IProcessTypeSupplier) {
					IProcessTypeSupplier processTypeSupplier = (IProcessTypeSupplier)element;
					return processTypeSupplier.getCategory();
				}
				return null;
			}
		});
		combo.setToolTipText("Select a process category.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IProcessTypeSupplier) {
					try {
						processTypeSupplier = (IProcessTypeSupplier)object;
						List<ProcessorSupplier> processSuppliers = new ArrayList<>(processTypeSupplier.getProcessorSuppliers());
						Collections.sort(processSuppliers, nameComparator);
						comboViewerProcessor.setInput(processSuppliers);
					} catch(Exception e1) {
						logger.warn(e1);
					}
				}
			}
		});
		//
		return comboViewer;
	}

	private ComboViewer createComboProcessor(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ProcessorSupplier) {
					ProcessorSupplier processorSupplier = (ProcessorSupplier)element;
					return processorSupplier.getName();
				}
				return null;
			}
		});
		combo.setToolTipText("Select a processor.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
		//
		return comboViewer;
	}

	private void validate() {

		String message = null;
		setErrorMessage(message);
		setPageComplete((message == null));
		if(message == null) {
			//
		}
	}
}
