/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.LiteratureReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ExtendedLiteratureUI extends Composite implements IExtendedPartUI {

	private static final String DATA_URL = "DATA_URL";
	//
	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarSearch = new AtomicReference<>();
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerLiteratureControl = new AtomicReference<>();
	private AtomicReference<Button> buttonDOI = new AtomicReference<>();
	private AtomicReference<LiteratureUI> literatureControl = new AtomicReference<>();
	//
	private List<LiteratureReference> literatureReferences = new ArrayList<>();

	public ExtendedLiteratureUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(List<?> contentList) {

		literatureReferences.clear();
		if(contentList != null && !contentList.isEmpty()) {
			for(Object content : contentList) {
				literatureReferences.add(new LiteratureReference(content.toString()));
			}
		}
		//
		updateInput();
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

		enableToolbar(toolbarInfo, buttonToolbarInfo.get(), IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarSearch, buttonToolbarSearch.get(), IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		updateInput();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createButtonToolbarInfo(composite);
		createButtonToolbarSearch(composite);
		createComboViewerLiterature(composite);
		createButtonDOI(composite);
		createButtonSettings(composite);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				literatureControl.get().updateSearchResult(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createDataSection(Composite parent) {

		LiteratureUI styledText = new LiteratureUI(parent, SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setText("");
		styledText.setToolTipText("Literature");
		styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		literatureControl.set(styledText);
	}

	private void createButtonToolbarInfo(Composite parent) {

		buttonToolbarInfo.set(createButtonToggleToolbar(parent, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO));
	}

	private void createButtonToolbarSearch(Composite parent) {

		buttonToolbarSearch.set(createButtonToggleToolbar(parent, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH));
	}

	private void createComboViewerLiterature(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ListContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof LiteratureReference literatureReference) {
					String title = literatureReference.getTitle();
					if(title.isEmpty()) {
						return "Literature Reference";
					} else {
						return title;
					}
				}
				return null;
			}
		});
		combo.setToolTipText("Select a literature reference.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateLiteratureSelection();
			}
		});
		//
		comboViewerLiteratureControl.set(comboViewer);
	}

	private void createButtonDOI(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Open the contained DOI in an external browser.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXTERNAL_BROWSER, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object data = button.getData(DATA_URL);
				if(data instanceof String url) {
					if(!url.isEmpty()) {
						Program.launch(url);
					}
				}
			}
		});
		//
		buttonDOI.set(button);
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

	private void updateInput() {

		/*
		 * Update the combo viewer.
		 */
		comboViewerLiteratureControl.get().setInput(literatureReferences);
		if(!literatureReferences.isEmpty()) {
			comboViewerLiteratureControl.get().setSelection(new StructuredSelection(literatureReferences.get(0)));
		}
		//
		updateLiteratureSelection();
	}

	private void updateLiteratureSelection() {

		Object object = comboViewerLiteratureControl.get().getStructuredSelection().getFirstElement();
		if(object instanceof LiteratureReference literatureReference) {
			updateWidgets(literatureReference);
		}
	}

	private void updateWidgets(LiteratureReference literatureReference) {

		String content = "";
		String title = "";
		String url = "";
		//
		if(literatureReference != null) {
			content = literatureReference.getContent();
			title = literatureReference.getTitle();
			url = literatureReference.getUrl();
		}
		//
		literatureControl.get().setText(content);
		toolbarInfo.get().setText("Title: " + title);
		buttonDOI.get().setData(DATA_URL, url);
		buttonDOI.get().setEnabled(!url.isEmpty());
		buttonDOI.get().setToolTipText(!url.isEmpty() ? url : "It wasn't possible to extract and open a DOI.");
	}
}