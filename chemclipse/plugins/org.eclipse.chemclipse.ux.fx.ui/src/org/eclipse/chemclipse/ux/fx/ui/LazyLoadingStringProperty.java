/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.chemclipse.ux.fx.ui;

public abstract class LazyLoadingStringProperty extends LazyLoadingProperty<String> {

	public static final String DEFAULT_LOADING_STRING = "Loading..";

	public LazyLoadingStringProperty(final String loadingString) {
		setValue(loadingString);
	}

	public LazyLoadingStringProperty() {
		setValue(DEFAULT_LOADING_STRING);
	}

	@Override
	protected String getFailedValue(final Throwable t) {
		return t.getLocalizedMessage();
	}

}
