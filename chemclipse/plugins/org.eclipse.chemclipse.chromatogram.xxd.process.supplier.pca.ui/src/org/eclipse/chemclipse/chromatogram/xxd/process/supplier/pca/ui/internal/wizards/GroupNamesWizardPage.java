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

	private Button caseSensitive;
	private final int CONTAINS_STRING = 0;
	private final int ENDS_WITH_STRING = 2;
	private InputFilesTable inputFilesTable;
	private final int REGEXP = 3;
	private int selectNames;
	private final int STARTS_WITH_STRING = 1;
	private Text textGroupName;
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
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		composite.setLayoutData(gridData);
		/*
		 * Select the process entry.
		 */
		createLabel(composite, "Group Name");
		textGroupName = createText(composite);
		createLabel(composite, "String or Regexp, which match with names");
		textSelectNames = createText(composite);
		createOptionGroup(composite);
		inputFilesTable = createInputFilesTable(composite);
		createToolbarBottom(composite);
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
		caseSensitive = new Button(group, SWT.CHECK);
		caseSensitive.setText("Case Sensitive");
		Button button = new Button(group, SWT.RADIO);
		button.setText("Contains string");
		button.addListener(SWT.Selection, e -> selectNames = CONTAINS_STRING);
		selectNames = CONTAINS_STRING;
		button.setSelection(true);
		button = new Button(group, SWT.RADIO);
		button.setText("Starts with string");
		button.addListener(SWT.Selection, e -> selectNames = STARTS_WITH_STRING);
		button = new Button(group, SWT.RADIO);
		button.setText("Ends with string");
		button.addListener(SWT.Selection, e -> selectNames = ENDS_WITH_STRING);
		button = new Button(group, SWT.RADIO);
		button.setText("Regexp");
		button.addListener(SWT.Selection, e -> selectNames = REGEXP);
	}

	private InputFilesTable createInputFilesTable(Composite parent) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 500;
		inputFilesTable = new InputFilesTable(parent, gridData);
		IInputWizard inputWizard = (IInputWizard)getWizard();
		inputFilesTable.setDataInputEntries(inputWizard.getDataInputEntries());
		return inputFilesTable;
	}

	private void createToolbarBottom(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		createButtonProcess(composite);
		createButtonReset(composite);
	}

	private Button createButtonProcess(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Set the group name(s)");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateGroupNames();
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
		inputFilesTable.update();
	}

	private void resetGroupNames() {

		IInputWizard inputWizard = (IInputWizard)getWizard();
		for(IDataInputEntry dataInputEntry : inputWizard.getDataInputEntries()) {
			dataInputEntry.setGroupName("");
		}
		inputFilesTable.update();
	}
}
