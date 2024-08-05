/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.ui.fieldeditors;

import org.eclipse.chemclipse.model.ui.swt.FileHeaderDataEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class FileHeaderDataFieldEditor extends FieldEditor {

	private FileHeaderDataEditor fileHeaderDataEditor;

	public FileHeaderDataFieldEditor(String name, String labelText, Composite parent) {

		init(name, labelText);
		createControl(parent);
	}

	@Override
	public int getNumberOfControls() {

		return 1;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		if(numColumns >= 2) {
			GridData gridData = (GridData)fileHeaderDataEditor.getLayoutData();
			gridData.horizontalSpan = numColumns - 1;
			gridData.grabExcessHorizontalSpace = true;
		}
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		fileHeaderDataEditor = new FileHeaderDataEditor(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		fileHeaderDataEditor.setLayoutData(gridData);
		//
		fileHeaderDataEditor.addChangeListener(new Listener() {

			@Override
			public void handleEvent(Event event) {

				String message = fileHeaderDataEditor.getMessage();
				if(message != null) {
					showErrorMessage(message);
				} else {
					clearErrorMessage();
				}
			}
		});
	}

	@Override
	protected void doLoad() {

		String entries = getPreferenceStore().getString(getPreferenceName());
		fileHeaderDataEditor.load(entries);
	}

	@Override
	protected void doLoadDefault() {

		String entries = getPreferenceStore().getDefaultString(getPreferenceName());
		fileHeaderDataEditor.load(entries);
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), fileHeaderDataEditor.save());
	}
}