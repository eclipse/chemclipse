/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

public abstract class AbstractTabFolderPreferencePage extends PreferencePage implements ITabFolderPreferencePage, IPropertyChangeListener {

	private TabFolder tabFolder;
	private List<FieldEditor> fieldEditors = null;

	public TabFolder getTabFolder() {

		return tabFolder;
	}

	@Override
	protected Control createContents(Composite parent) {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginTop = 10;
		gridLayout.marginLeft = 10;
		gridLayout.marginRight = 10;
		GridData gridData = new GridData(GridData.FILL_BOTH);
		/*
		 * Create the tab folder and items.
		 */
		createTabFolder(parent);
		createTabItems(gridLayout, gridData);
		//
		createFieldEditors();
		initialize();
		checkState();
		//
		return tabFolder;
	}

	private void createTabFolder(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		tabFolder = new TabFolder(composite, SWT.BORDER);
	}

	protected abstract void createFieldEditors();

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		if(event.getProperty().equals(FieldEditor.IS_VALID)) {
			boolean newValue = ((Boolean)event.getNewValue()).booleanValue();
			if(newValue) {
				checkState();
			} else {
				setValid(newValue);
			}
		}
	}

	protected void addField(FieldEditor fieldEditor) {

		if(fieldEditors == null) {
			fieldEditors = new ArrayList<>();
		}
		fieldEditors.add(fieldEditor);
	}

	protected void initialize() {

		if(fieldEditors != null) {
			Iterator<FieldEditor> iterator = fieldEditors.iterator();
			while(iterator.hasNext()) {
				FieldEditor fieldEditor = iterator.next();
				fieldEditor.setPage(this);
				fieldEditor.setPropertyChangeListener(this);
				fieldEditor.setPreferenceStore(getPreferenceStore());
				fieldEditor.load();
			}
		}
	}

	@Override
	protected void performDefaults() {

		if(fieldEditors != null) {
			Iterator<FieldEditor> iterator = fieldEditors.iterator();
			while(iterator.hasNext()) {
				FieldEditor fieldEditor = iterator.next();
				fieldEditor.loadDefault();
			}
		}
		checkState();
		super.performDefaults();
	}

	@Override
	public boolean performOk() {

		if(fieldEditors != null) {
			Iterator<FieldEditor> iterator = fieldEditors.iterator();
			while(iterator.hasNext()) {
				FieldEditor fieldEditor = iterator.next();
				fieldEditor.store();
			}
		}
		return true;
	}

	protected void checkState() {

		boolean valid = true;
		/*
		 * Check all field editors.
		 */
		if(fieldEditors != null) {
			int size = fieldEditors.size();
			for(int i = 0; i < size; i++) {
				FieldEditor fieldEditor = fieldEditors.get(i);
				valid = fieldEditor.isValid();
				if(!valid) {
					setErrorMessage(fieldEditor.getLabelText());
					break;
				}
			}
		}
		setValid(valid);
		if(valid) {
			setErrorMessage(null);
		}
	}
}
