/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.editors;

import java.util.Arrays;

import org.eclipse.chemclipse.support.l10n.Messages;
import org.eclipse.chemclipse.support.messages.ISupportMessages;
import org.eclipse.chemclipse.support.messages.SupportMessages;
import org.eclipse.chemclipse.support.util.DilutionListUtil;
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

public class DilutionListEditor extends ListEditor {

	public DilutionListEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		initialize(parent);
	}

	@Override
	protected String createList(String[] items) {

		DilutionListUtil dilutionListUtil = new DilutionListUtil();
		return dilutionListUtil.createList(items);
	}

	@Override
	protected String getNewInputObject() {

		List list = getList();
		Messages messages = SupportMessages.INSTANCE();
		InputDialog dialog = new InputDialog(getShell(), messages.getMessage(ISupportMessages.LABEL_DILUTION), messages.getMessage(ISupportMessages.LABEL_ADD_DILUTION, "1:10"), "", new DilutionInputValidator(list));
		dialog.create();
		if(dialog.open() == Dialog.OK) {
			String dilution = dialog.getValue();
			return addDilution(dilution, list);
		}
		return null;
	}

	private void initialize(Composite parent) {

		Messages messages = SupportMessages.INSTANCE();
		Composite composite = getButtonBoxControl(parent);
		Button button = new Button(composite, SWT.PUSH);
		button.setText(messages.getMessage(ISupportMessages.LABEL_CLEAR_DILUTIONS));
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
					messageBox.setText(messages.getMessage(ISupportMessages.LABEL_DELETE_DILUTION));
					messageBox.setMessage(messages.getMessage(ISupportMessages.LABEL_QUESTION_DELETE_DILUTIONS));
					int decision = messageBox.open();
					if(decision == SWT.YES) {
						list.removeAll();
					}
				}
			}
		});
	}

	private String addDilution(String dilution, List list) {

		String[] items = list.getItems();
		if(!itemExistsInList(dilution, items)) {
			return dilution;
		}
		return null;
	}

	private boolean itemExistsInList(String dilution, String[] list) {

		for(String item : list) {
			if(item.equals(dilution)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected String[] parseString(String stringList) {

		DilutionListUtil dilutionListUtil = new DilutionListUtil();
		String[] dilutions = dilutionListUtil.parseString(stringList);
		Arrays.sort(dilutions);
		return dilutions;
	}
}
