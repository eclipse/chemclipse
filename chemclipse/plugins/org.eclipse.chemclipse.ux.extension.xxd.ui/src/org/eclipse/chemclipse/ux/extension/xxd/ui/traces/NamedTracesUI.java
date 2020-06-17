/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.traces;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.traces.NamedTrace;
import org.eclipse.chemclipse.model.traces.NamedTraces;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.NamedTraceInputValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.TraceValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

public class NamedTracesUI extends Composite {

	private static final String TOOLTIP_TEXT = "Enter/modify the traces.";
	//
	public static final String IMPORT_TITLE = "Import Named Trace(s)";
	public static final String EXPORT_TITLE = "Export Named Trace(s)";
	public static final String MESSAGE_IMPORT_SUCCESSFUL = "Named trace(s) have been imported successfully.";
	public static final String MESSAGE_EXPORT_SUCCESSFUL = "Named trace(s) have been exported successfully.";
	public static final String MESSAGE_EXPORT_FAILED = "Failed to export the named trace(s).";
	//
	private static final String FILTER_EXTENSION = "*.txt";
	private static final String FILTER_NAME = "Named Traces (*.txt)";
	private static final String FILE_NAME = "NamedTraces.txt";
	//
	private ComboViewer comboViewer;
	private Text textTraces;
	private Button buttonAdd;
	private Button buttonDelete;
	private Button buttonImport;
	private Button buttonExport;
	//
	private NamedTraces namedTraces = null;
	private NamedTrace namedTrace = null;
	//
	private IUpdateListener updateListener = null;
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public NamedTracesUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(NamedTraces namedTraces) {

		this.namedTraces = namedTraces;
		updateInput(null);
	}

