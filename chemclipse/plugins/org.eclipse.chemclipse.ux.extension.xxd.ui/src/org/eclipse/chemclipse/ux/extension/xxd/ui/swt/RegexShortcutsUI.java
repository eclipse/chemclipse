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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.RegexSuggestion;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class RegexShortcutsUI extends Composite {

	private AtomicReference<ComboViewer> comboControl = new AtomicReference<>();
	private AtomicReference<Text> textControl = new AtomicReference<>();

	public RegexShortcutsUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		createComboViewer(this);
		createText(this);
		//
		initialize();
	}

	private void initialize() {

		RegexSuggestion regexSuggestion = RegexSuggestion.DIGIT_ANY;
		comboControl.get().setInput(RegexSuggestion.values());
		comboControl.get().setSelection(new StructuredSelection(regexSuggestion));
		textControl.get().setText(regexSuggestion.command());
	}

	private void createComboViewer(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof RegexSuggestion regexSuggestion) {
					return regexSuggestion.label();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select the suggestion.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof RegexSuggestion regexSuggestion) {
					textControl.get().setText(regexSuggestion.command());
				} else {
					textControl.get().setText("");
				}
			}
		});
		//
		comboControl.set(comboViewer);
	}

	private void createText(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("The regular expression command is displayed here.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		textControl.set(text);
	}
}