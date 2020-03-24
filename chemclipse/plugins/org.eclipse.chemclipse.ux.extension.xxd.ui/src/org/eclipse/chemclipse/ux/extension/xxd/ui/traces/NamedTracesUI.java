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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.NamedTraceInputValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageNamedTraces;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Text;

public class NamedTracesUI extends Composite {

	private ComboViewer comboViewer;
	private Text textTraces;
	private Button buttonAdd;
	private Button buttonDelete;
	private Button buttonSettings;
	//
	private NamedTraces namedTraces = null;
	private NamedTrace namedTrace = null;
	//
	private IUpdateListener updateListener = null;

	public NamedTracesUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void setInput(NamedTraces namedTraces) {

		this.namedTraces = namedTraces;
		updateInput();
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

		setLayout(new GridLayout(5, false));
		//
		comboViewer = createComboViewer(this);
		textTraces = createText(this);
		buttonAdd = createButtonAdd(this);
		buttonDelete = createButtonDelete(this);
		buttonSettings = createButtonSettings(this);
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
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {

				if(namedTrace != null) {
					namedTrace.setTraces(text.getText().trim());
					fireUpdate();
				}
			}
		});
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
							updateInput();
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
						namedTraces.remove((NamedTrace)object);
						updateInput();
						fireUpdate();
					}
				}
			}
		});
		return button;
	}

	private Button createButtonSettings(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Settings");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePageNamedTraces()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
		return button;
	}

	private void applySettings() {

		//
	}

	private void updateInput() {

		namedTrace = null;
		if(namedTraces != null) {
			//
			buttonAdd.setEnabled(true);
			//
			List<NamedTrace> traces = new ArrayList<>(namedTraces.values());
			Collections.sort(traces, (t1, t2) -> t1.getIdentifier().compareTo(t2.getIdentifier()));
			//
			Combo combo = comboViewer.getCombo();
			int selectionIndex = combo.getSelectionIndex();
			comboViewer.setInput(traces);
			//
			if(combo.getItemCount() > 0) {
				buttonDelete.setEnabled(true);
				int index = (selectionIndex >= 0 && selectionIndex < combo.getItemCount()) ? selectionIndex : 0;
				combo.select(index);
				namedTrace = traces.get(index);
			} else {
				buttonDelete.setEnabled(false);
			}
		} else {
			buttonAdd.setEnabled(false);
			buttonDelete.setEnabled(false);
			comboViewer.setInput(null);
		}
		//
		updateNamedTrace();
	}

	private void updateNamedTrace() {

		textTraces.setText(namedTrace != null ? namedTrace.getTraces() : "");
		buttonDelete.setEnabled(namedTrace != null);
		buttonSettings.setEnabled(true);
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
