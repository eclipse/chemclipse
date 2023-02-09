/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ResumeMethodDialog extends TitleAreaDialog {

	private ComboViewer comboViewer;
	private Label labelDescription;
	private int resumeIndex = 0;
	private ProcessEntryContainer container;

	public ResumeMethodDialog(Shell shell) {

		super(shell);
	}

	public void setInput(ProcessEntryContainer container) {

		this.container = container;
	}

	@Override
	public void create() {

		super.create();
		setTitle(ExtensionMessages.processMethod);
		setMessage(ExtensionMessages.resumeProcessMethodAtEntry, IMessageProvider.INFORMATION);
		getButton(IDialogConstants.CANCEL_ID).setEnabled(true);
		getButton(IDialogConstants.OK_ID).setText("Process");
	}

	public int getResumeIndex() {

		return resumeIndex;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite control = (Composite)super.createDialogArea(parent);
		Composite composite = new Composite(control, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));
		//
		createLabel(composite, ExtensionMessages.resumeFollowingEntryDefaultComplete);
		comboViewer = createComboViewerProcessEntry(composite);
		labelDescription = createLabel(composite, "");
		createButtonResumeMethodOption(composite);
		//
		initialize();
		//
		return control;
	}

	private void initialize() {

		List<Object> processEntries = new ArrayList<>();
		processEntries.add(ExtensionMessages.completeMethod);
		//
		if(container != null) {
			for(IProcessEntry processEntry : container) {
				processEntries.add(processEntry);
			}
		}
		//
		comboViewer.setInput(processEntries);
		comboViewer.getCombo().select(0);
	}

	private Label createLabel(Composite parent, String message) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(message);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return label;
	}

	private ComboViewer createComboViewerProcessEntry(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ListContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IProcessEntry processEntry) {
					return processEntry.getName();
				} else if(element instanceof String text) {
					return text;
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a process entry to resume the method.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object element = comboViewer.getStructuredSelection().getFirstElement();
				if(element instanceof IProcessEntry processEntry) {
					resumeIndex = combo.getSelectionIndex() - 1;
					labelDescription.setText(processEntry.getDescription());
				} else {
					resumeIndex = ProcessEntryContainer.DEFAULT_RESUME_INDEX;
					labelDescription.setText("");
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonResumeMethodOption(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false));
		button.setText(ExtensionMessages.rememberDecisionDontShowAgain);
		button.setToolTipText(ExtensionMessages.revertDecisionInSettings);
		button.setSelection(!Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_SHOW_RESUME_METHOD_DIALOG));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_SHOW_RESUME_METHOD_DIALOG, !button.getSelection());
			}
		});
		//
		return button;
	}
}
