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
 * Christoph Läubrich - rework categories handling, support different datatype selections
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ProcessingWizardPage extends WizardPage {

	private ComboViewer comboViewerCategory;
	private ComboViewer comboViewerProcessor;
	private ProcessTypeSupport processTypeSupport;
	private Set<DataType> selectedDataTypes = new HashSet<>();
	private List<Button> dataTypeSelections = new ArrayList<>();
	private ProcessEntry processEntry;
	private DataType[] dataTypes;

	protected ProcessingWizardPage(ProcessTypeSupport processTypeSupport, DataType[] dataTypes) {
		super("ProcessingWizardPage");
		this.processTypeSupport = processTypeSupport;
		this.dataTypes = dataTypes;
		setTitle("Process Entry");
		setDescription("Select a chromatogram filter, integrator, identifier ... .");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		if(dataTypes.length > 1) {
			createLabel(composite, "Select the desired data types");
			for(DataType dataType : dataTypes) {
				Button button;
				switch(dataType) {
					case CSD:
						button = createDataTypeCheckbox(composite, DataType.CSD, "CSD (FID, PPD, ...)", "Select the csd processor items", PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_CSD);
						break;
					case MSD:
						button = createDataTypeCheckbox(composite, DataType.MSD, "MSD (Quadrupole, IonTrap, ...)", "Select the msd processor items", PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_MSD);
						break;
					case WSD:
						button = createDataTypeCheckbox(composite, DataType.WSD, "WSD (UV/Vis, DAD, ...)", "Select the wsd processor items", PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_WSD);
						break;
					case NMR:
						button = createDataTypeCheckbox(composite, DataType.NMR, "NMR (FID, Spectrum, ...)", "Select the NMR processor items", PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_NMR);
						break;
					default:
						continue;
				}
				dataTypeSelections.add(button);
				button.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						updateComboCategoryItems();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
			}
		}
		createLabel(composite, "Category");
		comboViewerCategory = createComboCategory(composite);
		createLabel(composite, "Processor");
		comboViewerProcessor = createComboProcessor(composite);
		//
		updateComboCategoryItems();
		setControl(composite);
		validate();
	}

	private static Button createDataTypeCheckbox(Composite parent, DataType dataType, String text, String tooltip, String preferenceKey) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		Button button = new Button(parent, SWT.CHECK);
		button.setData(dataType);
		button.setText(text);
		button.setToolTipText(tooltip);
		button.setSelection(preferenceStore.getBoolean(preferenceKey));
		Color enabledColor = button.getForeground();
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean selection = button.getSelection();
				preferenceStore.setValue(preferenceKey, selection);
				if(selection) {
					button.setForeground(enabledColor);
				} else {
					button.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
				}
			}
		});
		if(!button.getSelection()) {
			button.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		}
		return button;
	}

	public IProcessEntry getProcessEntry() {

		return processEntry;
	}

	private void createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
	}

	private void updateComboCategoryItems() {

		if(dataTypeSelections == null) {
			return;
		}
		selectedDataTypes.clear();
		if(dataTypes.length == 1) {
			selectedDataTypes.add(dataTypes[0]);
		} else {
			for(Button button : dataTypeSelections) {
				button.setEnabled(true);
				if(button.getSelection()) {
					selectedDataTypes.add((DataType)button.getData());
				}
			}
		}
		List<IProcessTypeSupplier> processTypeSuppliers = processTypeSupport.getProcessorTypeSuppliers(selectedDataTypes);
		Map<String, ProcessCategory> categories = new TreeMap<>();
		for(IProcessTypeSupplier supplier : processTypeSuppliers) {
			String category = supplier.getCategory();
			ProcessCategory processCategory = categories.get(category);
			if(processCategory == null) {
				processCategory = new ProcessCategory(category);
				categories.put(category, processCategory);
			}
			processCategory.addSupplier(supplier, selectedDataTypes);
		}
		Object[] objects = categories.values().toArray();
		comboViewerCategory.setInput(objects);
		if(objects.length == 1) {
			comboViewerCategory.setSelection(new StructuredSelection(objects[0]));
		}
		comboViewerCategory.getControl().setEnabled(objects.length > 1);
		updateCategory();
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

				updateCategory();
			}
		});
		return comboViewer;
	}

	private void updateCategory() {

		Object object = comboViewerCategory.getStructuredSelection().getFirstElement();
		if(object instanceof ProcessCategory) {
			ProcessCategory category = (ProcessCategory)object;
			List<IProcessSupplier<?>> suppliers = category.processorSuppliers;
			comboViewerProcessor.setInput(suppliers);
			if(suppliers.size() == 1) {
				comboViewerProcessor.setSelection(new StructuredSelection(suppliers.get(0)));
			}
			comboViewerProcessor.getCombo().setEnabled(suppliers.size() > 1);
		} else {
			comboViewerProcessor.setInput(new Object[0]);
		}
		validate();
	}

	private ComboViewer createComboProcessor(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IProcessSupplier) {
					IProcessSupplier<?> supplier = (IProcessSupplier<?>)element;
					StringBuilder sb = new StringBuilder();
					sb.append(supplier.getName());
					if(selectedDataTypes.size() > 1 && !supplier.getSupportedDataTypes().containsAll(selectedDataTypes)) {
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

				validate();
			}
		});
		return comboViewer;
	}

	private void updateEntry() {

		Object object = comboViewerProcessor.getStructuredSelection().getFirstElement();
		if(object instanceof IProcessSupplier) {
			IProcessSupplier<?> processorSupplier = (IProcessSupplier<?>)object;
			processEntry = new ProcessEntry();
			processEntry.setProcessorId(processorSupplier.getId());
			processEntry.setName(processorSupplier.getName());
			processEntry.setDescription(processorSupplier.getDescription());
			processEntry.getSupportedDataTypes().addAll(processorSupplier.getSupportedDataTypes());
			processEntry.setProcessSettingsClass(processorSupplier.getSettingsClass());
			setMessage(processorSupplier.getDescription());
		} else {
			processEntry = null;
		}
	}

	private void validate() {

		if(!dataTypeSelections.isEmpty()) {
			int selected = 0;
			for(Button button : dataTypeSelections) {
				if(button.getSelection()) {
					selected++;
				}
			}
			if(selected < 1) {
				setPageComplete(false);
				setErrorMessage("Please select at least one data type");
				return;
			}
		}
		if(comboViewerCategory.getSelection().isEmpty()) {
			setPageComplete(false);
			setErrorMessage("Please select a category");
			return;
		}
		if(comboViewerProcessor.getSelection().isEmpty()) {
			setPageComplete(false);
			setErrorMessage("Please select a processor");
			return;
		}
		setPageComplete(true);
		setErrorMessage(null);
		updateEntry();
	}

	private static final class ProcessCategory {

		private static final NameComparator COMPARATOR = new NameComparator();
		private String name;
		List<IProcessSupplier<?>> processorSuppliers = new ArrayList<>();

		public ProcessCategory(String name) {
			this.name = name;
		}

		public void addSupplier(IProcessTypeSupplier supplier, Collection<DataType> dataTypes) {

			for(IProcessSupplier<?> processSupplier : supplier.getProcessorSuppliers()) {
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
