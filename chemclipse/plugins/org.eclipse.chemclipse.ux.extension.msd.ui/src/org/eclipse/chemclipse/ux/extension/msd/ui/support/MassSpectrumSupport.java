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
package org.eclipse.chemclipse.ux.extension.msd.ui.support;

import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;

public class MassSpectrumSupport {

	private static ISupplierFileIdentifier massSpectrumIdentifier;
	private static ISupplierFileEditorSupport massSpectrumEditorSupport;

	private MassSpectrumSupport() {
	}

	public static ISupplierFileIdentifier getInstanceIdentifier() {

		if(massSpectrumIdentifier == null) {
			massSpectrumIdentifier = new MassSpectrumIdentifier();
		}
		return massSpectrumIdentifier;
	}

	public static ISupplierFileEditorSupport getInstanceEditorSupport() {

		if(massSpectrumEditorSupport == null) {
			massSpectrumEditorSupport = new MassSpectrumEditorSupport();
		}
		return massSpectrumEditorSupport;
	}
}
