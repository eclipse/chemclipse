/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.wsd.ui.support;

import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramIdentifier;

public class ChromatogramSupport {

	private static IChromatogramIdentifier chromatogramIdentifier;
	private static IChromatogramEditorSupport chromatogramEditorSupport;

	private ChromatogramSupport() {

	}

	public static IChromatogramIdentifier getInstanceIdentifier() {

		if(chromatogramIdentifier == null) {
			chromatogramIdentifier = new ChromatogramIdentifier();
		}
		return chromatogramIdentifier;
	}

	public static IChromatogramEditorSupport getInstanceEditorSupport() {

		if(chromatogramEditorSupport == null) {
			chromatogramEditorSupport = new ChromatogramEditorSupport();
		}
		return chromatogramEditorSupport;
	}
}
