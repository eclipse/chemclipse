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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.fieldeditors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ColorCodeDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.ColorCode;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.ColorCodes;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ColorCodeTableUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ColorCodesFieldEditor extends FieldEditor {

	private static final int NUMBER_COLUMNS = 2;
	//
	private Composite composite;
	private ColorCodes colorCodes = new ColorCodes();
	private ColorCodeTableUI colorCodeTableUI;

	public ColorCodesFieldEditor(String name, String labelText, Composite parent) {

		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		//
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(NUMBER_COLUMNS, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		//
		createLabelSection(composite);
		createTableSection(composite);
		createButtonGroup(composite);
	}

	private void createLabelSection(Composite parent) {

		Label label = new Label(parent, SWT.LEFT);
		label.setText("Add/Remove Color Codes");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = NUMBER_COLUMNS;
		label.setLayoutData(gridData);
	}

	private void createTableSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);
		//
		colorCodeTableUI = new ColorCodeTableUI(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		setTableViewerInput();
	}

	private void createButtonGroup(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(gridData);
		//
		setButtonLayoutData(createButtonAdd(composite));
		setButtonLayoutData(createButtonEdit(composite));
		setButtonLayoutData(createButtonRemove(composite));
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add");
		button.setToolTipText("Add a color code.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				ColorCodeDialog dialog = new ColorCodeDialog(e.display.getActiveShell());
				if(IDialogConstants.OK_ID == dialog.open()) {
					ColorCode colorCode = dialog.getColorCode();
					if(colorCode != null) {
						colorCodes.add(colorCode);
						setTableViewerInput();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonEdit(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Edit");
		button.setToolTipText("Edit the selected color code.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = (IStructuredSelection)colorCodeTableUI.getSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof ColorCode colorCode) {
					ColorCodeDialog dialog = new ColorCodeDialog(e.display.getActiveShell(), colorCode);
					if(IDialogConstants.OK_ID == dialog.open()) {
						colorCode = dialog.getColorCode();
						if(colorCode != null) {
							colorCodes.add(colorCode);
							setTableViewerInput();
						}
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRemove(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Remove");
		button.setToolTipText("Remove the selected color codes.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), "Color Codes", "Do you want to delete the selected color codes?")) {
					List<String> removeKeys = new ArrayList<>();
					IStructuredSelection structuredSelection = (IStructuredSelection)colorCodeTableUI.getSelection();
					for(Object object : structuredSelection.toArray()) {
						if(object instanceof ColorCode colorCode) {
							removeKeys.add(colorCode.getName());
						}
					}
					/*
					 * Remove the objects.
					 */
					for(String key : removeKeys) {
						colorCodes.remove(key);
					}
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private void setTableViewerInput() {

		colorCodeTableUI.setInput(colorCodes.values());
	}

	@Override
	protected void doLoad() {

		String codes = getPreferenceStore().getString(getPreferenceName());
		colorCodes.load(codes);
		setTableViewerInput();
	}

	@Override
	protected void doLoadDefault() {

		String codes = getPreferenceStore().getDefaultString(getPreferenceName());
		colorCodes.loadDefault(codes);
		setTableViewerInput();
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), colorCodes.save());
	}

	@Override
	public int getNumberOfControls() {

		return 1;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		GridData gridData = (GridData)composite.getLayoutData();
		gridData.horizontalSpan = numColumns - 1;
		gridData.grabExcessHorizontalSpace = gridData.horizontalSpan == 1;
	}
}