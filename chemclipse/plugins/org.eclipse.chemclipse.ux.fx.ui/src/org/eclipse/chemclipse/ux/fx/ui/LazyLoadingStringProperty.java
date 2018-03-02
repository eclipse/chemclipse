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

import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

public abstract class LazyLoadingStringProperty extends SimpleStringProperty {

	public static final String DEFAULT_LOADING_STRING = "Loading..";

	private String loadingString = DEFAULT_LOADING_STRING;

	private boolean loaded = false;

	public LazyLoadingStringProperty() {

	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(final boolean loaded) {
		this.loaded = loaded;
	}

	public String getLoadingString() {
		return loadingString;
	}

	public void setLoadingString(final String loadingString) {
		this.loadingString = loadingString;
	}

	@Override
	public String getValue() {
		if (!loaded) {
			startLoadingService();
			return loadingString;
		}
		return super.getValue();
	}

	protected void startLoadingService() {

		final Task<String> s = LazyLoadingStringProperty.this.createTask();

		LazyLoadingThreads.getExecutorService().submit(s);

		s.setOnFailed(e -> {
			setLoaded(true);
			setValue(s.getException().getLocalizedMessage());

		});

		s.setOnSucceeded(e -> {
			setLoaded(true);
			setValue(s.getValue());

		});

		// System.err.println("Started");
	}

	protected abstract Task<String> createTask();
}
