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

import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.DataType;

public class EditorSupportFactory {

	private ISupplierFileIdentifier chromatogramIdentifier;
	private IChromatogramEditorSupport chromatogramEditorSupport;

	public EditorSupportFactory(DataType dataType) {
		chromatogramIdentifier = new ChromatogramIdentifier(dataType);
		chromatogramEditorSupport = new ChromatogramEditorSupport(dataType);
	}

	public ISupplierFileIdentifier getInstanceIdentifier() {

		return chromatogramIdentifier;
	}

	public IChromatogramEditorSupport getInstanceEditorSupport() {

		return chromatogramEditorSupport;
	}
}
