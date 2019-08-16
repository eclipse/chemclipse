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
package org.eclipse.chemclipse.ux.extension.msd.ui.wizards;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.model.ChromatogramOutputEntry;
import org.eclipse.chemclipse.converter.model.IChromatogramOutputEntry;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ChromatogramOutputEntriesWizardPage extends WizardPage {

	private Combo chromatogramConverterComboBox;
	private Text chromatogramOutputFolderTextBox;
	private IChromatogramConverterSupport converterSupport;

	/**
	 * @param pageName
	 */
	protected ChromatogramOutputEntriesWizardPage(String pageName) {
		super(pageName);
		setTitle("Chromatogram Output Formats");
		setDescription("This wizard lets you select several output chromatogram formats.");
		converterSupport = ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport();
	}

	/**
	 * Returns the converter id.
	 * 
	 * @return String
	 * @throws NoConverterAvailableException
	 */
	public IChromatogramOutputEntry getChromatogramOutputEntry() throws NoConverterAvailableException {

		String converterId = "";
		String outputFolder = "";
		/*
		 * Converter
		 */
		int index = chromatogramConverterComboBox.getSelectionIndex();
		if(index >= 0) {
			String description = chromatogramConverterComboBox.getItem(index);
			converterId = converterSupport.getConverterId(description, true);
		} else {
			throw new NoConverterAvailableException("No converter has been selected.");
		}
		/*
		 * Output folder
		 */
		outputFolder = chromatogramOutputFolderTextBox.getText();
		if(outputFolder == null || outputFolder.equals("")) {
			throw new NoConverterAvailableException("The output folder must not be null.");
		}
		/*
		 * Output entry
		 */
		return new ChromatogramOutputEntry(outputFolder, converterId);
	}

	@Override
	public void createControl(Composite parent) {

		Label label;
		GridLayout gridLayout;
		GridData gridData;
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		/*
		 * Select the output file format converter.
		 */
		try {
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			gridData.heightHint = 30;
			/*
			 * Label
			 */
			label = new Label(composite, SWT.NONE);
			label.setText("Select the output file converter:");
			label.setLayoutData(gridData);
			/*
			 * Output converter combo box.
			 */
			String[] filterNames = converterSupport.getExportableFilterNames();
			chromatogramConverterComboBox = new Combo(composite, SWT.NONE);
			chromatogramConverterComboBox.setItems(filterNames);
			chromatogramConverterComboBox.setLayoutData(gridData);
			/*
			 * Label
			 */
			label = new Label(composite, SWT.NONE);
			label.setText("Select the output folder:");
			gridData.verticalIndent = 20;
			label.setLayoutData(gridData);
			/*
			 * Text output folder
			 */
			chromatogramOutputFolderTextBox = new Text(composite, SWT.BORDER);
			chromatogramOutputFolderTextBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			/*
			 * Button select output folder.
			 */
			Button button = new Button(composite, SWT.NONE);
			button.setText("Select folder");
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					DirectoryDialog dialog = new DirectoryDialog(DisplayUtils.getShell());
					dialog.setText("Select chromatogram format output folder:");
					String reportFolder = dialog.open();
					if(reportFolder != null) {
						chromatogramOutputFolderTextBox.setText(reportFolder);
					}
				}
			});
		} catch(NoConverterAvailableException e) {
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			gridData.heightHint = 30;
			label = new Label(composite, SWT.NONE);
			label.setText("Sorry, there are no output converters available.");
			label.setLayoutData(gridData);
		}
		/*
		 * Set the control.
		 */
		setControl(composite);
	}
}
