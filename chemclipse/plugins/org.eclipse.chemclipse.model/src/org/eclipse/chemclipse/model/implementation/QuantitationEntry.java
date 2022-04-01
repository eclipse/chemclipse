/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.quantitation.AbstractQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;

public class QuantitationEntry extends AbstractQuantitationEntry implements IQuantitationEntry {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -8475836941611695083L;

	public QuantitationEntry(String name, double concentration, String concentrationUnit, double area) {

		super(name, concentration, concentrationUnit, area);
	}
}