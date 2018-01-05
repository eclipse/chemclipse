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

public abstract class AbstractFileAttributes implements IFileAttributes {

	private String fileName = "";
	private String canonicalPath = "";
	private long creationTime = 0;
	private long lastAccessTime = 0;
	private long lastModificationTime = 0;

	@Override
	public String getFileName() {

		return fileName;
	}

	@Override
	public void setFileName(String fileName) {

		this.fileName = fileName;
	}

	@Override
	public String getCanonicalPath() {

		return canonicalPath;
	}

	@Override
	public void setCanonicalPath(String canonicalPath) {

		this.canonicalPath = canonicalPath;
	}

	@Override
	public long getCreationTime() {

		return creationTime;
	}

	@Override
	public void setCreationTime(long creationTime) {

		this.creationTime = creationTime;
	}

	@Override
	public long getLastAccessTime() {

		return lastAccessTime;
	}

	@Override
	public void setLastAccessTime(long lastAccessTime) {

		this.lastAccessTime = lastAccessTime;
	}

	@Override
	public long getLastModificationTime() {

		return lastModificationTime;
	}

	@Override
	public void setLastModificationTime(long lastModificationTime) {

		this.lastModificationTime = lastModificationTime;
	}
}
