/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;

import org.eclipse.chemclipse.model.targets.TargetListUtil;
import org.eclipse.chemclipse.support.util.QuantReferencesListUtil;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.QuantitationReferenceInputValidator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class QuantReferencesListEditor extends ListEditor {

	public QuantReferencesListEditor(String name, String labelText, Composite parent) {

		super(name, labelText, parent);
		initialize(parent);
	}

	@Override
	protected String createList(String[] items) {

		QuantReferencesListUtil listUtil = new QuantReferencesListUtil();
		return listUtil.createList(items);
	}

	@Override
	protected String getNewInputObject() {

		List list = getList();
		InputDialog dialog = new InputDialog(getShell(), "Quantitation Reference", "You can create a new quantitation reference here.", "Styrene", new QuantitationReferenceInputValidator(list.getItems()));
		dialog.create();
		if(dialog.open() == Dialog.OK) {
			String target = dialog.getValue();
			return addItem(target, list);
		}
		return null;
	}

	private void initialize(Composite parent) {

		Composite composite = getButtonBoxControl(parent);
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Clear References");
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List list = getList();
				if(list != null) {
					Shell shell = Display.getCurrent().getActiveShell();
					MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL);
					messageBox.setText("Delete Quantitation References");
					messageBox.setMessage("Would you like to delete the selected references from the list?");
					int decision = messageBox.open();
					if(decision == SWT.YES) {
						list.removeAll();
					}
				}
			}
		});
	}

	private String addItem(String target, List list) {

		String[] items = list.getItems();
		if(!itemExistsInList(target, items)) {
			return target;
		}
		return null;
	}

	private boolean itemExistsInList(String target, String[] list) {

		for(String item : list) {
			if(item.equals(target)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected String[] parseString(String stringList) {

		TargetListUtil targetListUtil = new TargetListUtil();
		String[] targets = targetListUtil.parseString(stringList);
		Arrays.sort(targets);
		return targets;
	}
}