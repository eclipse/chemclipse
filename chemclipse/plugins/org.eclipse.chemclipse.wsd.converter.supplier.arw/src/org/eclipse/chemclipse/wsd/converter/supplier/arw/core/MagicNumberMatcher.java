/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.arw.core;

import java.io.File;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;

public class MagicNumberMatcher extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	private static final byte[] MAGIC_CODE = new byte[]{0x57, 0x61, 0x76, 0x65, 0x6C, 0x65, 0x6E, 0x67, 0x74, 0x68}; // Wavelength

	@Override
	public boolean checkFileFormat(File file) {

		if(checkFileExtension(file, ".arw")) {
			return checkMagicCode(file, MAGIC_CODE);
		}
		//
		return false;
	}
}
