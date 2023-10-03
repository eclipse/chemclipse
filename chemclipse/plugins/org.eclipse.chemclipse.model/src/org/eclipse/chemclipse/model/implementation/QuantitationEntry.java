/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.quantitation.AbstractQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;

public class QuantitationEntry extends AbstractQuantitationEntry implements IQuantitationEntry {

	private static final long serialVersionUID = 6841560520877661249L;

	public QuantitationEntry(String name, double concentration, String concentrationUnit, double area) {

		this(name, "", concentration, concentrationUnit, area);
	}

	public QuantitationEntry(String name, String group, double concentration, String concentrationUnit, double area) {

		super(name, group, concentration, concentrationUnit, area);
	}
}