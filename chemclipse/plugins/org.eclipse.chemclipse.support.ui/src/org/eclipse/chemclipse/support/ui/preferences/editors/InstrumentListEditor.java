/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.util.InstrumentNameListUtil;
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

public class InstrumentListEditor extends ListEditor {

	public InstrumentListEditor(String name, String labelText, Composite parent) {

		super(name, labelText, parent);
		initialize(parent);
	}

	@Override
	protected String createList(String[] items) {

		InstrumentNameListUtil instrumentListUtil = new InstrumentNameListUtil();
		return instrumentListUtil.createList(items);
	}

	@Override
	protected String getNewInputObject() {

		List list = getList();
		Messages messages = SupportMessages.INSTANCE();
		InputDialog dialog = new InputDialog(getShell(), messages.getMessage(ISupportMessages.LABEL_INSTRUMENT_NAME), messages.getMessage(ISupportMessages.LABEL_ADD_INSTRUMENT, "API 5000"), "", new InstrumentInputValidator(list));
		dialog.create();
		if(dialog.open() == Dialog.OK) {
			String instrument = dialog.getValue();
			return addInstrument(instrument, list);
		}
		return null;
	}

	private void initialize(Composite parent) {

		Messages messages = SupportMessages.INSTANCE();
		Composite composite = getButtonBoxControl(parent);
		Button button = new Button(composite, SWT.PUSH);
		button.setText(messages.getMessage(ISupportMessages.LABEL_CLEAR_INSTRUMENTS));
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
					messageBox.setText(messages.getMessage(ISupportMessages.LABEL_DELETE_INSTRUMENT));
					messageBox.setMessage(messages.getMessage(ISupportMessages.LABEL_QUESTION_DELETE_INSTRUMENTS));
					int decision = messageBox.open();
					if(decision == SWT.YES) {
						list.removeAll();
					}
				}
			}
		});
	}

	private String addInstrument(String instrument, List list) {

		String[] items = list.getItems();
		if(!itemExistsInList(instrument, items)) {
			return instrument;
		}
		return null;
	}

	private boolean itemExistsInList(String instrument, String[] list) {

		for(String item : list) {
			if(item.equals(instrument)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected String[] parseString(String stringList) {

		InstrumentNameListUtil instrumentListUtil = new InstrumentNameListUtil();
		String[] instruments = instrumentListUtil.parseString(stringList);
		Arrays.sort(instruments);
		return instruments;
	}
}
