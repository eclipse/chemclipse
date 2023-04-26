/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakOutputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.PeakOutputEntry;
import org.eclipse.chemclipse.converter.core.IConverterSupport;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.msd.converter.peak.IPeakConverterSupport;
import org.eclipse.chemclipse.msd.converter.peak.PeakConverterMSD;
import org.eclipse.chemclipse.support.ui.swt.EnhancedCombo;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PeakOutputFilesWizardPage extends WizardPage {

	private Combo peakConverterComboBox;
	private Text peakOutputFolderTextBox;
	private IPeakConverterSupport converterSupport;

	/**
	 * @param pageName
	 */
	protected PeakOutputFilesWizardPage(String pageName) {

		super(pageName);
		setTitle("Peak Output Formats");
		setDescription("This wizard lets you select several output peak formats.");
	}

	/**
	 * Returns the converter id.
	 * 
	 * @return String
	 * @throws NoConverterAvailableException
	 */
	public IPeakOutputEntry getPeakOutputEntry() throws NoConverterAvailableException {

		String converterId = "";
		String outputFolder = "";
		/*
		 * Converter
		 */
		int index = peakConverterComboBox.getSelectionIndex();
		if(index >= 0) {
			String description = peakConverterComboBox.getItem(index);
			converterId = converterSupport.getConverterId(description, true);
		} else {
			throw new NoConverterAvailableException("No converter has been selected.");
		}
		/*
		 * Output folder
		 */
		outputFolder = peakOutputFolderTextBox.getText();
		if(outputFolder == null || outputFolder.equals("")) {
			throw new NoConverterAvailableException("The output folder must not be null.");
		}
		/*
		 * Output entry
		 */
		return new PeakOutputEntry(outputFolder, converterId);
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
		converterSupport = PeakConverterMSD.getPeakConverterSupport();
		String[] filterNames = converterSupport.getFilterNames(IConverterSupport.EXPORT_SUPPLIER);
		//
		if(filterNames.length > 0) {
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
			peakConverterComboBox = EnhancedCombo.create(composite, SWT.NONE);
			peakConverterComboBox.setItems(filterNames);
			peakConverterComboBox.setLayoutData(gridData);
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
			peakOutputFolderTextBox = new Text(composite, SWT.BORDER);
			peakOutputFolderTextBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			/*
			 * Button select output folder.
			 */
			Button button = new Button(composite, SWT.NONE);
			button.setText("Select folder");
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					Shell shell = Display.getCurrent().getActiveShell();
					DirectoryDialog dialog = new DirectoryDialog(shell);
					dialog.setText("Select chromatogram format output folder:");
					String reportFolder = dialog.open();
					if(reportFolder != null) {
						peakOutputFolderTextBox.setText(reportFolder);
					}
				}
			});
		} else {
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
