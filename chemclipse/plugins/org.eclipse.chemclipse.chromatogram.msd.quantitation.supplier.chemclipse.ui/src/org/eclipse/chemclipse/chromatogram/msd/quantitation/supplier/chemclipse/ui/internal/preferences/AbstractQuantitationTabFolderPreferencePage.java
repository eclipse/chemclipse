/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class AbstractQuantitationTabFolderPreferencePage extends AbstractCustomTabFolderPreferencePage {

	private TabItem tabItemOverview;
	private TabItem tabItemDatabaseSelection;
	private TabItem tabItemIdentificationSettings;
	private Composite fieldEditorOverview;
	private Composite fieldEditorDatabaseSelection;
	private Composite fieldEditorIdentificationSettings;

	@Override
	public void createTabItems(GridLayout gridLayout, GridData gridData) {

		TabFolder tabFolder = getTabFolder();
		createOverviewTabItem(tabFolder, gridLayout, gridData);
		createDatabaseSelectionTabItem(tabFolder, gridLayout, gridData);
		createIdentificationSettingsTabItem(tabFolder, gridLayout, gridData);
	}

	private void createOverviewTabItem(TabFolder tabFolder, GridLayout gridLayout, GridData gridData) {

		tabItemOverview = new TabItem(tabFolder, SWT.NONE);
		tabItemOverview.setText("Overview");
		Composite overviewComposite = new Composite(tabFolder, SWT.NONE);
		overviewComposite.setLayout(gridLayout);
		tabItemOverview.setControl(overviewComposite);
		fieldEditorOverview = new Composite(overviewComposite, SWT.NULL);
		fieldEditorOverview.setLayout(gridLayout);
		fieldEditorOverview.setLayoutData(gridData);
	}

	private void createDatabaseSelectionTabItem(TabFolder tabFolder, GridLayout gridLayout, GridData gridData) {

		tabItemDatabaseSelection = new TabItem(tabFolder, SWT.NONE);
		tabItemDatabaseSelection.setText("Database Selection");
		Composite selectionComposite = new Composite(tabFolder, SWT.NONE);
		selectionComposite.setLayout(gridLayout);
		tabItemDatabaseSelection.setControl(selectionComposite);
		fieldEditorDatabaseSelection = new Composite(selectionComposite, SWT.NULL);
		fieldEditorDatabaseSelection.setLayout(gridLayout);
		fieldEditorDatabaseSelection.setLayoutData(gridData);
	}

	private void createIdentificationSettingsTabItem(TabFolder tabFolder, GridLayout gridLayout, GridData gridData) {

		tabItemIdentificationSettings = new TabItem(tabFolder, SWT.NONE);
		tabItemIdentificationSettings.setText("Identification Settings");
		Composite identificationComposite = new Composite(tabFolder, SWT.NONE);
		identificationComposite.setLayout(gridLayout);
		tabItemIdentificationSettings.setControl(identificationComposite);
		fieldEditorIdentificationSettings = new Composite(identificationComposite, SWT.NULL);
		fieldEditorIdentificationSettings.setLayout(gridLayout);
		fieldEditorIdentificationSettings.setLayoutData(gridData);
	}

	/**
	 * Returns the overview field editor.
	 * 
	 * @return {@link Composite}
	 */
	protected Composite getFieldEditorOverview() {

		return fieldEditorOverview;
	}

	/**
	 * Returns the identification settings field editor.
	 * 
	 * @return {@link Composite}
	 */
	protected Composite getFieldEditorIdentificationSettings() {

		return fieldEditorIdentificationSettings;
	}

	/**
	 * Returns the database selection field editor.
	 * 
	 * @return {@link Composite}
	 */
	protected Composite getFieldEditorDatabaseSelection() {

		return fieldEditorDatabaseSelection;
	}
}
