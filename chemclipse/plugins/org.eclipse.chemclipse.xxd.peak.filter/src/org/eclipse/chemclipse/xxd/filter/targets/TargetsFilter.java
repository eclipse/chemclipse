/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.targets;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.xxd.filter.settings.DeleteTargetsFilterSettings;

public class TargetsFilter {

	public static void filter(ITargetSupplier targetSupplier, DeleteTargetsFilterSettings settings) {

		if(targetSupplier != null && settings != null) {
			Set<IIdentificationTarget> targets = targetSupplier.getTargets();
			switch(settings.getTargetDeleteOption()) {
				case ALL_TARGETS:
					targets.clear();
					break;
				case EMPTY_SMILES:
					Set<IIdentificationTarget> delete = new HashSet<>();
					for(IIdentificationTarget target : targets) {
						String smiles = target.getLibraryInformation().getSmiles();
						if("".equals(smiles)) {
							delete.add(target);
						}
					}
					removeTargets(targets, delete);
					break;
			}
		}
	}

	private static void removeTargets(Set<IIdentificationTarget> targets, Set<IIdentificationTarget> delete) {

		targets.removeAll(delete);
	}
}
