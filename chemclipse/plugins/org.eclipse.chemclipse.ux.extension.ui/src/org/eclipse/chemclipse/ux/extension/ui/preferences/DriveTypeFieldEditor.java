/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.preferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class DriveTypeFieldEditor extends StringFieldEditor {

	private Button buttonShow;

	public DriveTypeFieldEditor(String name, String labelText, Composite parent) {

		super(name, labelText, parent);
	}

	@Override
	public int getNumberOfControls() {

		return 3;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		((GridData)getTextControl().getLayoutData()).horizontalSpan = numColumns - 2;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		super.doFillIntoGrid(parent, numColumns - 1);
		buttonShow = createButtonShow(parent);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		int widthHint = convertHorizontalDLUsToPixels(buttonShow, IDialogConstants.BUTTON_WIDTH);
		gridData.widthHint = Math.max(widthHint, buttonShow.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		buttonShow.setLayoutData(gridData);
	}

	private Button createButtonShow(Composite parent) {

		if(buttonShow == null) {
			Button button = new Button(parent, SWT.PUSH);
			button.setText("Show...");
			button.setToolTipText("List the available drive types.");
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					String message;
					if(OperatingSystemUtils.isWindows()) {
						String lineDelimiter = OperatingSystemUtils.getLineDelimiter();
						StringBuilder builder = new StringBuilder();
						File[] roots = File.listRoots();
						for(File root : roots) {
							builder.append(root.toString());
							builder.append(": ");
							try {
								FileStore fileStore = Files.getFileStore(root.toPath());
								String type = (fileStore != null) ? fileStore.type() : "";
								builder.append(type);
							} catch(IOException e1) {
								builder.append("The type can't be detected");
							}
							builder.append(lineDelimiter);
						}
						message = builder.toString();
					} else {
						message = "The drive types option is only available on Windows.";
					}
					//
					MessageDialog.openInformation(e.display.getActiveShell(), "Drive Types", message);
				}
			});
			button.addDisposeListener(event -> buttonShow = null);
			return button;
		} else {
			return buttonShow;
		}
	}
}