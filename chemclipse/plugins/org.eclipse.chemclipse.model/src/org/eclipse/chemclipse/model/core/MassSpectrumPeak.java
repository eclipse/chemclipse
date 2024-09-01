/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

public class MassSpectrumPeak implements IMassSpectrumPeak {

	private double mz;
	private final Set<String> classifier = new LinkedHashSet<>();
	private Set<IIdentificationTarget> identificationTargets = new HashSet<>();

	@Override
	public double getIon() {

		return mz;
	}

	@Override
	public void setIon(double mz) {

		this.mz = mz;
	}

	@Override
	public Set<IIdentificationTarget> getTargets() {

		return identificationTargets;
	}

	@Override
	public Collection<String> getClassifier() {

		return Collections.unmodifiableCollection(classifier);
	}

	@Override
	public void addClassifier(String classifier) {

		if(classifier != null && !classifier.trim().isEmpty()) {
			this.classifier.add(classifier.trim());
		}
	}

	@Override
	public void removeClassifier(String classifier) {

		this.classifier.remove(classifier);
	}
}
