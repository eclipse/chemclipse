/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.quantitation;

import java.io.File;

import org.eclipse.chemclipse.converter.core.IFileContentMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.processing.converter.ISupplier;

public class QuantDBSupplier implements IQuantDBSupplierSetter {

	private String id = "";
	private String description = "";
	private String filterName = "";
	private String fileExtension = "";
	private String fileName = "";
	private boolean exportable = false;
	private boolean importable = false;
	private IMagicNumberMatcher magicNumberMatcher = null;
	private IFileContentMatcher fileContentMatcher = null;

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

		String extension = fileExtension;
		if(fileExtension != null) {
			if(!"".equals(fileExtension)) {
				extension = fileExtension.startsWith(".") ? fileExtension : "." + fileExtension;
			}
			this.fileExtension = extension;
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

	public IFileContentMatcher getFileContentMatcher() {

		return fileContentMatcher;
	}

	@Override
	public boolean isMatchContent(File file) {

		if(fileContentMatcher != null) {
			return fileContentMatcher.checkFileFormat(file);
		}
		return false;
	}

	@Override
	public void setFileContentMatcher(IFileContentMatcher fileContentMatcher) {

		this.fileContentMatcher = fileContentMatcher;
	}

	@Override
	public String getDirectoryExtension() {

		return "";
	}

	@Override
	public boolean isExportable() {

		return exportable;
	}

	public void setExportable(final boolean isExportable) {

		this.exportable = isExportable;
	}

	@Override
	public boolean isImportable() {

		return importable;
	}

	public void setImportable(final boolean isImportable) {

		this.importable = isImportable;
	}

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
		return id.equals(other.getId()) && //
				description.equals(other.getDescription()) && //
				filterName.equals(other.getFilterName()) && //
				fileExtension.equals(other.getFileExtension()) && //
				fileName.equals(other.getFileName());
	}

	@Override
	public int hashCode() {

		return id.hashCode() + //
				description.hashCode() + //
				filterName.hashCode() + //
				fileExtension.hashCode() + //
				fileName.hashCode();
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
		builder.append("]");
		return builder.toString();
	}
}
