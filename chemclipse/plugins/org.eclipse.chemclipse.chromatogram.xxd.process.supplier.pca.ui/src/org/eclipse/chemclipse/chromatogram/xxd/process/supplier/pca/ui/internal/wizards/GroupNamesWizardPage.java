/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.InputFilesTable;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
		Label label = new Label(composite, SWT.NONE);
		label.setText("Group Name");
		textGroupName = new Text(composite, SWT.BORDER);
		textGroupName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(composite, SWT.NONE);
		label.setText("String or Regexp, which match with names");
		textSelectNames = new Text(composite, SWT.BORDER);
		textSelectNames.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setLayout(new GridLayout(1, false));
		group.setText("Settings");
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
		button = new Button(composite, SWT.PUSH);
		button.setText("Set Group names");
		button.addListener(SWT.Selection, e -> updateGroupNames());
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		gridData.widthHint = 100;
		gridData.verticalSpan = 5;
		inputFilesTable = new InputFilesTable(composite, gridData);
		IInputWizard inputWizard = (IInputWizard)getWizard();
		inputFilesTable.setDataInputEntries(inputWizard.getDataInputEntries());
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

	private void updateGroupNames() {

		String s = textSelectNames.getText().trim();
		if(s.isEmpty()) {
			return;
		}
		List<IDataInputEntry> filterInpfilteruts = new ArrayList<>();
		IInputWizard inputWizard = (IInputWizard)getWizard();
		switch(selectNames) {
			case CONTAINS_STRING:
				if(!caseSensitive.getSelection()) {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().toLowerCase().contains(s.toLowerCase())).forEach(filterInpfilteruts::add);
				} else {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().contains(s)).forEach(filterInpfilteruts::add);
				}
				break;
			case STARTS_WITH_STRING:
				if(!caseSensitive.getSelection()) {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().toLowerCase().startsWith(s.toLowerCase())).forEach(filterInpfilteruts::add);
				} else {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().startsWith(s)).forEach(filterInpfilteruts::add);
				}
				break;
			case ENDS_WITH_STRING:
				if(!caseSensitive.getSelection()) {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().toLowerCase().endsWith(s.toLowerCase())).forEach(filterInpfilteruts::add);
				} else {
					inputWizard.getDataInputEntries().stream().filter(i -> i.getName().endsWith(s)).forEach(filterInpfilteruts::add);
				}
				break;
			case REGEXP:
				Pattern p = Pattern.compile(s);
				inputWizard.getDataInputEntries().stream().filter(i -> p.matcher(i.getName()).find()).forEach(filterInpfilteruts::add);
				break;
			default:
				break;
		}
		String groupName = textGroupName.getText().trim();
		final String setGroupName = groupName.isEmpty() ? null : groupName;
		filterInpfilteruts.forEach(i -> i.setGroupName(setGroupName));
		inputFilesTable.update();
	}
}
