/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.services;

public class LibraryInformationResolverSettings {

	private ResolverOption resolverOption = ResolverOption.SYNONYM;
	boolean caseSensitive = true;
	boolean searchExact = true;

	public ResolverOption getResolverOption() {

		return resolverOption;
	}

	public void setResolverOption(ResolverOption resolverOption) {

		this.resolverOption = resolverOption;
	}

	public boolean isCaseSensitive() {

		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {

		this.caseSensitive = caseSensitive;
	}

	public boolean isSearchExact() {

		return searchExact;
	}

	public void setSearchExact(boolean searchExact) {

		this.searchExact = searchExact;
	}
}