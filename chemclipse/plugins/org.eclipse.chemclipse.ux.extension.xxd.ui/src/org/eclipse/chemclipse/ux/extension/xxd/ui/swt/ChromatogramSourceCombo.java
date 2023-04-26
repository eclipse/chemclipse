/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramSourceCombo extends Composite {

	private static final String SOURCE_REFERENCES = "Internal (This Chromatogram)";
	private static final String SOURCE_EDITORS = "External (Editor Chromatogram)";
	//
	private List<String> sources = new ArrayList<String>();
	private ComboViewer comboViewer;

	public ChromatogramSourceCombo(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	public boolean isSourceReferences() {

		return SOURCE_REFERENCES.equals(comboViewer.getCombo().getText());
	}

	public boolean isSourceEditors() {

		return SOURCE_EDITORS.equals(comboViewer.getCombo().getText());
	}

	public Combo getCombo() {

		return comboViewer.getCombo();
	}

	private void initialize() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		//
		comboViewer = createComboViewer(composite);
	}

	private ComboViewer createComboViewer(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ListContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof String) {
					return (String)element;
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select the chromatogram destination.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 200;
		combo.setLayoutData(gridData);
		sources.add(SOURCE_REFERENCES);
		sources.add(SOURCE_EDITORS);
		comboViewer.setInput(sources);
		//
		return comboViewer;
	}
}
