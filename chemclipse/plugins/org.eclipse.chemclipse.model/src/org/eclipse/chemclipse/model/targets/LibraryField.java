/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import java.util.function.Function;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.text.ILabel;

public enum LibraryField implements ILabel {

	NAME("Name", getLibraryExtractor(ILibraryInformation::getName)), //
	CAS("CAS", getLibraryExtractor(ILibraryInformation::getCasNumber)), //
	NAME_CAS("Name (CAS)", getLibraryExtractor(ILibraryInformation::getName, ILibraryInformation::getCasNumber)), //
	FORMULA("Formula", getLibraryExtractor(ILibraryInformation::getFormula)), //
	SMILES("SMILES", getLibraryExtractor(ILibraryInformation::getSmiles)), //
	SYNONYMS("Synonyms", getLibraryExtractor(ILibraryInformation::getSynonyms).andThen(elements -> elements != null ? String.join("; ", elements) : null)), //
	INCHI("InChI", getLibraryExtractor(ILibraryInformation::getInChI)), //
	INCHIKEY("InChIKey", getLibraryExtractor(ILibraryInformation::getInChIKey)), //
	MOL_WEIGHT("Mol Weight", getLibraryExtractorDouble(ILibraryInformation::getMolWeight)), //
	EXACT_MASS("Exact Mass", getLibraryExtractorDouble(ILibraryInformation::getExactMass)), //
	REFID("RefID", getLibraryExtractor(ILibraryInformation::getReferenceIdentifier)), //
	NAME_REFID("Name (RefID)", getLibraryExtractor(ILibraryInformation::getName, ILibraryInformation::getReferenceIdentifier)); //

	private final Function<IIdentificationTarget, String> transformer;
	private final String label;

	private LibraryField(String label, Function<IIdentificationTarget, String> transformer) {

		this.label = label;
		this.transformer = transformer;
	}

	public String label() {

		return label;
	}

	/**
	 * @return a transformer that can transform the given target into an ordinary String
	 */
	public Function<IIdentificationTarget, String> getTransformer() {

		return transformer;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
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
			ILibraryInformation libraryInformation = target.getLibraryInformation();
			if(libraryInformation == null) {
				return null;
			}
			//
			return extractor.apply(libraryInformation);
		};
	}

	private static <T> Function<IIdentificationTarget, String> getLibraryExtractorDouble(Function<ILibraryInformation, Double> extractor) {

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
			ILibraryInformation libraryInformation = target.getLibraryInformation();
			if(libraryInformation == null) {
				return null;
			}
			//
			return Double.toString(extractor.apply(libraryInformation));
		};
	}

	private static <T> Function<IIdentificationTarget, String> getLibraryExtractor(Function<ILibraryInformation, T> extractor1, Function<ILibraryInformation, T> extractor2) {

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
			ILibraryInformation libraryInformation = target.getLibraryInformation();
			if(libraryInformation == null) {
				return null;
			}
			//
			String result = extractor1.apply(libraryInformation) + " (" + extractor2.apply(libraryInformation) + ")";
			return result.trim();
		};
	}
}