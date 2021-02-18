/*******************************************************************************
 * Copyright (c) 2017, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 * Philip Wenig - identification target comparator
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.comparator.SortOrder;

/**
 * Utility class for {@link ILibraryInformation} related stuff.
 * 
 * @author Alexander Kerner
 *
 */
public class ILibraryInformations {

	public static final Comparator<? super IIdentificationTarget> DEFAULT_TARGET_COMPARATOR = new IdentificationTargetComparator(SortOrder.DESC);

	/**
	 * Same as {@link #selectLibraryInformation(Collection, Comparator)}, using {@link #DEFAULT_TARGET_COMPARATOR}.
	 * 
	 * @param targets
	 *            {@link IIdentificationTarget peak targets} from which one {@link ILibraryInformation} should be selected
	 * @return the selected {@link ILibraryInformation}
	 */
	public static ILibraryInformation selectLibraryInformation(final Collection<? extends IIdentificationTarget> targets) {

		return selectLibraryInformation(targets, DEFAULT_TARGET_COMPARATOR);
	}

	/**
	 * From a collection of {@link IIdentificationTarget peak targets} the best one is selected and it's {@link ILibraryInformation} is returned.
	 * </p>
	 * <i>Best one</i> is defined by given {@link Comparator} and means efficiently <i>lowest one</i>.
	 * 
	 * @param targets
	 *            {@link IIdentificationTarget peak targets} from which one {@link ILibraryInformation} should be selected
	 * @param comparator
	 *            {@link Comparator} that is used to select the <i>lowest peak target</i> from given collection
	 * @return the selected {@link ILibraryInformation}
	 */
	public static ILibraryInformation selectLibraryInformation(final Collection<? extends IIdentificationTarget> targets, final Comparator<? super IIdentificationTarget> comparator) {

		ILibraryInformation libraryInformation = null;
		if(targets != null) {
			final List<IIdentificationTarget> copy = new ArrayList<>(targets);
			Collections.sort(copy, comparator);
			if(targets.size() >= 1) {
				libraryInformation = copy.get(0).getLibraryInformation();
			}
		}
		return libraryInformation;
	}
}
