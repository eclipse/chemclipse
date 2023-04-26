/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.xxd.process.comparators.NameComparator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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

	private final Map<IProcessSupplierContext, String> processSupplierContextMap;
	private final Set<DataCategory> selectedDataTypes = new HashSet<>();
	private final List<Button> dataCategorySelections = new ArrayList<>();
	private final List<DataCategory> dataCategories = new ArrayList<>();
	//
	private ComboViewer comboViewerCategory;
	private ComboViewer comboViewerProcessor;
	private ProcessEntry processEntry;
	private IProcessSupplierContext processContext;
	//
	private static IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	protected ProcessingWizardPage(Map<IProcessSupplierContext, String> contexts, DataCategory[] dataCategories) {

		super("ProcessingWizardPage");
		this.processSupplierContextMap = contexts;
		/*
		 * Collect and sort the data categories.
		 */
		if(dataCategories != null) {
			for(DataCategory dataCategory : dataCategories) {
				this.dataCategories.add(dataCategory);
			}
			Collections.sort(this.dataCategories, (c1, c2) -> c1.name().compareTo(c2.name()));
		}
		processContext = contexts.entrySet().iterator().next().getKey();
		setTitle(ExtensionMessages.processEntry);
		setDescription(ExtensionMessages.selectProcessEntry);
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		if(dataCategories.size() > 1) {
			createLabel(composite, ExtensionMessages.selectDataCategories);
			for(DataCategory dataCategory : dataCategories) {
				/*
				 * Set the default visibility.
				 */
				setDataTypeSelectionDefault(dataCategory);
				Button button = createDataTypeCheckbox(composite, dataCategory);
				dataCategorySelections.add(button);
				button.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						updateComboDataCategoryItems();
					}
				});
			}
		}
		//
		if(processSupplierContextMap.size() > 1) {
			createLabel(composite, ExtensionMessages.context);
			createComboViewerContext(composite);
		}
		//
		createLabel(composite, ExtensionMessages.category);
		comboViewerCategory = createComboViewerCategory(composite);
		createLabel(composite, ExtensionMessages.processor);
		comboViewerProcessor = createComboViewerProcessor(composite);
		//
		updateComboDataCategoryItems();
		setControl(composite);
		validate();
	}

	private void createComboViewerContext(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		comboViewer.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {

				return processSupplierContextMap.get(element);
			}
		});
		//
		comboViewer.setInput(processSupplierContextMap.keySet());
		comboViewer.setSelection(new StructuredSelection(getProcessSupplierContext()));
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				processContext = (IProcessSupplierContext)comboViewer.getStructuredSelection().getFirstElement();
				updateComboDataCategoryItems();
			}
		});
	}

	private static Button createDataTypeCheckbox(Composite parent, DataCategory dataCategory) {

		Button button = new Button(parent, SWT.CHECK);
		button.setData(dataCategory);
		button.setText(dataCategory.label());
		button.setToolTipText(MessageFormat.format(ExtensionMessages.selectCategoryProcessorItems, dataCategory.name()));
		button.setSelection(getDataTypeSelection(dataCategory));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean selection = button.getSelection();
				setDataTypeSelection(dataCategory, selection);
			}
		});
		//
		return button;
	}

	private static void setDataTypeSelectionDefault(DataCategory dataCategory) {

		preferenceStore.setDefault(PreferenceConstants.P_PROCESSOR_SELECTION_DATA_CATEGORY + dataCategory.name(), PreferenceConstants.DEF_PROCESSOR_SELECTION_DATA_CATEGORY);
	}

	private static boolean getDataTypeSelection(DataCategory dataCategory) {

		return preferenceStore.getBoolean(PreferenceConstants.P_PROCESSOR_SELECTION_DATA_CATEGORY + dataCategory.name());
	}

	private static void setDataTypeSelection(DataCategory dataCategory, boolean selection) {

		preferenceStore.setValue(PreferenceConstants.P_PROCESSOR_SELECTION_DATA_CATEGORY + dataCategory.name(), selection);
	}

	public IProcessEntry getProcessEntry() {

		return processEntry;
	}

	private void createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
	}

	private void updateComboDataCategoryItems() {

		selectedDataTypes.clear();
		if(dataCategories.size() == 1) {
			selectedDataTypes.add(dataCategories.get(0));
		} else {
			for(Button button : dataCategorySelections) {
				button.setEnabled(true);
				if(button.getSelection()) {
					selectedDataTypes.add(((DataCategory)button.getData()));
				}
			}
		}
		//
		Set<IProcessSupplier<?>> processTypeSuppliers = new LinkedHashSet<>();
		getProcessSupplierContext().visitSupplier(new Consumer<IProcessSupplier<?>>() {

			@Override
			public void accept(IProcessSupplier<?> supplier) {

				for(DataCategory category : supplier.getSupportedDataTypes()) {
					if(selectedDataTypes.contains(category)) {
						processTypeSuppliers.add(supplier);
						return;
					}
				}
			}
		});
		//
		Map<String, ProcessCategory> categories = new TreeMap<>();
		for(IProcessSupplier<?> supplier : processTypeSuppliers) {
			String category = supplier.getCategory();
			ProcessCategory processCategory = categories.get(category);
			if(processCategory == null) {
				processCategory = new ProcessCategory(category);
				categories.put(category, processCategory);
			}
			processCategory.addSupplier(supplier, selectedDataTypes);
		}
		//
		Object[] objects = categories.values().toArray();
		comboViewerCategory.setInput(objects);
		if(objects.length == 1) {
			comboViewerCategory.setSelection(new StructuredSelection(objects[0]));
		}
		//
		comboViewerCategory.getControl().setEnabled(objects.length > 1);
		updateCategory();
	}

	private ComboViewer createComboViewerCategory(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ProcessCategory processCategory) {
					return processCategory.name;
				}
				return "-";
			}
		});
		//
		combo.setToolTipText(ExtensionMessages.selectCategory);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateCategory();
			}
		});
		//
		return comboViewer;
	}

	private void updateCategory() {

		Object object = comboViewerCategory.getStructuredSelection().getFirstElement();
		if(object instanceof ProcessCategory category) {
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

	private ComboViewer createComboViewerProcessor(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
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
		//
		combo.setToolTipText(ExtensionMessages.selectProcessor);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				validate();
			}
		});
		//
		return comboViewer;
	}

	private void updateEntry() {

		Object object = comboViewerProcessor.getStructuredSelection().getFirstElement();
		if(object instanceof IProcessSupplier) {
			IProcessSupplier<?> processorSupplier = (IProcessSupplier<?>)object;
			processEntry = new ProcessEntry((ProcessEntryContainer)null);
			processEntry.setProcessorId(processorSupplier.getId());
			processEntry.setName(processorSupplier.getName());
			processEntry.setDescription(processorSupplier.getDescription());
			processorSupplier.getSupportedDataTypes().forEach(processEntry::addDataCategory);
			setMessage(processorSupplier.getDescription());
		} else {
			processEntry = null;
		}
	}

	private void validate() {

		if(!dataCategorySelections.isEmpty()) {
			int selected = 0;
			for(Button button : dataCategorySelections) {
				if(button.getSelection()) {
					selected++;
				}
			}
			if(selected < 1) {
				setPageComplete(false);
				setErrorMessage(ExtensionMessages.selectOneDataType);
				return;
			}
		}
		//
		if(comboViewerCategory.getSelection().isEmpty()) {
			setPageComplete(false);
			setErrorMessage(ExtensionMessages.selectCategory);
			return;
		}
		//
		if(comboViewerProcessor.getSelection().isEmpty()) {
			setPageComplete(false);
			setErrorMessage(ExtensionMessages.selectProcessor);
			return;
		}
		//
		setPageComplete(true);
		setErrorMessage(null);
		updateEntry();
	}

	private static final class ProcessCategory {

		private static final NameComparator COMPARATOR = new NameComparator();
		private final String name;
		private List<IProcessSupplier<?>> processorSuppliers = new ArrayList<>();

		public ProcessCategory(String name) {

			this.name = name;
		}

		public void addSupplier(IProcessSupplier<?> supplier, Set<DataCategory> selectedDataTypes) {

			for(DataCategory dataType : selectedDataTypes) {
				if(supplier.getSupportedDataTypes().contains(dataType)) {
					processorSuppliers.add(supplier);
					Collections.sort(processorSuppliers, COMPARATOR);
					return;
				}
			}
		}
	}

	public IProcessSupplierContext getProcessSupplierContext() {

		return processContext;
	}
}
