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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("rawtypes")
public class QuantitationDatabase extends HashSet<IQuantitationCompound> implements IQuantitationDatabase {

	private static final long serialVersionUID = 2742894549648464728L;

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
