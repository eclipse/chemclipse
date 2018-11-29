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
package org.eclipse.chemclipse.ux.extension.xxd.ui.fieldeditors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.identifier.ITargetTemplate;
import org.eclipse.chemclipse.support.ui.preferences.editors.TargetInputValidator;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.TargetTemplates;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.TargetTemplateListUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
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

public class TargetFieldEditor extends FieldEditor {

	private static final int NUMBER_COLUMNS = 2;
	//
	private TargetTemplates targetTemplates = new TargetTemplates();
	private TargetTemplateListUI targetTemplateListUI;

	public TargetFieldEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		//
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(NUMBER_COLUMNS, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		//
		createLabelSection(composite);
		createSearchSection(composite);
		createTableSection(composite);
		createButtonGroup(composite);
	}

	private void createSearchSection(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = NUMBER_COLUMNS;
		searchSupportUI.setLayoutData(gridData);
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				targetTemplateListUI.setSearchText(searchText, caseSensitive);
			}
		});
	}

	private void createLabelSection(Composite parent) {

		Label label = new Label(parent, SWT.LEFT);
		label.setText("");
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
		targetTemplateListUI = new TargetTemplateListUI(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
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
		setButtonLayoutData(createButtonImport(composite));
		setButtonLayoutData(createButtonEdit(composite));
		setButtonLayoutData(createButtonRemove(composite));
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add");
		button.setToolTipText("Add a target template.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				InputDialog dialog = new InputDialog(button.getShell(), "Target", "You can create a new target here.", "Styrene | 100-42-5 | comment | contributor | referenceId", new TargetInputValidator(targetTemplates.getNameList()));
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue();
					ITargetTemplate targetTemplate = targetTemplates.extractTargetTemplate(item);
					if(targetTemplate != null) {
						targetTemplates.add(targetTemplate);
						setTableViewerInput();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Import");
		button.setToolTipText("Import a target template(s) from a library.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				// TODO
				System.out.println("Implement the import function.");
			}
		});
		//
		return button;
	}

	private Button createButtonEdit(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Edit");
		button.setToolTipText("Edit the selected target template.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = (IStructuredSelection)targetTemplateListUI.getSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof ITargetTemplate) {
					ITargetTemplate targetTemplate = (ITargetTemplate)object;
					InputDialog dialog = new InputDialog(button.getShell(), "Target", "Edit the target.", targetTemplates.extractTargetTemplate(targetTemplate), new TargetInputValidator(targetTemplates.getNameList()));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						ITargetTemplate targetTemplateNew = targetTemplates.extractTargetTemplate(item);
						if(targetTemplateNew != null) {
							targetTemplate.setName(targetTemplateNew.getName());
							targetTemplate.setCasNumber(targetTemplateNew.getCasNumber());
							targetTemplate.setComment(targetTemplateNew.getComment());
							targetTemplate.setContributor(targetTemplateNew.getContributor());
							targetTemplate.setReferenceId(targetTemplateNew.getReferenceId());
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
		button.setToolTipText("Remove the selected target template(s).");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(button.getShell(), "Target Template(s)", "Do you want to delete the selected target template(s)?")) {
					List<ITargetTemplate> removeItems = new ArrayList<>();
					IStructuredSelection structuredSelection = (IStructuredSelection)targetTemplateListUI.getSelection();
					for(Object object : structuredSelection.toArray()) {
						if(object instanceof ITargetTemplate) {
							removeItems.add((ITargetTemplate)object);
						}
					}
					targetTemplates.removeAll(removeItems);
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private void setTableViewerInput() {

		targetTemplateListUI.setInput(targetTemplates);
	}

	@Override
	protected void doLoad() {

		String entries = getPreferenceStore().getString(getPreferenceName());
		targetTemplates.load(entries);
		setTableViewerInput();
	}

	@Override
	protected void doLoadDefault() {

		String entries = getPreferenceStore().getDefaultString(getPreferenceName());
		targetTemplates.loadDefault(entries);
		setTableViewerInput();
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), targetTemplates.save());
	}

	@Override
	public int getNumberOfControls() {

		return 1;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

	}
}
