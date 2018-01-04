/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import org.eclipse.chemclipse.msd.model.core.IIonTransition;

public class MarkedIonTransition implements IMarkedIonTransition {

	private IIonTransition ionTransition;
	private boolean isSelected;

	public MarkedIonTransition(IIonTransition ionTransition) {
		this.ionTransition = ionTransition;
	}

	@Override
	public IIonTransition getIonTransition() {

		return ionTransition;
	}

	@Override
	public boolean isSelected() {

		return isSelected;
	}

	@Override
	public void setSelected(boolean isSelected) {

		this.isSelected = isSelected;
	}
}
