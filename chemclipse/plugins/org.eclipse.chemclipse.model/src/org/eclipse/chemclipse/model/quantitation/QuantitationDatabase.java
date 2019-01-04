/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class QuantitationDatabase extends HashSet<IQuantitationCompound> implements IQuantitationDatabase {

	private static final long serialVersionUID = 2742894549648464728L;
	/*
	 * Transient
	 * File and ConverterId are set by the DB converter.
	 */
	private File file = null;
	private String converterId = "";
	//
	private String operator = "";
	private String description = "";

	@Override
	public File getFile() {

		return file;
	}

	@Override
	public void setFile(File file) {

		this.file = file;
	}

	@Override
	public String getConverterId() {

		return converterId;
	}

	@Override
	public void setConverterId(String converterId) {

		this.converterId = converterId;
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
	public List<String> getCompoundNames() {

		List<String> compoundNames = new ArrayList<>();
		for(IQuantitationCompound quantitationCompound : this) {
			compoundNames.add(quantitationCompound.getName());
		}
		return compoundNames;
	}

	@Override
	public IQuantitationCompound getQuantitationCompound(String name) {

		for(IQuantitationCompound quantitationCompound : this) {
			if(quantitationCompound.getName().equals(name)) {
				return quantitationCompound;
			}
		}
		return null;
	}

	@Override
	public boolean containsQuantitationCompund(String name) {

		return (getQuantitationCompound(name) != null) ? true : false;
	}
}
