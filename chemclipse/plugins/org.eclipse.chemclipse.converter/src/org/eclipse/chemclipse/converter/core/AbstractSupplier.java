/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - remove unwanted logging and set extension to empty string if null
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import java.io.File;

public abstract class AbstractSupplier implements ISupplierSetter {

	private String id = "";
	private String description = "";
	private String filterName = "";
	private String fileExtension = "";
	private String fileName = "";
	private String directoryExtension = "";
	private boolean exportable = false;
	private boolean importable = false;
	private IMagicNumberMatcher magicNumberMatcher = null;

	@Override
	public String getId() {

		return id;
	}

	@Override
	public void setId(final String id) {

		if(id != null) {
			this.id = id;
		}
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(final String description) {

		if(description != null) {
			this.description = description;
		}
	}

	@Override
	public String getFilterName() {

		return filterName;
	}

	@Override
	public void setFilterName(final String filterName) {

		if(filterName != null) {
			this.filterName = filterName;
		}
	}

	@Override
	public String getFileExtension() {

		return fileExtension;
	}

	@Override
	public void setFileExtension(final String fileExtension) {

		if(fileExtension == null) {
			this.fileExtension = "";
		} else {
			this.fileExtension = fileExtension;
		}
	}

	@Override
	public String getFileName() {

		return fileName;
	}

	@Override
	public void setFileName(final String fileName) {

		if(fileName != null) {
			this.fileName = fileName;
		}
	}

	@Override
	public String getDirectoryExtension() {

		return directoryExtension;
	}

	@Override
	public void setDirectoryExtension(final String directoryExtension) {

		String extension = directoryExtension;
		if(directoryExtension != null) {
			if(!"".equals(directoryExtension)) {
				extension = directoryExtension.startsWith(".") ? directoryExtension : "." + directoryExtension;
			}
			this.directoryExtension = extension;
		}
	}

	@Override
	public boolean isExportable() {

		return exportable;
	}

	@Override
	public void setExportable(final boolean isExportable) {

		this.exportable = isExportable;
	}

	@Override
	public boolean isImportable() {

		return importable;
	}

	@Override
	public void setImportable(final boolean isImportable) {

		this.importable = isImportable;
	}

	@Override
	public boolean isMatchMagicNumber(File file) {

		if(magicNumberMatcher != null) {
			return magicNumberMatcher.checkFileFormat(file);
		}
		return false;
	}

	@Override
	public void setMagicNumberMatcher(final IMagicNumberMatcher magicNumberMatcher) {

		this.magicNumberMatcher = magicNumberMatcher;
	}

	// -----------------------------------------------equals, hashCode, toString
	@Override
	public boolean equals(final Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		ISupplier other = (ISupplier)otherObject;
		return id.equals(other.getId()) && description.equals(other.getDescription()) && filterName.equals(other.getFilterName()) && fileExtension.equals(other.getFileExtension()) && fileName.equals(other.getFileName()) && directoryExtension.equals(other.getDirectoryExtension()) && exportable == other.isExportable() && importable == other.isImportable();
	}

	@Override
	public int hashCode() {

		return id.hashCode() + description.hashCode() + filterName.hashCode() + fileExtension.hashCode() + fileName.hashCode() + directoryExtension.hashCode() + Boolean.valueOf(exportable).hashCode() + Boolean.valueOf(importable).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("id=" + this.id);
		builder.append(",description=" + this.description);
		builder.append(",filterName=" + this.filterName);
		builder.append(",fileExtension=" + this.fileExtension);
		builder.append(",fileName=" + this.fileName);
		builder.append(",directoryExtension=" + this.directoryExtension);
		builder.append(",isExportable=" + this.exportable);
		builder.append(",isImportable=" + this.importable);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------------------------equals, hashCode, toString
}
