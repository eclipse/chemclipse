/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class FileListEditor extends ListEditor {

	private String[] filterExtensions;
	private String[] filterNames;

	public FileListEditor(String name, String labelText, Composite parent) {

		super(name, labelText, parent);
		initialize(parent);
	}

	public void setFilterExtensionsAndNames(String[] filterExtensions, String[] filterNames) {

		this.filterExtensions = filterExtensions;
		this.filterNames = filterNames;
	}

	@Override
	protected String createList(String[] items) {

		FileListUtil fileListUtil = new FileListUtil();
		return fileListUtil.createList(items);
	}

	@Override
	protected String getNewInputObject() {

		List list = getList();
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		dialog.setText(SupportMessages.selectFile);
		if(filterExtensions != null && filterNames != null) {
			dialog.setFilterExtensions(filterExtensions);
			dialog.setFilterNames(filterNames);
		}
		String file = dialog.open();
		if(file != null) {
			return addFile(file, list);
		}
		return null;
	}

	private void initialize(Composite parent) {

		Composite composite = getButtonBoxControl(parent);
		Button button = new Button(composite, SWT.PUSH);
		button.setText(SupportMessages.clear);
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
					messageBox.setText(SupportMessages.files);
					messageBox.setMessage(SupportMessages.removeAllFilesFromList);
					int decision = messageBox.open();
					if(decision == SWT.YES) {
						list.removeAll();
					}
				}
			}
		});
	}

	private String addFile(String file, List list) {

		String[] items = list.getItems();
		if(!itemExistsInList(file, items)) {
			return file;
		}
		return null;
	}

	private boolean itemExistsInList(String file, String[] list) {

		for(String item : list) {
			if(item.equals(file)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected String[] parseString(String stringList) {

		FileListUtil fileListUtil = new FileListUtil();
		String[] files = fileListUtil.parseString(stringList);
		Arrays.sort(files);
		return files;
	}
}
