/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - reuse the DataExplorerUI
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import java.util.Collections;

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.msd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.msd.ui.support.MassSpectrumSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.MultiDataExplorerTreeUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumFileExplorer {

	private MultiDataExplorerTreeUI explorerUI;

	@Inject
	public MassSpectrumFileExplorer(Composite parent) {

		explorerUI = new MultiDataExplorerTreeUI(parent, Activator.getDefault().getPreferenceStore());
		explorerUI.setSupplierFileIdentifier(Collections.singleton(MassSpectrumSupport.getInstanceEditorSupport()));
		explorerUI.expandLastDirectoryPath();
	}

	@Focus
	private void setFocus() {

		explorerUI.setFocus();
	}
}
