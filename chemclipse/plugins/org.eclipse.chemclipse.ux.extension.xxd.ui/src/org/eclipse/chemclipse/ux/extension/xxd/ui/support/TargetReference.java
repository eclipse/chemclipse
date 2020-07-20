/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.core.Classifiable;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings.LibraryField;
import org.eclipse.core.runtime.Adapters;

public interface TargetReference extends ITargetSupplier {

	/**
	 * 
	 * @return the display name of this reference
	 */
	String getName();

	/**
	 * 
	 * @return a persistent unique ID that could be used to reference this target
	 */
	String getID();

	/**
	 * 
	 * @return the type that was identified for example peak/scan/...
	 */
	String getType();

	/**
	 * 
	 * @return the best matching target from this reference
	 */
	default IIdentificationTarget getBestTarget() {

		return IIdentificationTarget.getBestIdentificationTarget(getTargets());
	}

	default String getTargetLabel(LibraryField libraryField) {

		if(libraryField == LibraryField.NAME) {
			IPeak peak = Adapters.adapt(this, IPeak.class);
			if(peak != null) {
				String name = peak.getName();
				if(name != null) {
					return name;
				}
				return name;
			}
		}
		//
		if(libraryField == LibraryField.CLASSIFICATION) {
			IPeak peak = Adapters.adapt(this, IPeak.class);
			if(peak != null) {
				Set<String> set = new LinkedHashSet<>();
				Collection<String> classifier = peak.getClassifier();
				set.addAll(classifier);
				IIdentificationTarget bestTarget = getBestTarget();
				if(bestTarget != null) {
					ILibraryInformation libraryInformation = bestTarget.getLibraryInformation();
					if(libraryInformation != null) {
						set.addAll(libraryInformation.getClassifier());
					}
				}
				return Classifiable.asString(set);
			}
		}
		//
		IIdentificationTarget bestTarget = getBestTarget();
		if(bestTarget != null) {
			return libraryField.stringTransformer().apply(bestTarget);
		}
		//
		return null;
	}
}
