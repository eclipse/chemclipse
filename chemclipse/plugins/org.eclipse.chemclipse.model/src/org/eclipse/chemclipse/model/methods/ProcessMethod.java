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
 * Christoph Läubrich - add equals method, add copy constructor
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import java.util.ArrayList;

public class ProcessMethod extends ArrayList<IProcessEntry> implements IProcessMethod {

	private static final long serialVersionUID = 1143302899750627448L;
	//
	private String operator = "";
	private String description = "";

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
			addAll(other);
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
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		ProcessMethod other = (ProcessMethod)obj;
		if(description == null) {
			if(other.description != null)
				return false;
		} else if(!description.equals(other.description))
			return false;
		if(operator == null) {
			if(other.operator != null)
				return false;
		} else if(!operator.equals(other.operator))
			return false;
		return true;
	}
}
