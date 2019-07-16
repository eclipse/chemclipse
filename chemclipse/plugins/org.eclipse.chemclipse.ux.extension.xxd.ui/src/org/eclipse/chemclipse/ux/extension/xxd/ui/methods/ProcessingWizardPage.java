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
 * Christoph Läubrich - rework categories handling
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.xxd.process.comparators.NameComparator;
import org.eclipse.chemclipse.xxd.process.support.IProcessSupplier;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
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

	private Button checkboxCSD;
	private Button checkboxMSD;
	private Button checkboxWSD;
	private ComboViewer comboViewerCategory;
	private ComboViewer comboViewerProcessor;
	private ProcessTypeSupport processTypeSupport;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private Set<DataType> dataTypes = new HashSet<>();

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

		Object object = comboViewerProcessor.getStructuredSelection().getFirstElement();
		if(object instanceof IProcessSupplier) {
			IProcessSupplier processorSupplier = (IProcessSupplier)object;
			ProcessEntry processEntry = new ProcessEntry();
			processEntry.setProcessorId(processorSupplier.getId());
			processEntry.setName(processorSupplier.getName());
			processEntry.setDescription(processorSupplier.getDescription());
			processEntry.getSupportedDataTypes().addAll(processorSupplier.getSupportedDataTypes());
			processEntry.setProcessSettingsClass(processorSupplier.getSettingsClass());
			return processEntry;
		}
		return null;
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

		dataTypes.clear();
		addSelectedTypeAndPersist(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_CSD, checkboxCSD, dataTypes, DataType.CSD);
		addSelectedTypeAndPersist(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_MSD, checkboxMSD, dataTypes, DataType.MSD);
		addSelectedTypeAndPersist(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_WSD, checkboxWSD, dataTypes, DataType.WSD);
		List<IProcessTypeSupplier<?>> processTypeSuppliers = processTypeSupport.getProcessorTypeSuppliers(dataTypes);
		Map<String, ProcessCategory> categories = new TreeMap<>();
		for(IProcessTypeSupplier<?> supplier : processTypeSuppliers) {
			String category = supplier.getCategory();
			ProcessCategory processCategory = categories.get(category);
			if(processCategory == null) {
				processCategory = new ProcessCategory(category);
				categories.put(category, processCategory);
			}
			processCategory.addSupplier(supplier, dataTypes);
		}
		comboViewerCategory.setInput(categories.values().toArray());
	}

	private void addSelectedTypeAndPersist(String name, Button checkbox, Collection<DataType> dataTypes, DataType dataType) {

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

				if(element instanceof ProcessCategory) {
					return ((ProcessCategory)element).name;
				}
				return "-";
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
				if(object instanceof ProcessCategory) {
					ProcessCategory category = (ProcessCategory)object;
					comboViewerProcessor.setInput(category.processorSuppliers);
				} else {
					comboViewerProcessor.setInput(new Object[0]);
				}
			}
		});
		return comboViewer;
	}

	private ComboViewer createComboProcessor(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IProcessSupplier) {
					IProcessSupplier supplier = (IProcessSupplier)element;
					StringBuilder sb = new StringBuilder();
					sb.append(supplier.getName());
					if(dataTypes.size() > 1 && !supplier.getSupportedDataTypes().containsAll(dataTypes)) {
						sb.append(" ");
						sb.append(Arrays.toString(supplier.getSupportedDataTypes().toArray()));
					}
					return sb.toString();
				}
				return "-";
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

	private static final class ProcessCategory {

		private static final NameComparator COMPARATOR = new NameComparator();
		private String name;
		List<IProcessSupplier> processorSuppliers = new ArrayList<>();

		public ProcessCategory(String name) {
			this.name = name;
		}

		public void addSupplier(IProcessTypeSupplier<?> supplier, Collection<DataType> dataTypes) {

			List<IProcessSupplier> suppliers = supplier.getProcessorSuppliers();
			for(IProcessSupplier processSupplier : suppliers) {
				for(DataType dataType : dataTypes) {
					if(processSupplier.getSupportedDataTypes().contains(dataType)) {
						processorSuppliers.add(processSupplier);
						break;
					}
				}
			}
			Collections.sort(processorSuppliers, COMPARATOR);
		}
	}
}
