/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.identifier.IFlavorMarker;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

public class ExtendedFlavorMarkerUI extends LibraryInformationComposite {

	private Button buttonToolbarSearch;
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<FlavorMarkerListUI> listControl = new AtomicReference<>();
	private AtomicReference<LiteratureUI> literatureControl = new AtomicReference<>();

	public ExtendedFlavorMarkerUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void updateInput() {

		ILibraryInformation libraryInformation = getLibraryInformation();
		updateLiterature(null);
		//
		if(libraryInformation != null) {
			listControl.get().setInput(libraryInformation.getFlavorMarkers());
		} else {
			listControl.get().clear();
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarSearch(this);
		createDataSection(this);
		//
		initialize();
	}

	private void initialize() {

		initializeToolbarInfo();
		enableToolbar(toolbarSearch, buttonToolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		//
		applySettings();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToolbarInfo(composite);
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		createButtonSettings(composite);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				listControl.get().setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createDataSection(Composite parent) {

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		createTableSection(sashForm);
		createLiteratureSection(sashForm);
		sashForm.setWeights(600, 400);
	}

	private void createTableSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		FlavorMarkerListUI listUI = new FlavorMarkerListUI(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = listUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		listUI.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object object = listUI.getStructuredSelection().getFirstElement();
				if(object instanceof IFlavorMarker flavorMarker) {
					updateLiterature(flavorMarker);
				} else {
					updateLiterature(null);
				}
			}
		});
		//
		listControl.set(listUI);
	}

	private void createLiteratureSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		LiteratureUI styledText = new LiteratureUI(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setText("");
		styledText.setToolTipText("Literature");
		styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		literatureControl.set(styledText);
	}

	private void updateLiterature(IFlavorMarker flavorMarker) {

		String content;
		if(flavorMarker != null) {
			content = flavorMarker.getLiteratureReference();
		} else {
			content = "";
		}
		//
		literatureControl.get().setText(content);
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePage.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		updateInput();
	}
}