/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("rawtypes")
public class ChromatogramReferenceDialog extends Dialog {

	private IChromatogramSelection chromatogramSelection = null;
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();

	public ChromatogramReferenceDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {

		super.configureShell(newShell);
		newShell.setText("Chromatogram Reference");
	}

	public IChromatogramSelection getChromatogramSelection() {

		return chromatogramSelection;
	}

	@Override
	protected Point getInitialSize() {

		return new Point(450, 150);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, false);
		layout.marginRight = 10;
		layout.marginLeft = 10;
		composite.setLayout(layout);
		//
		createLabel(composite);
		createComboViewerSink(composite);
		//
		return composite;
	}

	private void createLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Select a chromatogam as a new reference.");
	}

	private void createComboViewerSink(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ListContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IChromatogramSelection) {
					IChromatogramSelection chromatogramSelection = (IChromatogramSelection)element;
					String name = chromatogramSelection.getChromatogram().getName();
					String type = chromatogramDataSupport.getChromatogramType(chromatogramSelection);
					return getChromatogramLabel(name, type, "Editor");
				}
				return null;
			}
		});
		combo.setToolTipText("Select a new reference chromatogram.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IChromatogramSelection) {
					chromatogramSelection = (IChromatogramSelection)object;
				}
			}
		});
		//
		comboViewer.setInput(editorUpdateSupport.getChromatogramSelections());
	}

	private String getChromatogramLabel(String name, String type, String defaultName) {

		String label;
		if(name == null || "".equals(name.trim())) {
			label = defaultName + type;
		} else {
			label = name + " " + type;
		}
		//
		return label;
	}
}
