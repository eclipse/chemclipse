/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;

public class ProjectExplorerSupportFactory {

	private ISupplierFileIdentifier supplierFileIdentifier;
	private ISupplierEditorSupport supplierEditorSupport;

	public ProjectExplorerSupportFactory(DataType dataType) {
		supplierFileIdentifier = new ProjectExplorerFileIdentifier(dataType);
		supplierEditorSupport = new ProjectExplorerEditorSupport(dataType);
	}

	public ISupplierFileIdentifier getInstanceIdentifier() {

		return supplierFileIdentifier;
	}

	public ISupplierEditorSupport getInstanceEditorSupport() {

		return supplierEditorSupport;
	}
}
