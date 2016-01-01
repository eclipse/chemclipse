/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.quantitation;

import org.eclipse.chemclipse.model.quantitation.AbstractQuantitationEntry;

public abstract class AbstractQuantitationEntryCSD extends AbstractQuantitationEntry implements IQuantitationEntryCSD {

	public AbstractQuantitationEntryCSD(String name, double concentration, String concentrationUnit, double area) {
		super(name, concentration, concentrationUnit, area);
	}
}
