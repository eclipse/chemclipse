/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.PeakTargetTransferUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class TargetTransferDialog extends Dialog {

	
	private IChromatogramSelection<?, ?> chromatogramSelection;

	
	public TargetTransferDialog(Shell shell, IChromatogramSelection<?, ?> chromatogramSelection) {
		super(shell);
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText("Target Transfer");
	}

	@Override
	protected Point getInitialSize() {

		return new Point(600, 300);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		//
		PeakTargetTransferUI peakTargetTransferUI = new PeakTargetTransferUI(composite, SWT.NONE);
		peakTargetTransferUI.update(chromatogramSelection);
		peakTargetTransferUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}
}
