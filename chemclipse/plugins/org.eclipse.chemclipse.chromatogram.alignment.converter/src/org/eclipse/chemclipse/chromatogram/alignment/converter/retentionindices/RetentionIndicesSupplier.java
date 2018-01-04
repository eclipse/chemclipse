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
package org.eclipse.chemclipse.chromatogram.alignment.converter.retentionindices;

public class RetentionIndicesSupplier implements IRetentionIndicesSupplier {

	private String id = "";
	private String description = "";
	private String filterName = "";
	private String fileExtension = "";
	private boolean exportable = false;
	private boolean importable = false;

	public String getId() {

		return id;
	}

	/**
	 * Sets the supplier id like
	 * "org.eclipse.chemclipse.chromatogram.alignment.supplier.amdis".
	 * 
	 * @param id
	 */
	protected void setId(final String id) {

		if(id != null) {
			this.id = id;
		}
	}

	public String getDescription() {

		return description;
	}

	/**
	 * Sets the description of the retention index supplier.
	 * 
	 * @param description
	 */
	protected void setDescription(final String description) {

		if(description != null) {
			this.description = description;
		}
	}

	public String getFilterName() {

		return filterName;
	}

	/**
	 * Set the filter name to be shown in the FileDialog.
	 * 
	 * @param filterName
	 */
	protected void setFilterName(final String filterName) {

		if(filterName != null) {
			this.filterName = filterName;
		}
	}

	public String getFileExtension() {

		return fileExtension;
	}

	/**
	 * Sets the file extension, e.g. AMDIS (.cal). If for example only (cal)
	 * will be stored, a leading point will automatically be added (.cal).
	 * 
	 * @param fileExtension
	 */
	protected void setFileExtension(final String fileExtension) {

		String extension = fileExtension;
		if(fileExtension != null) {
			if(!"".equals(fileExtension)) {
				extension = fileExtension.startsWith(".") ? fileExtension : "." + fileExtension;
			}
			this.fileExtension = extension;
		}
	}

	public boolean isExportable() {

		return exportable;
	}

	/**
	 * Sets whether the retention indices are exportable or not.
	 * 
	 * @param isExportable
	 */
	protected void setExportable(final boolean isExportable) {

		this.exportable = isExportable;
	}

	public boolean isImportable() {

		return importable;
	}

	/**
	 * Sets whether the retention indices are importable or not.
	 * 
	 * @param isImportable
	 */
	protected void setImportable(final boolean isImportable) {

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
		RetentionIndicesSupplier other = (RetentionIndicesSupplier)otherObject;
		return id.equals(other.id) && description.equals(other.getDescription()) && filterName.equals(other.getFilterName()) && fileExtension.equals(other.getFileExtension()) && exportable == other.isExportable() && importable == other.isImportable();
	}

	@Override
	public int hashCode() {

		return id.hashCode() + description.hashCode() + filterName.hashCode() + fileExtension.hashCode() + Boolean.valueOf(exportable).hashCode() + Boolean.valueOf(importable).hashCode();
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
		builder.append(",isExportable=" + this.exportable);
		builder.append(",isImportable=" + this.importable);
		builder.append("]");
		return builder.toString();
	}
}
