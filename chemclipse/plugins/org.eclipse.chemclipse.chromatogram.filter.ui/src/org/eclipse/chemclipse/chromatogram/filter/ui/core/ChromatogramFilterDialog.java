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
package org.eclipse.chemclipse.chromatogram.filter.ui.core;

import org.eclipse.chemclipse.chromatogram.filter.ui.Activator;
import org.eclipse.chemclipse.chromatogram.filter.ui.l10n.Messages;
import org.eclipse.chemclipse.numeric.services.IMaximaDetectorService;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
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
	private IMaximaDetectorService maximaDetectorService = null;

	public ChromatogramFilterDialog(Shell parentShell) {

		super(parentShell);
	}

	public IMaximaDetectorService getMaximaDetectorService() {

		return maximaDetectorService;
	}

	@Override
	protected void configureShell(Shell newShell) {

		super.configureShell(newShell);
		newShell.setText(Messages.scanMaximaDetectors);
	}

	@Override
	protected boolean isResizable() {

		return true;
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
		label.setText(Messages.selectScanMaximaDetector);
		return label;
	}

	private ComboViewer createComboViewer(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IMaximaDetectorService service) {
					return service.getName();
				}
				return null;
			}
		});
		//
		combo.setToolTipText(Messages.selectScanMaximaDetector);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IMaximaDetectorService service) {
					maximaDetectorService = service;
				}
			}
		});
		//
		comboViewer.setInput(Activator.getDefault().getMaximaDetectorServices());
		return comboViewer;
	}
}
