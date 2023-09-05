/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors;

import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.swt.VirtualChannelTable;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class VirtualChannelFieldEditor extends FieldEditor {

	public static final String ADD = "Add";
	public static final String ADD_TOOLTIP = "Add a new virtual channel";
	public static final String EDIT = "Edit";
	public static final String EDIT_TOOLTIP = "Edit the selected virtual channel";
	public static final String REMOVE = "Remove";
	public static final String REMOVE_TOOLTIP = "Remove the selected virtual channels";
	public static final String REMOVE_ALL = "Remove All";
	public static final String REMOVE_ALL_TOOLTIP = "Remove all virtual channels";
	public static final String IMPORT = "Import";
	public static final String EXPORT = "Export";
	//
	public static final String IMPORT_TITLE = "Import virtual channels";
	public static final String EXPORT_TITLE = "Export virtual channels";
	public static final String DIALOG_TITLE = "virtual channels";
	public static final String MESSAGE_ADD = "You can create a new virtual channel here.";
	public static final String MESSAGE_EDIT = "Edit the selected virtual channel.";
	public static final String MESSAGE_REMOVE = "Do you want to delete the selected virtual channels?";
	public static final String MESSAGE_REMOVE_ALL = "Do you want to delete all virtual channels?";
	public static final String MESSAGE_EXPORT_SUCCESSFUL = "virtual channel have been exported successfully.";
	public static final String MESSAGE_EXPORT_FAILED = "Failed to export the virtual channels.";
	//
	private VirtualChannelTable editor;

	public VirtualChannelFieldEditor(String name, String labelText, Composite parent) {

		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		editor = new VirtualChannelTable(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 150;
		gridData.widthHint = 600;
		editor.setLayoutData(gridData);
	}

	@Override
	protected void doLoad() {

		String entries = getPreferenceStore().getString(getPreferenceName());
		editor.load(entries);
	}

	@Override
	protected void doLoadDefault() {

		String entries = getPreferenceStore().getDefaultString(getPreferenceName());
		editor.load(entries);
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), editor.save());
	}

	@Override
	public int getNumberOfControls() {

		return 1;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		if(numColumns >= 2) {
			GridData gridData = (GridData)editor.getLayoutData();
			gridData.horizontalSpan = numColumns - 1;
			gridData.grabExcessHorizontalSpace = true;
		}
	}
}
