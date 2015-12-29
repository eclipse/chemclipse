/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.editors;

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

import org.eclipse.chemclipse.support.util.IonListUtil;

/**
 * @author eselmeister
 */
public class IonListEditor extends ListEditor {

	public IonListEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		Composite composite = getButtonBoxControl(parent);
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Clear List");
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
					messageBox.setText("Delete Ions");
					messageBox.setMessage("Do you really want to delete all ions from the list?");
					int decision = messageBox.open();
					if(decision == SWT.YES) {
						list.removeAll();
					}
				}
			}
		});
	}

	@Override
	protected String createList(String[] items) {

		IonListUtil ionListUtil = new IonListUtil();
		return ionListUtil.createList(items);
	}

	@Override
	protected String getNewInputObject() {

		List list = getList();
		InputDialog dialog = new InputDialog(new Shell(), "Enter a single ion or an ion range.", "E.g. 104 for styrene or 310 - 340 to add the range.", "", new IonInputValidator(list));
		dialog.create();
		dialog.open();
		String ions = dialog.getValue();
		return addIonsAndGetReturnValue(ions, list);
	}

	private String addIonsAndGetReturnValue(String ions, List list) {

		String[] items = list.getItems();
		String ionList[] = ions.trim().split("-");
		String ion = "";
		if(ionList.length == 1) {
			ion = ionList[0].trim();
			//
		} else if(ionList.length == 2) {
			/*
			 * Parse the range.
			 */
			int startIon = Integer.parseInt(ionList[0].trim());
			int stopIon = Integer.parseInt(ionList[1].trim());
			/*
			 * Swap the values if needed.
			 */
			int tmp;
			if(startIon > stopIon) {
				tmp = startIon;
				startIon = stopIon;
				stopIon = tmp;
			}
			/*
			 * The stop ion is the ion to be returned.
			 * All other ions will be stored directly in the
			 * list.
			 */
			ion = Integer.toString(stopIon);
			for(int i = stopIon - 1; i >= startIon; i--) {
				/*
				 * Check that the items doesn't exist.
				 */
				String item = Integer.toString(i);
				if(itemDoesNotExistsInList(items, item)) {
					list.add(item);
				}
			}
		}
		return ion;
	}

	private boolean itemDoesNotExistsInList(String[] items, String item) {

		for(String itemx : items) {
			if(itemx.equals(item)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected String[] parseString(String stringList) {

		IonListUtil ionListUtil = new IonListUtil();
		return ionListUtil.parseString(stringList);
	}
}
