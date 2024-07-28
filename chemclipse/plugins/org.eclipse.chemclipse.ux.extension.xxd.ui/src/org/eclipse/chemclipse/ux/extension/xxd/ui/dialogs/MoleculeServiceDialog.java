/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs;

import org.eclipse.chemclipse.swt.ui.services.IMoleculeImageService;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.MoleculeImageServiceSupport;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class MoleculeServiceDialog extends Dialog {

	private IMoleculeImageService moleculeImageService = MoleculeImageServiceSupport.getMoleculeImageServiceSelection();

	public MoleculeServiceDialog(Shell shell) {

		super(shell);
	}

	public IMoleculeImageService getMoleculeImageService() {

		return moleculeImageService;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText("Molecule Service");
	}

	@Override
	protected Point getInitialSize() {

		return new Point(500, 150);
	}

	@Override
	protected boolean isResizable() {

		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, true));
		//
		createComboViewerMoleculeService(composite);
		//
		return composite;
	}

	private void createComboViewerMoleculeService(Composite parent) {

		ComboViewer comboViewer = MoleculeImageServiceSupport.createComboViewerServices(parent);
		Combo combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboViewer.getStructuredSelection().getFirstElement() instanceof IMoleculeImageService service) {
					moleculeImageService = service;
				} else {
					moleculeImageService = null;
				}
			}
		});
	}
}