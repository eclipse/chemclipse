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
package org.eclipse.chemclipse.model.core;

import java.util.Collection;

public interface Classifiable {

	Collection<String> getClassifier();

	void addClassifier(String classifier);

	void removeClassifier(String classifier);

	static String asString(Classifiable classifiable) {

		return asString(classifiable.getClassifier());
	}

	static String asString(Iterable<? extends CharSequence> classifier) {

		if(classifier != null) {
			return String.join(", ", classifier);
		}
		return "";
	}
}
