/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.ProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.history.EditHistory;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.support.history.ProcessSupplierEntry;
import org.eclipse.chemclipse.support.history.ProcessSupplierSupport;
import org.eclipse.chemclipse.support.ui.files.ExtendedFileDialog;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageEditHistory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class ExtendedEditHistoryUI extends Composite implements IExtendedPartUI {

	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarSearch = new AtomicReference<>();
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<EditHistoryListUI> tableViewer = new AtomicReference<>();
	//
	private IEditHistory editHistory = null;

	public ExtendedEditHistoryUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(IEditHistory editHistory) {

		this.editHistory = editHistory;
		updateInput();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarSearch(this);
		createEditHistoryListUI();
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo.get(), IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarSearch, buttonToolbarSearch.get(), IMAGE_SEARCH, TOOLTIP_EDIT, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarSearch(composite);
		createButtonMethod(composite);
		createButtonSettings(composite);
	}

	private void createButtonToggleToolbarInfo(Composite parent) {

		buttonToolbarInfo.set(createButtonToggleToolbar(parent, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO));
	}

	private void createButtonToggleToolbarSearch(Composite parent) {

		buttonToolbarSearch.set(createButtonToggleToolbar(parent, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH));
	}

	private void createButtonMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Create a process method using the history.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<ProcessSupplierEntry> processSupplierEntries = getProcessSupplierEntry();
				if(processSupplierEntries.isEmpty()) {
					MessageDialog.openInformation(e.display.getActiveShell(), ExtensionMessages.processMethod, "The edit history does not contain any method process specific information.");
				} else {
					FileDialog fileDialog = ExtendedFileDialog.create(e.display.getActiveShell(), SWT.SAVE);
					fileDialog.setOverwrite(true);
					fileDialog.setText(ExtensionMessages.processMethod);
					fileDialog.setFilterExtensions(new String[]{MethodConverter.FILTER_EXTENSION});
					fileDialog.setFilterNames(new String[]{MethodConverter.FILTER_NAME});
					fileDialog.setFileName(MethodConverter.FILE_NAME);
					fileDialog.setFilterPath(MethodConverter.getUserMethodDirectory().getAbsolutePath());
					//
					String pathname = fileDialog.open();
					if(pathname != null) {
						File file = new File(pathname);
						/*
						 * Method
						 */
						Set<DataCategory> dataCategories = new HashSet<>(Arrays.asList(DataCategory.chromatographyCategories()));
						ProcessMethod processMethod = new ProcessMethod(dataCategories);
						for(ProcessSupplierEntry processSupplierEntry : processSupplierEntries) {
							ProcessEntry processEntry = new ProcessEntry(processMethod);
							processEntry.setProcessorId(processSupplierEntry.getId());
							processEntry.setName(processSupplierEntry.getName());
							processEntry.setDescription(processSupplierEntry.getDescription());
							processEntry.setSettings(processSupplierEntry.getUserSettings());
							processMethod.addProcessEntry(processEntry);
						}
						/*
						 * Save
						 */
						IProcessingInfo<?> processingInfo = MethodConverter.convert(file, processMethod, MethodConverter.DEFAULT_METHOD_CONVERTER_ID, new NullProgressMonitor());
						if(!processingInfo.hasErrorMessages()) {
							SupplierEditorSupport supplierEditorSupport = new SupplierEditorSupport(DataType.MTH, () -> Activator.getDefault().getEclipseContext());
							supplierEditorSupport.openEditor(file, false);
						}
					}
				}
			}
		});
	}

	private List<ProcessSupplierEntry> getProcessSupplierEntry() {

		List<ProcessSupplierEntry> processSupplierEntries = new ArrayList<>();
		//
		if(editHistory != null) {
			for(IEditInformation editInformation : editHistory) {
				if(ProcessSupplierSupport.isProcessSupplierEntry(editInformation)) {
					ProcessSupplierEntry processSupplierEntry = ProcessSupplierSupport.extractProcessSupplierEntry(editInformation);
					if(processSupplierEntry != null) {
						processSupplierEntries.add(processSupplierEntry);
					}
				}
			}
		}
		//
		return processSupplierEntries;
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageEditHistory.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings(display);
			}
		});
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

				tableViewer.get().setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createEditHistoryListUI() {

		EditHistoryListUI editHistoryListUI = new EditHistoryListUI(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		editHistoryListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.set(editHistoryListUI);
	}

	private void applySettings(Display display) {

		updateInput();
	}

	private void updateInput() {

		IEditHistory editHistoryFiltered = getEditHistoryFiltered();
		tableViewer.get().setInput(editHistoryFiltered);
		toolbarInfo.get().setText("Edit History Events: " + ((editHistoryFiltered != null) ? editHistoryFiltered.size() : "--"));
	}

	private IEditHistory getEditHistoryFiltered() {

		IEditHistory editHistoryFiltered = editHistory;
		if(editHistory != null) {
			if(PreferenceSupplier.isHideProcessMethodEntries()) {
				editHistoryFiltered = new EditHistory();
				for(IEditInformation editInformation : editHistory) {
					if(!ProcessSupplierSupport.isProcessSupplierEntry(editInformation)) {
						editHistoryFiltered.add(editInformation);
					}
				}
			}
		}
		//
		return editHistoryFiltered;
	}
}