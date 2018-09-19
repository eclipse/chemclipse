/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.wizards;

import org.eclipse.chemclipse.xxd.process.model.ChromatogramProcessEntry;
import org.eclipse.chemclipse.xxd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ChromatogramProcessEntriesWizardPage extends WizardPage {

	private ProcessTypeSupport processTypeSupport;
	private IChromatogramProcessEntry chromatogramProcessEntry;
	private Combo processorComboBox;
	private Combo pluginComboBox;
	private String[] processorCategories;
	private String[] selectedPluginIds;
	private String[] selectedPluginNames;

	/**
	 * @param pageName
	 */
	protected ChromatogramProcessEntriesWizardPage(String pageName, IChromatogramProcessEntry chromatogramProcessEntry) {
		super(pageName);
		setTitle("Chromatogram Process Entries");
		setDescription("This wizard lets you select several chromatogram filter, integrators, identifier ...");
		/*
		 * Initialize the processor names.
		 */
		processTypeSupport = new ProcessTypeSupport();
		processorCategories = processTypeSupport.getProcessorCategories();
		this.chromatogramProcessEntry = chromatogramProcessEntry;
	}

	/**
	 * May return null.
	 * 
	 * @return IChromatogramProcessEntry
	 */
	public IChromatogramProcessEntry getChromatogramProcessEntry() {

		IChromatogramProcessEntry newProcessEntry = null;
		/*
		 * Get the TYPE ... FILTER, IDENTIFIER ...
		 */
		int typeSelection = processorComboBox.getSelectionIndex();
		if(typeSelection >= 0) {
			/*
			 * Get the selected plugin.
			 */
			int pluginSelection = pluginComboBox.getSelectionIndex();
			if(pluginSelection >= 0) {
				/*
				 * Create a new chromatogram process entry instance.
				 */
				String processCategory = processorCategories[processorComboBox.getSelectionIndex()];
				String processorId = selectedPluginIds[pluginSelection];
				/*
				 * Only create the process entry if the plugin exists.
				 */
				if(!processorId.equals(IProcessTypeSupplier.NOT_AVAILABLE)) {
					if(chromatogramProcessEntry == null) {
						newProcessEntry = new ChromatogramProcessEntry(processCategory, processorId);
					} else {
						chromatogramProcessEntry.setProcessorId(processorId);
						chromatogramProcessEntry.setProcessCategory(processCategory);
						newProcessEntry = chromatogramProcessEntry;
					}
				}
			}
		}
		return newProcessEntry;
	}

	@Override
	public void createControl(Composite parent) {

		Label label;
		GridLayout gridLayout;
		GridData gridData;
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);
		/*
		 * Select the process entry.
		 */
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 30;
		/*
		 * Label
		 */
		label = new Label(composite, SWT.NONE);
		label.setText("Select the processing category:");
		label.setLayoutData(gridData);
		/*
		 * Output converter combo box.
		 */
		processorComboBox = new Combo(composite, SWT.NONE);
		processorComboBox.setItems(processorCategories);
		processorComboBox.setLayoutData(gridData);
		processorComboBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				String processorCategory = processorCategories[processorComboBox.getSelectionIndex()];
				selectedPluginIds = processTypeSupport.getPluginIds(processorCategory);
				selectedPluginNames = processTypeSupport.getProcessorNames(processorCategory, selectedPluginIds);
				pluginComboBox.setItems(selectedPluginNames);
			}
		});
		if(chromatogramProcessEntry != null) {
			String processCategory = chromatogramProcessEntry.getProcessCategory();
			int index = getProcessorTypeIndex(processCategory);
			processorComboBox.select(index);
		}
		/*
		 * Label
		 */
		label = new Label(composite, SWT.NONE);
		label.setText("Select plug-in:");
		label.setLayoutData(gridData);
		/*
		 * plug-in combo box.
		 */
		pluginComboBox = new Combo(composite, SWT.NONE);
		pluginComboBox.setLayoutData(gridData);
		if(chromatogramProcessEntry != null) {
			String processCategory = chromatogramProcessEntry.getProcessCategory();
			selectedPluginIds = processTypeSupport.getPluginIds(processCategory);
			selectedPluginNames = processTypeSupport.getProcessorNames(processCategory, selectedPluginIds);
			pluginComboBox.setItems(selectedPluginNames);
			// set the selected item
			String processorId = chromatogramProcessEntry.getProcessorId();
			int index = getProcessorIdIndex(processorId);
			pluginComboBox.select(index);
		}
		/*
		 * Set the control.
		 */
		setControl(composite);
	}

	/**
	 * Returns the processor name index.
	 * 
	 * @return int
	 */
	private int getProcessorTypeIndex(String processCategory) {

		/*
		 * -1 means no selection.
		 */
		int result = -1;
		for(int index = 0; index < processorCategories.length; index++) {
			if(processorCategories[index].equals(processCategory)) {
				result = index;
			}
		}
		return result;
	}

	/**
	 * Returns the processor name index.
	 * 
	 * @return int
	 */
	private int getProcessorIdIndex(String processorId) {

		/*
		 * -1 means no selection.
		 */
		int result = -1;
		for(int index = 0; index < selectedPluginIds.length; index++) {
			if(selectedPluginIds[index].equals(processorId)) {
				result = index;
			}
		}
		return result;
	}
}
