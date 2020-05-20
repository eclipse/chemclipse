/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.instruments;

import org.eclipse.chemclipse.support.util.InstrumentListUtil;

public class Instrument {

	/*
	 * Use an empty string as the default id.
	 */
	private String identifier = "";
	private String name = "";
	private String description = "";

	public Instrument() {

		this("", "Default", "This is the default instrument.");
	}

	public Instrument(String identifier, String name, String description) {

		setIdentifier(identifier);
		setName(name);
		setDescription(description);
	}

	public String getIdentifier() {

		return identifier;
	}

	/**
	 * Only alphanumeric values are allowed.
	 * Non-allowed characters will be removed.
	 * 
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {

		identifier = removeNonAlphaNumericTokens(identifier);
		identifier = removeSeparatorTokens(identifier);
		this.identifier = identifier;
	}

	public String getName() {

		return name;
	}

	/**
	 * Non-allowed characters will be removed.
	 * 
	 * @param description
	 */
	public void setName(String name) {

		this.name = removeSeparatorTokens(name);
	}

	public String getDescription() {

		return description;
	}

	/**
	 * Non-allowed characters will be removed.
	 * 
	 * @param description
	 */
	public void setDescription(String description) {

		this.description = removeSeparatorTokens(description);
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
		Instrument other = (Instrument)obj;
		if(identifier == null) {
			if(other.identifier != null)
				return false;
		} else if(!identifier.equals(other.identifier))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "Instrument [identifier=" + identifier + //
				", name=" + name + //
				", description=" + description + //
				"]";
	}

	private String removeNonAlphaNumericTokens(String value) {

		return value.replaceAll("\\P{Alnum}", "");
	}

	private String removeSeparatorTokens(String value) {

		return value.replaceAll("\\" + InstrumentListUtil.SEPARATOR_ENTRY, "") //
				.replaceAll(InstrumentListUtil.SEPARATOR_TOKEN, "");
	}
}
