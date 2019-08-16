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
package org.eclipse.chemclipse.converter.model.reports;

public interface IFileAttributes {

	String getFileName();

	void setFileName(String fileName);

	String getCanonicalPath();

	void setCanonicalPath(String canonicalPath);

	long getCreationTime();

	void setCreationTime(long creationTime);

	long getLastAccessTime();

	void setLastAccessTime(long lastAccessTime);

	long getLastModificationTime();

	void setLastModificationTime(long lastModificationTime);
}
