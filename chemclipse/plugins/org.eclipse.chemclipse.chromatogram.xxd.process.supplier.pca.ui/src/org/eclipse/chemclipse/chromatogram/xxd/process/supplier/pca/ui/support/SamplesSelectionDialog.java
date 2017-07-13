/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SamplesSelectionDialog {

	private PcaEditor pcaEditor;
	private SamplesSelectionTree samplesSelectionTree;
	private Shell shell;

	public SamplesSelectionDialog(PcaEditor pcaEditor) {
		this.pcaEditor = pcaEditor;
	}

	public void open() {

		if(shell == null || shell.isDisposed()) {
			shell = new Shell(Display.getCurrent(), SWT.ON_TOP | SWT.DIALOG_TRIM | SWT.RESIZE);
			shell.setLayout(new FillLayout());
			samplesSelectionTree = new SamplesSelectionTree(pcaEditor, shell);
			shell.pack();
			shell.open();
		}
	}

	public void update() {

		if(samplesSelectionTree != null) {
			samplesSelectionTree.update();
		}
	}
}
