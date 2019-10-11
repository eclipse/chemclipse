/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add equals method, add copy constructor, extend for new methods
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import java.io.File;

import org.eclipse.chemclipse.processing.methods.IProcessMethod;

public class ProcessMethod extends ListProcessEntryContainer implements IProcessMethod {

	private String UUID = java.util.UUID.randomUUID().toString();
	private String operator;
	private String description;
	private String name;
	private String category;
	private File sourceFile;
	private boolean readonly = false;

	public ProcessMethod() {
	}

	/**
	 * Copies all data from other into a new process method. This does <b>NOT</b> copies the UUID but generates a new one!
	 * 
	 * @param other
	 */
	public ProcessMethod(IProcessMethod other) {
		if(other != null) {
			this.operator = other.getOperator();
			this.description = other.getDescription();
			this.category = other.getCategory();
			this.name = other.getName();
			other.forEach(otherEntry -> getEntries().add(new ProcessEntry(otherEntry, this)));
		}
	}

	@Override
	public String getOperator() {

		if(operator == null) {
			return "";
		}
		return operator;
	}

	public void setOperator(String operator) {

		this.operator = operator;
	}

	@Override
	public String getDescription() {

		if(description == null) {
			return "";
		}
		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public String getName() {

		if(name == null || name.isEmpty()) {
			if(sourceFile != null) {
				return sourceFile.getName();
			}
			return "";
		}
		return name;
	}

	public void setName(String name) throws IllegalStateException {

		this.name = name;
	}

	@Override
	public String getCategory() {

		if(category == null) {
			return "";
		}
		return category;
	}

	public void setCategory(String category) throws IllegalStateException {

		this.category = category;
	}

	public void setSourceFile(File sourceFile) {

		this.sourceFile = sourceFile;
	}

	public File getSourceFile() {

		return sourceFile;
	}

	@Override
	public boolean isReadOnly() {

		return readonly;
	}

	public void setReadonly(boolean readonly) {

		this.readonly = readonly;
	}

	@Override
	public String getUUID() {

		return UUID;
	}

	public void setUUID(String UUID) {

		this.UUID = UUID;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((UUID == null) ? 0 : UUID.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + (readonly ? 1231 : 1237);
		result = prime * result + ((sourceFile == null) ? 0 : sourceFile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		if(!super.equals(obj)) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		ProcessMethod other = (ProcessMethod)obj;
		if(UUID == null) {
			if(other.UUID != null) {
				return false;
			}
		} else if(!UUID.equals(other.UUID)) {
			return false;
		}
		if(category == null) {
			if(other.category != null) {
				return false;
			}
		} else if(!category.equals(other.category)) {
			return false;
		}
		if(description == null) {
			if(other.description != null) {
				return false;
			}
		} else if(!description.equals(other.description)) {
			return false;
		}
		if(name == null) {
			if(other.name != null) {
				return false;
			}
		} else if(!name.equals(other.name)) {
			return false;
		}
		if(operator == null) {
			if(other.operator != null) {
				return false;
			}
		} else if(!operator.equals(other.operator)) {
			return false;
		}
		if(readonly != other.readonly) {
			return false;
		}
		if(sourceFile == null) {
			if(other.sourceFile != null) {
				return false;
			}
		} else if(!sourceFile.equals(other.sourceFile)) {
			return false;
		}
		return true;
	}
}