	public void update() {

		super.update();
		updateNamedTrace();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public String[] getItems() {

		return comboViewer.getCombo().getItems();
	}

	public void select(int index) {

		if(index >= 0 && index < getItems().length) {
			comboViewer.getCombo().select(index);
			Object object = comboViewer.getStructuredSelection().getFirstElement();
			if(object instanceof NamedTrace) {
				namedTrace = (NamedTrace)object;
				updateNamedTrace();
			}
		}
	}

	/**
	 * Could be null if none has been set.
	 * 
	 * @return {@link NamedTraces}
	 */
	public NamedTraces getNamedTraces() {

		return namedTraces;
	}

	/**
	 * Could be null if none is selected.
	 * 
	 * @return {@link NamedTrace}
	 */
	public NamedTrace getNamedTrace() {

		return namedTrace;
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(6, false);
		// gridLayout.marginWidth = 0;
		// gridLayout.marginLeft = 0;
		// gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		comboViewer = createComboViewer(this);
		textTraces = createText(this);
		buttonAdd = createButtonAdd(this);
		buttonDelete = createButtonDelete(this);
		buttonImport = createButtonImport(this);
		buttonExport = createButtonExport(this);
	}

	private ComboViewer createComboViewer(Composite composite) {

		ComboViewer comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof NamedTrace) {
					NamedTrace namedTrace = (NamedTrace)element;
					return namedTrace.getIdentifier();
				}
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select a named trace.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof NamedTrace) {
					namedTrace = (NamedTrace)object;
					updateNamedTrace();
					fireUpdate();
				}
			}
		});
		//
		return comboViewer;
	}

	private Text createText(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText(TOOLTIP_TEXT);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		TraceValidator traceValidator = new TraceValidator();
		ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		//
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {

				if(namedTrace != null) {
					if(validate(traceValidator, controlDecoration, text)) {
						namedTrace.setTraces(traceValidator.getTracesAsString());
						fireUpdate();
					}
				}
			}
		});
		//
		//
		return text;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add a new named trace.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(namedTraces != null) {
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), "Named Trace", "Create a new named trace.", "Hydrocarbons | 57 71 85", new NamedTraceInputValidator(namedTraces.keySet()));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						NamedTrace namedTraceNew = namedTraces.extractNamedTrace(item);
						if(namedTraceNew != null) {
							namedTraces.add(namedTraceNew);
							namedTrace = namedTraceNew;
							updateInput(namedTrace.getIdentifier());
							fireUpdate();
						}
					}
				}
			}
		});
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected named trace.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), "Named Trace", "Would you like to delete the selected named trace?")) {
					Object object = comboViewer.getStructuredSelection().getFirstElement();
					if(object instanceof NamedTrace) {
						namedTrace = null;
						namedTraces.remove((NamedTrace)object);
						updateInput(null);
						fireUpdate();
					}
				}
			}
		});
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Import named trace(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(namedTraces != null) {
					FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.READ_ONLY);
					fileDialog.setText(IMPORT_TITLE);
					fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
					fileDialog.setFilterNames(new String[]{FILTER_NAME});
					fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_NAMED_TRACES_TEMPLATE_FOLDER));
					String path = fileDialog.open();
					if(path != null) {
						preferenceStore.putValue(PreferenceConstants.P_NAMED_TRACES_TEMPLATE_FOLDER, fileDialog.getFilterPath());
						File file = new File(path);
						namedTraces.importItems(file);
						MessageDialog.openInformation(e.display.getActiveShell(), IMPORT_TITLE, MESSAGE_IMPORT_SUCCESSFUL);
						updateInput(null);
						fireUpdate();
					}
				}
			}
		});
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Export named trace(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(namedTraces != null) {
					FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
					fileDialog.setOverwrite(true);
					fileDialog.setText(EXPORT_TITLE);
					fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
					fileDialog.setFilterNames(new String[]{FILTER_NAME});
					fileDialog.setFileName(FILE_NAME);
					fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_NAMED_TRACES_TEMPLATE_FOLDER));
					String path = fileDialog.open();
					if(path != null) {
						preferenceStore.putValue(PreferenceConstants.P_NAMED_TRACES_TEMPLATE_FOLDER, fileDialog.getFilterPath());
						File file = new File(path);
						if(namedTraces.exportItems(file)) {
							MessageDialog.openInformation(e.display.getActiveShell(), EXPORT_TITLE, MESSAGE_EXPORT_SUCCESSFUL);
						} else {
							MessageDialog.openWarning(e.display.getActiveShell(), EXPORT_TITLE, MESSAGE_EXPORT_FAILED);
						}
					}
				}
			}
		});
		return button;
	}

	private void updateInput(String identifier) {

		namedTrace = null;
		if(namedTraces != null) {
			/*
			 * Sort the traces by identifier.
			 */
			List<NamedTrace> traces = new ArrayList<>(namedTraces.values());
			Collections.sort(traces, (t1, t2) -> t1.getIdentifier().compareTo(t2.getIdentifier()));
			/*
			 * Populate the combo viewer
			 */
			Combo combo = comboViewer.getCombo();
			int selectionIndex = combo.getSelectionIndex();
			comboViewer.setInput(traces);
			int itemCount = combo.getItemCount();
			/*
			 * Set the last selection if possible.
			 */
			if(itemCount > 0) {
				int index = -1;
				if(identifier == null) {
					index = (selectionIndex >= 0 && selectionIndex < itemCount) ? selectionIndex : 0;
				} else {
					exitloop:
					for(int i = 0; i < traces.size(); i++) {
						if(identifier.equals(traces.get(i).getIdentifier())) {
							index = i;
							break exitloop;
						}
					}
				}
				/*
				 * Set the selected item.
				 */
				if(index >= 0 && index < itemCount) {
					combo.select(index);
					namedTrace = traces.get(index);
				}
			}
			//
			buttonAdd.setEnabled(true);
			buttonDelete.setEnabled(itemCount > 0);
			buttonImport.setEnabled(true);
			buttonExport.setEnabled(true);
		} else {
			/*
			 * Settings
			 */
			buttonAdd.setEnabled(false);
			buttonDelete.setEnabled(false);
			buttonImport.setEnabled(false);
			buttonExport.setEnabled(false);
			comboViewer.setInput(null);
		}
		//
		updateNamedTrace();
	}

	private void updateNamedTrace() {

		textTraces.setText(namedTrace != null ? namedTrace.getTraces() : "");
		buttonDelete.setEnabled(namedTrace != null);
	}

	private boolean validate(IValidator validator, ControlDecoration controlDecoration, Text text) {

		IStatus status = validator.validate(text.getText().trim());
		if(status.isOK()) {
			controlDecoration.hide();
			text.setToolTipText(TOOLTIP_TEXT);
			return true;
		} else {
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(status.getMessage());
			controlDecoration.show();
			text.setToolTipText(status.getMessage());
			return false;
		}
	}

	private void fireUpdate() {

		if(updateListener != null) {
			getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					updateListener.update();
				}
			});
		}
	}
}
