/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.Collection;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IMarkedSignals;

/**
 * This class stores a list of ions which should be considered or not
 * in calculation of the e.g. ITotalIonSignals. The classes {@link ExcludedIons} and {@link MarkedIons} implement the
 * interface.<br/>
 * Why do we not have a method to add accurate ion values with a given precision and a method to remove them?<br/>
 * Because it depends on the given precision if the ion is in the list or not. That's why it's better to create a set of accurate
 * ions and the given precision on the fly.
 * 
 * @author eselmeister
 */
public interface IMarkedIons extends IMarkedSignals<IMarkedIon> {

	Set<Integer> getIonsNominal();

	/**
	 * Adds the ion range with magnification factor = 1.
	 * 
	 * @param ionStart
	 * @param ionStop
	 */
	void add(int ionStart, int ionStop);

	/**
	 * add ions with magnification factor = 1.
	 * 
	 * @param ions
	 */
	void add(int... ions);

	/**
	 * add ions with magnification factor = 1.
	 * 
	 * @param ions
	 */
	void add(Collection<Integer> ions);
}
