/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - use DataExplorerUI
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import java.util.Collections;

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.msd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.msd.ui.support.DatabaseSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumLibraryExplorer {

	private DataExplorerUI explorerUI;

	@Inject
	public MassSpectrumLibraryExplorer(Composite parent) {

		explorerUI = new DataExplorerUI(parent, Activator.getDefault().getPreferenceStore());
		explorerUI.setSupplierFileIdentifier((Collections.singleton(DatabaseSupport.getInstanceEditorSupport())));
		explorerUI.expandLastDirectoryPath();
	}

	@Focus
	private void setFocus() {

		explorerUI.setFocus();
	}
}
