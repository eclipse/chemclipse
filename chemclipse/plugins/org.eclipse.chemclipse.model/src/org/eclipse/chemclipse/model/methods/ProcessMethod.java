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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ProcessMethod implements IProcessMethod {

	private String operator = "";
	private String description = "";
	private String name = "";
	private String category = "";
	private List<IProcessEntry> entries = new ArrayList<>();

	public ProcessMethod() {
	}

	/**
	 * Copies all data from other into a new process method
	 * 
	 * @param other
	 */
	public ProcessMethod(IProcessMethod other) {
		if(other != null) {
			this.operator = other.getOperator();
			this.description = other.getDescription();
			this.category = other.getCategory();
			this.name = other.getName();
			other.forEach(entries::add);
		}
	}

	@Override
	public String getOperator() {

		return operator;
	}

	@Override
	public void setOperator(String operator) {

		this.operator = operator;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public Iterator<IProcessEntry> iterator() {

		return Collections.unmodifiableCollection(entries).iterator();
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) throws IllegalStateException {

		this.name = name;
	}

	@Override
	public String getCategory() {

		return category;
	}

	@Override
	public void setCategory(String category) throws IllegalStateException {

		this.category = category;
	}

	@Override
	public void addProcessEntry(IProcessEntry processEntry) throws IllegalStateException {

		entries.add(processEntry);
	}

	@Override
	public void removeProcessEntry(IProcessEntry processEntry) throws IllegalStateException {

		entries.remove(processEntry);
	}

	@Override
	public int getNumberOfEntries() {

		return entries.size();
	}

	/**
	 * 
	 * @return a modifiable view of the entries, handle with care!
	 */
	public List<IProcessEntry> getEntries() {

		return entries;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ProcessMethod other = (ProcessMethod)obj;
		if(category == null) {
			if(other.category != null)
				return false;
		} else if(!category.equals(other.category))
			return false;
		if(description == null) {
			if(other.description != null)
				return false;
		} else if(!description.equals(other.description))
			return false;
		if(entries == null) {
			if(other.entries != null)
				return false;
		} else if(!entries.equals(other.entries))
			return false;
		if(name == null) {
			if(other.name != null)
				return false;
		} else if(!name.equals(other.name))
			return false;
		if(operator == null) {
			if(other.operator != null)
				return false;
		} else if(!operator.equals(other.operator))
			return false;
		return true;
	}
}
