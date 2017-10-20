/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.support;

import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileIdentifier;

public class MassSpectrumDatabaseSupport {

	private static ISupplierFileIdentifier massSpectrumLibraryIdentifier;
	private static ISupplierFileEditorSupport massSpectrumLibraryEditorSupport;

	private MassSpectrumDatabaseSupport() {
	}

	public static ISupplierFileIdentifier getInstanceIdentifier() {

		if(massSpectrumLibraryIdentifier == null) {
			massSpectrumLibraryIdentifier = new MassSpectrumDatabaseIdentifier();
		}
		return massSpectrumLibraryIdentifier;
	}

	public static ISupplierFileEditorSupport getInstanceEditorSupport() {

		if(massSpectrumLibraryEditorSupport == null) {
			massSpectrumLibraryEditorSupport = new MassSpectrumDatabaseEditorSupport();
		}
		return massSpectrumLibraryEditorSupport;
	}
}
