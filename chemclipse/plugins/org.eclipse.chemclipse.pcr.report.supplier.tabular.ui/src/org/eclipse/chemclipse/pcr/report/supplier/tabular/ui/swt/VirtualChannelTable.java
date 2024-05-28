/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
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

import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.ADD_TOOLTIP;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.DIALOG_TITLE;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.EDIT_TOOLTIP;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.EXPORT_TITLE;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.IMPORT_TITLE;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.MESSAGE_ADD;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.MESSAGE_EDIT;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.MESSAGE_EXPORT_FAILED;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.MESSAGE_EXPORT_SUCCESSFUL;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.MESSAGE_REMOVE;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.MESSAGE_REMOVE_ALL;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.REMOVE_ALL_TOOLTIP;
import static org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor.REMOVE_TOOLTIP;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.pcr.report.supplier.tabular.csv.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.VirtualChannel;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.VirtualChannels;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.internal.provider.VirtualChannelInputValidator;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class VirtualChannelTable extends Composite implements IChangeListener, IExtendedPartUI {

	private static final String FILTER_EXTENSION = "*.txt";
	private static final String FILTER_NAME = "Virtual Channels (*.txt)";
	private static final String FILE_NAME = "VirtualChannels.txt";
	//
	private static final String CATEGORY = "PCR Report";
	private static final String DELETE = "Delete";
	//
	public static final String EXAMPLE = "Sample | 1&2 | Channel 1 and 2 | 5";
	//
	private AtomicReference<VirtualChannelListUI> tableViewer = new AtomicReference<>();
	//
	private List<Button> buttons = new ArrayList<>();
	private List<Listener> listeners = new ArrayList<>();
	//
	private VirtualChannels virtualChannels = new VirtualChannels();

	public VirtualChannelTable(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public VirtualChannels getVirtualMappings() {

		return virtualChannels;
	}

	public void load(String entries) {

		virtualChannels.load(entries);
		setViewerInput();
	}

	public String save() {

		return virtualChannels.save();
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

		VirtualChannelListUI virtualChannelsListUI = new VirtualChannelListUI(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		virtualChannelsListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		virtualChannelsListUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				setViewerInput();
			}
		});
		Shell shell = virtualChannelsListUI.getTable().getShell();
		ITableSettings tableSettings = virtualChannelsListUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		addKeyEventProcessors(shell, tableSettings);
		virtualChannelsListUI.applySettings(tableSettings);
		tableViewer.set(virtualChannelsListUI);
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(ADD_TOOLTIP);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				InputDialog dialog = new InputDialog(e.display.getActiveShell(), DIALOG_TITLE, MESSAGE_ADD, EXAMPLE, new VirtualChannelInputValidator(virtualChannels));
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue();
					VirtualChannel channel = virtualChannels.extractVirtualChannel(item);
					if(channel != null) {
						virtualChannels.add(channel);
						setViewerInput();
					}
				}
			}
		});
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
				if(object instanceof VirtualChannel channel) {
					VirtualChannels modified = new VirtualChannels();
					modified.addAll(virtualChannels);
					modified.remove(channel);
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), DIALOG_TITLE, MESSAGE_EDIT, virtualChannels.extractSetting(channel), new VirtualChannelInputValidator(modified));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						VirtualChannel settingNew = virtualChannels.extractVirtualChannel(item);
						channel.copyFrom(settingNew);
						setViewerInput();
					}
				}
			}
		});
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
					virtualChannels.clear();
					setViewerInput();
				}
			}
		});
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

				FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText(IMPORT_TITLE);
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFilterPath(PreferenceSupplier.getListPathImport());
				String path = fileDialog.open();
				if(path != null) {
					PreferenceSupplier.setListPathImport(fileDialog.getFilterPath());
					File file = new File(path);
					virtualChannels.importItems(file);
					setViewerInput();
				}
			}
		});
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

				FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
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
					if(virtualChannels.exportItems(file)) {
						MessageDialog.openInformation(e.display.getActiveShell(), EXPORT_TITLE, MESSAGE_EXPORT_SUCCESSFUL);
					} else {
						MessageDialog.openWarning(e.display.getActiveShell(), EXPORT_TITLE, MESSAGE_EXPORT_FAILED);
					}
				}
			}
		});
		return button;
	}

	private void setViewerInput() {

		tableViewer.get().setInput(virtualChannels);
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
				if(object instanceof VirtualChannel) {
					virtualChannels.remove(object);
				}
			}
			setViewerInput();
		}
	}
}
