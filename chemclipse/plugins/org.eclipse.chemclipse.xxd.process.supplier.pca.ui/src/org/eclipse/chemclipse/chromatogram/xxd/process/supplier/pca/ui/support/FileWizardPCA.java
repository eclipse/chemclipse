/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.FilesInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.IInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.quickstart.WizardTile;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.widgets.Shell;

public class FileWizardPCA extends WizardTile {

	private File file;

	public FileWizardPCA(File file) {

		this.file = file;
	}

	public void run(Shell shell, IEclipseContext context) throws InvocationTargetException, InterruptedException {

		/*
		 * Somehow, the wizard is displayed and the editor is created.
		 * But then it fails. Nobody knows why.
		 */
		execute(shell, context);
	}

	@Override
	public String getTitle() {

		return "File Wizard PCA";
	}

	@Override
	protected IInputWizard createWizard() {

		return new FilesInputWizard(file);
	}
}