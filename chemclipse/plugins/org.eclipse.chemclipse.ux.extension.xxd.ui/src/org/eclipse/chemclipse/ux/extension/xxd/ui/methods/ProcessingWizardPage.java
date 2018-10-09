/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ProcessingWizardPage extends WizardPage {

	private static final Logger logger = Logger.getLogger(ProcessingWizardPage.class);
	//
	private ComboViewer comboViewerCategory;
	private ComboViewer comboViewerProcessor;
	private ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();
	private IProcessTypeSupplier processTypeSupplier = null;

	protected ProcessingWizardPage() {
		super("ProcessingWizardPage");
		setTitle("Process Entry");
		setDescription("Select a chromatogram filter, integrator, identifier ... .");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		createLabel(composite, "Process Category");
		comboViewerCategory = createComboCategory(composite);
		createLabel(composite, "Processor");
		comboViewerProcessor = createComboProcessor(composite);
		//
		comboViewerCategory.setInput(processTypeSupport.getProcessorTypeSuppliers());
		//
		setControl(composite);
		validate();
	}

	public IProcessMethod getProcessMethod() {

		IProcessMethod processMethod = null;
		if(processTypeSupplier != null) {
			Object object = comboViewerProcessor.getStructuredSelection().getFirstElement();
			if(object instanceof String) {
				try {
					/*
					 * Get the process method.
					 */
					int index = comboViewerProcessor.getCombo().getSelectionIndex();
					String processorId = processTypeSupplier.getPluginIds().get(index);
					//
					processMethod = new ProcessMethod();
					processMethod.setProcessorId(processorId);
					processMethod.setName(object.toString());
					processMethod.setDescription(processTypeSupplier.getProcessorDescription(processorId));
					processMethod.getSupportedDataTypes().addAll(processTypeSupplier.getSupportedDataTypes());
					processMethod.setProcessSettingsClass(processTypeSupplier.getProcessSettingsClass(processorId));
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		}
		return processMethod;
	}

	private void createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
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
						List<String> names = new ArrayList<>();
						for(String processorId : processTypeSupplier.getPluginIds()) {
							names.add(processTypeSupplier.getProcessorName(processorId));
						}
						comboViewerProcessor.setInput(names);
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

				if(element instanceof String) {
					String value = (String)element;
					return value;
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
