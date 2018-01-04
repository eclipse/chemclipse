/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.ui.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.widgets.Text;

public class ImportDirectoryWizardPage extends WizardPage {

	private static final String CONVERTER_LABEL_XY = "*.xy";
	private static final String CONVERTER_LABEL_OCB = "*.ocb";
	private static final String CONVERTER_ID_XY = "org.eclipse.chemclipse.csd.converter.supplier.xy";
	private static final String CONVERTER_ID_OCB = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
	//
	private Map<String, String> converterIds;
	//
	private Combo comboConverter;
	private Text textDirectory;

	public ImportDirectoryWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		converterIds = new HashMap<String, String>();
		converterIds.put(CONVERTER_LABEL_XY, CONVERTER_ID_XY);
		converterIds.put(CONVERTER_LABEL_OCB, CONVERTER_ID_OCB);
	}

	public String getSelectedDirectory() {

		return textDirectory.getText().trim();
	}

	public String getSelectedConverterId() {

		return converterIds.get(comboConverter.getText().trim());
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		//
		GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		layoutData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		//
		GridData gridDataCombo = new GridData(GridData.FILL_HORIZONTAL);
		gridDataCombo.grabExcessHorizontalSpace = true;
		gridDataCombo.horizontalSpan = 2;
		comboConverter = new Combo(container, SWT.NONE);
		comboConverter.select(1);
		comboConverter.setItems(new String[]{CONVERTER_LABEL_XY, CONVERTER_LABEL_OCB});
		comboConverter.setLayoutData(gridDataCombo);
		//
		GridData gridDataText = new GridData(GridData.FILL_HORIZONTAL);
		gridDataText.grabExcessHorizontalSpace = true;
		textDirectory = new Text(container, SWT.BORDER);
		textDirectory.setLayoutData(gridDataText);
		//
		Button button = new Button(container, SWT.NONE);
		button.setText("Select the output folder");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
				directoryDialog.setText("Chromatogram Import");
				directoryDialog.setMessage("Select a folder where the imported chromatograms shall be stored.");
				String directory = directoryDialog.open();
				if(directory != null) {
					textDirectory.setText(directory);
				}
			}
		});
		/*
		 * Set the control, otherwise an error will be thrown.
		 */
		setControl(container);
	}
}
