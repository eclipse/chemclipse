/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - accept zipped data
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.massbank.converter;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;

public class MagicNumberMatcher extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		if(file.getName().toLowerCase().endsWith(".zip")) {
			try {
				try (ZipFile zipFile = new ZipFile(file)) {
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while(entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						if(entry.getName().startsWith("MassBank-data-")) {
							return true;
						} else if(entry.getName().endsWith("List_of_Contributors_Prefixes_and_Projects.md")) {
							return true;
						}
					}
				}
			} catch(IOException e) {
				return false;
			}
		}
		return checkFileExtension(file, ".txt");
	}
}
