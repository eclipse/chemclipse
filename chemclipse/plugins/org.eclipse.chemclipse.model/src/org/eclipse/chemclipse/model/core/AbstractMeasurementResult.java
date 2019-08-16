/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

public abstract class AbstractMeasurementResult implements IMeasurementResult {

	private String name = "";
	private String identifier = "";
	private String description = "";
	private Object result = null;

	public AbstractMeasurementResult(String name, String identifier, String description, Object result) {
		this.name = name;
		this.identifier = identifier;
		this.description = description;
		this.result = result;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public String getIdentifier() {

		return identifier;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public Object getResult() {

		return result;
	}

	@Override
	public String toString() {

		return "AbstractMeasurementResult [name=" + name + ", identifier=" + identifier + ", description=" + description + ", result=" + result + "]";
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
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
		AbstractMeasurementResult other = (AbstractMeasurementResult)obj;
		if(identifier == null) {
			if(other.identifier != null)
				return false;
		} else if(!identifier.equals(other.identifier))
			return false;
		return true;
	}
}
