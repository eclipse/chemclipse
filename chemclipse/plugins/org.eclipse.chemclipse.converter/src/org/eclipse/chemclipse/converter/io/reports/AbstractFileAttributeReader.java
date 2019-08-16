/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io.reports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import org.eclipse.chemclipse.converter.model.reports.IFileAttributes;

public abstract class AbstractFileAttributeReader {

	public void setFileAttributes(File file, IFileAttributes fileAttributes) throws IOException {

		Path path = Paths.get(file.toURI());
		BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
		fileAttributes.setFileName(file.getName());
		fileAttributes.setCanonicalPath(file.getCanonicalPath());
		fileAttributes.setCreationTime(basicFileAttributes.creationTime().toMillis());
		fileAttributes.setLastAccessTime(basicFileAttributes.lastAccessTime().toMillis());
		fileAttributes.setLastModificationTime(file.lastModified());
	}
}
