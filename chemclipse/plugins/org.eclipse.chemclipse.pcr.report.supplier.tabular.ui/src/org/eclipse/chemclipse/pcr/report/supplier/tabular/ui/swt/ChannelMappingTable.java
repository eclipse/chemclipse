/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.swt;

import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.ADD_TOOLTIP;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.DIALOG_TITLE;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.EDIT_TOOLTIP;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.EXPORT_TITLE;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.IMPORT_TITLE;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.MESSAGE_ADD;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.MESSAGE_EDIT;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.MESSAGE_EXPORT_FAILED;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.MESSAGE_EXPORT_SUCCESSFUL;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.MESSAGE_REMOVE;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.MESSAGE_REMOVE_ALL;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.REMOVE_ALL_TOOLTIP;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor.REMOVE_TOOLTIP;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.csv.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMapping;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMappings;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.internal.provider.ChannelMappingInputValidator;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.ui.swt.dialogs.WindowsFileDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.IChangeListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ChannelMappingTable extends Composite implements IChangeListener, IExtendedPartUI {

	private static final String FILTER_EXTENSION = "*.txt";
	private static final String FILTER_NAME = "Channel Mapping (*.txt)";
	private static final String FILE_NAME = "ChannelMapping.txt";
	//
	private static final String CATEGORY = "PCR Report";
	private static final String DELETE = "Delete";
	//
	public static final String EXAMPLE = "New Subset | 1 | Channel One";
	//
	private AtomicReference<ChannelMappingListUI> tableViewer = new AtomicReference<>();
	//
	private List<Button> buttons = new ArrayList<>();
	private List<Listener> listeners = new ArrayList<>();
	//
	private ChannelMappings channelMappings = new ChannelMappings();

	public ChannelMappingTable(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public ChannelMappings getChannelMappings() {

		return channelMappings;
	}

	public void load(String entries) {

		channelMappings.load(entries);
		setViewerInput();
	}

	public String save() {

		return channelMappings.save();
	}

	@Override
	public void addChangeListener(Listener listener) {

		/*
		 * Listen to changes in the table.
		 */
		Control control = tableViewer.get().getControl();
		control.addListener(SWT.Selection, listener);
		control.addListener(SWT.KeyUp, listener);
		control.addListener(SWT.MouseUp, listener);
		control.addListener(SWT.MouseDoubleClick, listener);
		/*
		 * Listen to selection of the buttons.
		 */
		for(Button button : buttons) {
			button.addListener(SWT.Selection, listener);
			button.addListener(SWT.KeyUp, listener);
			button.addListener(SWT.MouseUp, listener);
			button.addListener(SWT.MouseDoubleClick, listener);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {

		tableViewer.get().getControl().setEnabled(enabled);
		for(Button button : buttons) {
			button.setEnabled(enabled);
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		//
		createToolbarMain(this);
		createTableSection(this);
		//
		initialize();
	}

	private void initialize() {

		setViewerInput();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		add(createButtonAdd(composite));
		add(createButtonEdit(composite));
		add(createButtonRemove(composite));
		add(createButtonRemoveAll(composite));
		add(createButtonImport(composite));
		add(createButtonExport(composite));
	}

	private void add(Button button) {

		buttons.add(button);
	}

	private void createTableSection(Composite parent) {

		ChannelMappingListUI channelMappingListUI = new ChannelMappingListUI(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		channelMappingListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		channelMappingListUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				setViewerInput();
			}
		});
		//
		Shell shell = channelMappingListUI.getTable().getShell();
		ITableSettings tableSettings = channelMappingListUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		addKeyEventProcessors(shell, tableSettings);
		channelMappingListUI.applySettings(tableSettings);
		//
		tableViewer.set(channelMappingListUI);
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(ADD_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				InputDialog dialog = new InputDialog(e.display.getActiveShell(), DIALOG_TITLE, MESSAGE_ADD, EXAMPLE, new ChannelMappingInputValidator(channelMappings));
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue();
					ChannelMapping mapping = channelMappings.extractChannelMapping(item);
					if(mapping != null) {
						channelMappings.add(mapping);
						setViewerInput();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonEdit(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(EDIT_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = (IStructuredSelection)tableViewer.get().getSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof ChannelMapping) {
					ChannelMappings modified = new ChannelMappings();
					modified.addAll(channelMappings);
					ChannelMapping mapping = (ChannelMapping)object;
					modified.remove(mapping);
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), DIALOG_TITLE, MESSAGE_EDIT, channelMappings.extractSetting(mapping), new ChannelMappingInputValidator(modified));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						ChannelMapping settingNew = channelMappings.extractChannelMapping(item);
						mapping.copyFrom(settingNew);
						setViewerInput();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRemove(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(REMOVE_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteItems(e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(REMOVE_ALL_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), DIALOG_TITLE, MESSAGE_REMOVE_ALL)) {
					channelMappings.clear();
					setViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(IMPORT_TITLE);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(OperatingSystemUtils.isWindows()) {
					WindowsFileDialog.ClearInitialDirectoryWorkaround();
				}
				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText(IMPORT_TITLE);
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFilterPath(PreferenceSupplier.getListPathImport());
				String path = fileDialog.open();
				if(path != null) {
					PreferenceSupplier.setListPathImport(fileDialog.getFilterPath());
					File file = new File(path);
					channelMappings.importItems(file);
					setViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(EXPORT_TITLE);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(OperatingSystemUtils.isWindows()) {
					WindowsFileDialog.ClearInitialDirectoryWorkaround();
				}
				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
				fileDialog.setOverwrite(true);
				fileDialog.setText(EXPORT_TITLE);
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFileName(FILE_NAME);
				fileDialog.setFilterPath(PreferenceSupplier.getListPathExport());
				String path = fileDialog.open();
				if(path != null) {
					PreferenceSupplier.setListPathExport(fileDialog.getFilterPath());
					File file = new File(path);
					if(channelMappings.exportItems(file)) {
						MessageDialog.openInformation(e.display.getActiveShell(), EXPORT_TITLE, MESSAGE_EXPORT_SUCCESSFUL);
					} else {
						MessageDialog.openWarning(e.display.getActiveShell(), EXPORT_TITLE, MESSAGE_EXPORT_FAILED);
					}
				}
			}
		});
		//
		return button;
	}

	private void setViewerInput() {

		tableViewer.get().setInput(channelMappings);
		for(Listener listener : listeners) {
			listener.handleEvent(new Event());
		}
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return DELETE;
			}

			@Override
			public String getCategory() {

				return CATEGORY;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteItems(shell);
			}
		});
	}

	private void addKeyEventProcessors(Shell shell, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					deleteItems(shell);
				}
			}
		});
	}

	private void deleteItems(Shell shell) {

		if(MessageDialog.openQuestion(shell, DIALOG_TITLE, MESSAGE_REMOVE)) {
			IStructuredSelection structuredSelection = (IStructuredSelection)tableViewer.get().getSelection();
			for(Object object : structuredSelection.toArray()) {
				if(object instanceof ChannelMapping) {
					channelMappings.remove(object);
				}
			}
			setViewerInput();
		}
	}
}
