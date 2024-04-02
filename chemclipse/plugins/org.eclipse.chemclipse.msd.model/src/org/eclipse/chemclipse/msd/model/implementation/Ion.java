/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;

/**
 * If a new ion type should be implemented, extend the abstract class {@link AbstractIon} and not this class.
 */
public class Ion extends AbstractIon implements IIon {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -1398709539024021636L;

	public Ion(double ion) {

		super(ion);
	}

	public Ion(double ion, float abundance) {

		super(ion, abundance);
	}

	public Ion(IIon ion) {

		super(ion);
	}

	public Ion(double ion, float abundance, IIonTransition ionTransition) {

		super(ion, abundance, ionTransition);
	}

	public Ion(IIon ion, IIonTransition ionTransition) {

		super(ion, ionTransition);
	}
}
