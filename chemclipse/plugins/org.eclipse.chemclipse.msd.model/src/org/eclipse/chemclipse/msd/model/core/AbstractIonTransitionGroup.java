/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractIonTransitionGroup implements IIonTransitionGroup {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -1011943566574470391L;
	private List<IIonTransition> ionTransitions;

	protected AbstractIonTransitionGroup() {

		ionTransitions = new ArrayList<>();
	}

	@Override
	public void add(IIonTransition ionTransition) {

		ionTransitions.add(ionTransition);
	}

	@Override
	public void remove(IIonTransition ionTransition) {

		ionTransitions.remove(ionTransition);
	}

	@Override
	public boolean contains(IIonTransition ionTransition) {

		return ionTransitions.contains(ionTransition);
	}

	@Override
	public IIonTransition get(int index) {

		if(size() > index) {
			return ionTransitions.get(index);
		} else {
			return null;
		}
	}

	@Override
	public IIonTransition get(IIonTransition ionTransition) {

		int index = ionTransitions.indexOf(ionTransition);
		if(index >= 0) {
			return ionTransitions.get(index);
		} else {
			return null;
		}
	}

	@Override
	public List<IIonTransition> getIonTransitions() {

		return ionTransitions;
	}

	@Override
	public int size() {

		return ionTransitions.size();
	}
}
