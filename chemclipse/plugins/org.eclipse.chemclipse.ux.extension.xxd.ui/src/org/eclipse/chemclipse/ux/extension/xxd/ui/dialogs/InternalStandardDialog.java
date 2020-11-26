/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedInternalStandardsUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class InternalStandardDialog extends Dialog {

	private IPeak peak;
	private ExtendedInternalStandardsUI extendedInternalStandardsUI;

	public InternalStandardDialog(Shell shell, IPeak peak) {

		super(shell);
		this.peak = peak;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText("Internal Standard (ISTD)");
	}

	@Override
	protected Point getInitialSize() {

		return new Point(600, 600);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		extendedInternalStandardsUI = new ExtendedInternalStandardsUI(composite, SWT.NONE);
		extendedInternalStandardsUI.update(peak);
		return composite;
	}
}
