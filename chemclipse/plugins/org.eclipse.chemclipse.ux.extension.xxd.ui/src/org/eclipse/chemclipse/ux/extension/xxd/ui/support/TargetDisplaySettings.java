/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

import java.util.function.Function;

import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public interface TargetDisplaySettings {

	public static final TargetExtendedComparator COMPARATOR = new TargetExtendedComparator(SortOrder.DESC);

	enum LibraryField {
		NAME("Name", libraryExtractor(ILibraryInformation::getName)), //
		CAS("CAS", libraryExtractor(ILibraryInformation::getCasNumber)), //
		CLASSIFICATION("Classifications", libraryExtractor(ILibraryInformation::getClassifier).andThen(elements -> elements != null ? String.join("; ", elements) : null)), //
		FORMULA("Formula", libraryExtractor(ILibraryInformation::getFormula)), //
		SYNONYMS("Synonyms", libraryExtractor(ILibraryInformation::getSynonyms).andThen(elements -> elements != null ? String.join("; ", elements) : null));

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

		private static <T> Function<IIdentificationTarget, T> libraryExtractor(Function<ILibraryInformation, T> extractor) {

			return target -> {
				if(target == null) {
					return null;
				}
				ILibraryInformation information = target.getLibraryInformation();
				if(information == null) {
					return null;
				}
				return extractor.apply(information);
			};
		}
	}

	boolean isShowPeakLabels();

	boolean isShowScanLables();

	void setShowPeakLabels(boolean showPeakLabels);

	void setShowScanLables(boolean showScanLables);

	int getRotation();

	int getCollisionDetectionDepth();

	void setCollisionDetectionDepth(int depth);

	/**
	 * Sets the rotation angel of the labels in degree
	 * 
	 * @param degree
	 */
	void setRotation(int degree);

	LibraryField getField();

	void setField(LibraryField libraryField);
}
