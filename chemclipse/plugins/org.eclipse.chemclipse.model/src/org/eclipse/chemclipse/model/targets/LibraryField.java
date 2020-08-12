/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import java.util.function.Function;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

public enum LibraryField {
	NAME("Name", getLibraryExtractor(ILibraryInformation::getName)), //
	CAS("CAS", getLibraryExtractor(ILibraryInformation::getCasNumber)), //
	FORMULA("Formula", getLibraryExtractor(ILibraryInformation::getFormula)), //
	SMILES("SMILES", getLibraryExtractor(ILibraryInformation::getSmiles)), //
	SYNONYMS("Synonyms", getLibraryExtractor(ILibraryInformation::getSynonyms).andThen(elements -> elements != null ? String.join("; ", elements) : null));

	private final Function<IIdentificationTarget, String> transformer;
	private final String label;

	private LibraryField(String label, Function<IIdentificationTarget, String> transformer) {

		this.label = label;
		this.transformer = transformer;
	}

	/**
	 * 
	 * @return a transformer that can transform the given target into an ordinary String
	 */
	public Function<IIdentificationTarget, String> stringTransformer() {

		return transformer;
	}

	@Override
	public String toString() {

		return label;
	}

	private static <T> Function<IIdentificationTarget, T> getLibraryExtractor(Function<ILibraryInformation, T> extractor) {

		return target -> {
			/*
			 * No target.
			 */
			if(target == null) {
				return null;
			}
			/*
			 * No library information.
			 */
			ILibraryInformation information = target.getLibraryInformation();
			if(information == null) {
				return null;
			}
			//
			return extractor.apply(information);
		};
	}
}
