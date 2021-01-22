/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.ui.core;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
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

public class ChromatogramFilterDialog extends Dialog {

	private static final int WIDTH = 450;
	private static final int HEIGHT = 150;
	//
	private IMassSpectrumIdentifierSupplier massSpectrumIdentifierSupplier = null;

	public ChromatogramFilterDialog(Shell parentShell) {

		super(parentShell);
	}

	public IMassSpectrumIdentifierSupplier getMassSpectrumIdentifierSupplier() {

		return massSpectrumIdentifierSupplier;
	}

	@Override
	protected void configureShell(Shell newShell) {

		super.configureShell(newShell);
		newShell.setText("Scan Maxima Identifier");
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

	private Label createLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Select a scan maximum identifier.");
		return label;
	}

	private ComboViewer createComboViewer(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IMassSpectrumIdentifierSupplier) {
					IMassSpectrumIdentifierSupplier massSpectrumIdentifierSupplier = (IMassSpectrumIdentifierSupplier)element;
					return massSpectrumIdentifierSupplier.getIdentifierName();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a scan maxima identifier.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IMassSpectrumIdentifierSupplier) {
					massSpectrumIdentifierSupplier = (IMassSpectrumIdentifierSupplier)object;
				}
			}
		});
		//
		comboViewer.setInput(MassSpectrumIdentifier.getMassSpectrumIdentifierSupport().getSuppliers());
		return comboViewer;
	}
}
