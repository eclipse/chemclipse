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
package org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
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
public class ChromatogramEditorDialog extends Dialog {

	private static final int WIDTH = 450;
	private static final int HEIGHT = 150;
	//
	private IChromatogram chromatogramMaster = null;
	private IChromatogramSelection chromatogramSelection = null;
	//
	private ComboViewer comboViewer;
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();

	public ChromatogramEditorDialog(Shell parentShell) {

		this(parentShell, null);
	}

	/**
	 * If the chromatogramMaster is null, all editor references are listed.
	 * Otherwise, the master will be skipped.
	 * 
	 * @param parentShell
	 * @param chromatogramMaster
	 */
	public ChromatogramEditorDialog(Shell parentShell, IChromatogram chromatogramMaster) {

		super(parentShell);
		this.chromatogramMaster = chromatogramMaster;
	}

	@Override
	protected void configureShell(Shell newShell) {

		super.configureShell(newShell);
		newShell.setText("Chromatogram Editor");
	}

	@Override
	protected boolean isResizable() {

		return true;
	}

	public IChromatogramSelection getChromatogramSelection() {

		return chromatogramSelection;
	}

	@Override
	protected Point getInitialSize() {

		return new Point(WIDTH, HEIGHT);
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
		createComboViewer(composite);
		//
		return composite;
	}

	private void createLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Select an editor chromatogram.");
	}

	private void createComboViewer(Composite parent) {

		comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ListContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IChromatogramSelection chromatogramSelection) {
					String name = chromatogramSelection.getChromatogram().getName();
					String type = ChromatogramDataSupport.getChromatogramType(chromatogramSelection);
					return getChromatogramLabel(name, type, "Editor");
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a chromatogram.");
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
		updateComboViewer(chromatogramMaster);
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

	private void updateComboViewer(IChromatogram chromatogramMaster) {

		List<IChromatogramSelection> chromatogramSelections = editorUpdateSupport.getChromatogramSelections();
		if(chromatogramMaster != null) {
			List<IChromatogramSelection> removeSelections = new ArrayList<>();
			for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
				if(chromatogramSelection.getChromatogram() == chromatogramMaster) {
					removeSelections.add(chromatogramSelection);
				}
			}
			chromatogramSelections.removeAll(removeSelections);
		}
		comboViewer.setInput(chromatogramSelections);
	}
}
