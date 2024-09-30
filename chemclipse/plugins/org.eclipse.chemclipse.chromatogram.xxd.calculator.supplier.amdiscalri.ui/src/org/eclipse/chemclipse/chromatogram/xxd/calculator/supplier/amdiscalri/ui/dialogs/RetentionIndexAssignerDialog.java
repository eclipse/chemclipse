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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.dialogs;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexAssigner;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.RetentionIndexAssignerEditor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class RetentionIndexAssignerDialog extends Dialog {

	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 400;
	private RetentionIndexAssigner retentionIndexAssigner;

	public RetentionIndexAssignerDialog(Shell parent) {

		super(parent);
	}

	public void setRetentionIndexAssigner(RetentionIndexAssigner retentionIndexAssigner) {

		this.retentionIndexAssigner = retentionIndexAssigner;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText("Retention Index Assigner");
	}

	@Override
	protected Point getInitialSize() {

		return new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	@Override
	protected boolean isResizable() {

		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		composite.setLayout(new FillLayout());
		//
		RetentionIndexAssignerEditor retentionIndexAssignerEditor = new RetentionIndexAssignerEditor(composite, SWT.NONE);
		retentionIndexAssignerEditor.setRetentionIndexMarker(retentionIndexAssigner);
		//
		return composite;
	}
}