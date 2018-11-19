/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.support;

import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;

public class ChromatogramSupport {

	private static ISupplierFileIdentifier chromatogramIdentifier;
	private static ISupplierEditorSupport chromatogramEditorSupport;

	private ChromatogramSupport() {
	}

	public static ISupplierFileIdentifier getInstanceIdentifier() {

		if(chromatogramIdentifier == null) {
			chromatogramIdentifier = new ChromatogramIdentifier();
		}
		return chromatogramIdentifier;
	}

	public static ISupplierEditorSupport getInstanceEditorSupport() {

		if(chromatogramEditorSupport == null) {
			chromatogramEditorSupport = new ChromatogramEditorSupport();
		}
		return chromatogramEditorSupport;
	}
}
