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
	public String getUUID() {

		return UUID;
	}

	public void setUUID(String UUID) {

		this.UUID = UUID;
	}

	@Override
	public boolean isFinal() {

		return isReadOnly();
	}

	@Override
	public void setReadOnly(boolean readOnly) {

		if(isFinal() && !readOnly) {
			throw new IllegalStateException("This process method is finalized");
		}
		super.setReadOnly(readOnly);
	}
}
