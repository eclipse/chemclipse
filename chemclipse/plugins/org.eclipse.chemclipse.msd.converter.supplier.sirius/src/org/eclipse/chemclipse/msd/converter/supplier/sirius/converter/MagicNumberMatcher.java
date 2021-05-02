/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.sirius.converter;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.supplier.sirius.io.SiriusReader;

public class MagicNumberMatcher extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	private static final Logger logger = Logger.getLogger(SiriusReader.class);

	@Override
	public boolean checkFileFormat(File file) {

		if(checkFileExtension(file, ".ms")) {
			return true;
		} else if(checkFileExtension(file, ".zip")) {
			try (ZipFile zipFile = new ZipFile(file)) {
				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while(entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if(entry.getName().endsWith(".ms")) {
						return true;
					}
				}
			} catch(IllegalArgumentException e) {
				logger.warn("Non UTF-8 entry in " + file.getName(), e);
			} catch(IOException e) {
				logger.warn(e);
			}
		}
		//
		return false;
	}
}
