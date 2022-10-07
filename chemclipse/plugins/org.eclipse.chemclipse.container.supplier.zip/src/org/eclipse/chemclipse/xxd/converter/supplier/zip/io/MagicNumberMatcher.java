/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 *
 * All rights reserved.
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add magic code
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.zip.io;

import java.io.File;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;

public class MagicNumberMatcher extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	private static final byte[] MAGIC_CODE = new byte[]{(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04};

	@Override
	public boolean checkFileFormat(File file) {

		return checkFileExtension(file, ".zip") && checkMagicCode(file, MAGIC_CODE);
	}
}
