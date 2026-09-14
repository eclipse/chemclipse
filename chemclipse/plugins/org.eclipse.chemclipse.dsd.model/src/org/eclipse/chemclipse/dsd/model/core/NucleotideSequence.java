/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.dsd.model.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class NucleotideSequence {

	private List<Nucleobase> nucleobases = new ArrayList<>();

	public List<Nucleobase> getNucleobases() {

		return nucleobases;
	}

	public void fromScans(List<IScan> scans) {

		nucleobases.clear();
		for(IScan scan : scans) {
			IIdentificationTarget bestTarget = getIdentificationTarget(scan);
			if(bestTarget == null) {
				continue;
			}
			Nucleobase nucleobase = getNucleobase(bestTarget);
			if(nucleobase != null) {
				nucleobases.add(nucleobase);
			}
		}
	}

	@Override
	public String toString() {

		StringBuilder result = new StringBuilder(nucleobases.size());

		for(Nucleobase nucleotide : nucleobases) {
			result.append(nucleotide.letter());
		}

		return result.toString();
	}

	private static IIdentificationTarget getIdentificationTarget(IScan scan) {

		Set<IIdentificationTarget> targets = scan.getTargets();
		if(targets != null && !targets.isEmpty()) {
			IdentificationTargetComparator comparator = new IdentificationTargetComparator(SortOrder.DESC);
			return IIdentificationTarget.getIdentificationTarget(targets, comparator);
		}

		return null;
	}

	/**
	 * A base call is stored as an identification target whose library name is the single letter of the nucleobase.
	 *
	 * @param target
	 * @return {@link Nucleobase} or null if the target is no base call
	 */
	public static Nucleobase getNucleobase(IIdentificationTarget target) {

		String name = target.getLibraryInformation().getName();
		if(name.length() == 1) {
			char letter = name.charAt(0);
			return Nucleobase.of(letter);
		}
		return null;
	}
}
