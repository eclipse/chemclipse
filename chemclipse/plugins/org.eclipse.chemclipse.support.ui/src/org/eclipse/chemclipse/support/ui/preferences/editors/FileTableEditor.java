/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.chemclipse.support.util.FileSettingUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

public class FileTableEditor extends TableViewerFieldEditor<File> {

	private String[] filterExtensions;
	private FileSettingUtil fileSettingUtil;

	public FileTableEditor(String name, String labelText, String[] filterExtensions, Composite parent) {

		super(name, labelText, new String[]{SupportMessages.fileName, SupportMessages.filePath}, new int[]{150, 200}, parent);
		this.filterExtensions = filterExtensions;
		this.fileSettingUtil = new FileSettingUtil();
	}

	@Override
	protected String createSavePreferences(List<File> values) {

		return fileSettingUtil.serialize(values);
	}

	@Override
	protected List<File> parseSavePreferences(String data) {

		return fileSettingUtil.deserialize(data);
	}

	@Override
	protected String convertColumnValue(File value, int indexColumn) {

		if(indexColumn == 0) {
			return value.getName();
		}
		return value.getAbsolutePath();
	}

	@Override
	protected List<File> getNewInputObject() {

		List<File> files = new ArrayList<>();
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.MULTI);
		dialog.setText(SupportMessages.selectFile);
		if(filterExtensions != null) {
			dialog.setFilterExtensions(filterExtensions);
		}
		String file = dialog.open();
		if(file != null) {
			String[] names = dialog.getFileNames();
			for(String name : names) {
				files.add(new File(name));
			}
		}
		return files;
	}

	@Override
	protected int compareValue(File value1, File value2, int indexColumn) {

		if(indexColumn == 0) {
			return value1.getName().compareTo(value2.getName());
		}
		return value1.getAbsoluteFile().compareTo(value2.getAbsoluteFile());
	}
}
