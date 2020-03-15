/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Composite;

public class DataExplorerPart {

	private DataExplorerUI dataExplorerUI;
	@Inject
	private IEventBroker broker;
	@Inject
	private IEclipseContext context;

	@PostConstruct
	public void postConstruct(Composite parent) {

		dataExplorerUI = new DataExplorerUI(parent, broker, Activator.getDefault().getPreferenceStore());
		dataExplorerUI.setSupplierFileIdentifier(Collections.singletonList(new SupplierEditorSupport(DataType.MSD, () -> context)));
		dataExplorerUI.expandLastDirectoryPath();
	}
}