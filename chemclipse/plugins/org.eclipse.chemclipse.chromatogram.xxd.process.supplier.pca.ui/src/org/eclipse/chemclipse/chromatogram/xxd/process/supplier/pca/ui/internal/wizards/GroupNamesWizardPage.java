/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - improvements UI
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.InputFilesTable;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GroupNamesWizardPage extends WizardPage {

	private final int CONTAINS_STRING = 0;
	private final int STARTS_WITH_STRING = 1;
	private final int ENDS_WITH_STRING = 2;
	private final int REGEXP = 3;
	//
	private int selectNames = CONTAINS_STRING;
	//
	private InputFilesTable inputFilesTable;
	private Text textGroupName;
	private Button caseSensitive;
	private Text textSelectNames;

	public GroupNamesWizardPage() {
		super("Sample Grouping");
		setTitle("Set Group Name");
		setDescription("Set mass group name according to name");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		/*
		 * Select the process entry.
		 */
		createLabel(composite, "Group Name");
		textGroupName = createText(composite);
		createLabel(composite, "String or regular expression to match the group name(s)");
		textSelectNames = createText(composite);
		createOptionGroup(composite);
		createToolbar(composite);
		inputFilesTable = createInputFilesTable(composite);
		//
		setControl(composite);
	}

	@Override
	public void setVisible(boolean visible) {

		if(visible) {
			IInputWizard inputWizard = (IInputWizard)getWizard();
			inputFilesTable.setDataInputEntries(inputWizard.getDataInputEntries());
			inputFilesTable.update();
		}
		super.setVisible(visible);
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		label.setText(text);
		return label;
	}

	private Text createText(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	private void createOptionGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setLayout(new GridLayout(1, false));
		group.setText("Settings");
		//
		caseSensitive = createCheckBox(group, "Case Sensitive");
		createRadioButton(group, "Contains String", true, CONTAINS_STRING);
		createRadioButton(group, "Starts with String", false, STARTS_WITH_STRING);
		createRadioButton(group, "Ends with String", false, ENDS_WITH_STRING);
		createRadioButton(group, "Regular Expression", false, REGEXP);
	}

	private InputFilesTable createInputFilesTable(Composite parent) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		gridData.heightHint = 300;
		inputFilesTable = new InputFilesTable(parent, gridData);
		IInputWizard inputWizard = (IInputWizard)getWizard();
		inputFilesTable.setDataInputEntries(inputWizard.getDataInputEntries());
		return inputFilesTable;
	}

	private void createToolbar(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(2, false));
		//
		createButtonProcess(composite);
		createButtonReset(composite);
	}

	private Button createButtonProcess(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Set the group name(s)");
		button.setText("Set the group name(s)");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateGroupNames();
			}
		});
		//
		return button;
	}

	private Button createCheckBox(Composite parent, String text) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText(text);
		return button;
	}

	private Button createRadioButton(Composite parent, String text, boolean selected, int option) {

		Button button = new Button(parent, SWT.RADIO);
		button.setText(text);
		button.setSelection(selected);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				selectNames = option;
			}
		});
		//
		return button;
	}

	private Button createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the group name(s)");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				resetGroupNames();
			}
		});
		//
		return button;
	}

	private void updateGroupNames() {

		String s = textSelectNames.getText().trim();
		if(s.isEmpty()) {
			return;
		}
		//
		List<IDataInputEntry> filterInput = new ArrayList<>();
		IInputWizard inputWizard = (IInputWizard)getWizard();
		//
		switch(selectNames) {
			case CONTAINS_STRING:
				if(!caseSensitive.getSelection()) {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().toLowerCase().contains(s.toLowerCase())).forEach(filterInput::add);
				} else {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().contains(s)).forEach(filterInput::add);
				}
				break;
			case STARTS_WITH_STRING:
				if(!caseSensitive.getSelection()) {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().toLowerCase().startsWith(s.toLowerCase())).forEach(filterInput::add);
				} else {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().startsWith(s)).forEach(filterInput::add);
				}
				break;
			case ENDS_WITH_STRING:
				if(!caseSensitive.getSelection()) {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().toLowerCase().endsWith(s.toLowerCase())).forEach(filterInput::add);
				} else {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().endsWith(s)).forEach(filterInput::add);
				}
				break;
			case REGEXP:
				Pattern p = Pattern.compile(s);
				inputWizard.getDataInputEntries().stream().filter(i -> p.matcher(i.getName()).find()).forEach(filterInput::add);
				break;
			default:
				break;
		}
		//
		String groupName = textGroupName.getText().trim();
		final String setGroupName = groupName.isEmpty() ? null : groupName;
		filterInput.forEach(i -> i.setGroupName(setGroupName));
		update();
	}

	private void resetGroupNames() {

		IInputWizard inputWizard = (IInputWizard)getWizard();
		for(IDataInputEntry dataInputEntry : inputWizard.getDataInputEntries()) {
			dataInputEntry.setGroupName("");
		}
		update();
	}

	private void update() {

		inputFilesTable.update();
	}
}
