/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime;

import java.io.File;
import java.io.FilenameFilter;

public class HLMFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {

		if(name.endsWith(".HLM") || name.endsWith(".hlm")) {
			return true;
		} else {
			return false;
		}
	}
}
