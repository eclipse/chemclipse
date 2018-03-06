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

	// private final String loadingString = DEFAULT_LOADING_STRING;

	private boolean loaded = false;

	public LazyLoadingStringProperty(final String loadingString) {
		setValue(loadingString);
	}

	public LazyLoadingStringProperty() {
		setValue(DEFAULT_LOADING_STRING);
	}

	public boolean isLoaded() {
		return loaded;
	}

	protected void setLoaded(final boolean loaded) {
		this.loaded = loaded;
	}

	/**
	 * Is called after the background task's finished (success or failure). Override
	 * if needed. E.g. to bind the value afterwards.
	 */
	protected void afterLoaded() {
		// nothing

	}

	@Override
	public String getValue() {
		if (!loaded) {
			startLoadingService();
		}
		return super.getValue();
	}

	/**
	 * Starts the {@link Task} that will calculate this Property's value in the
	 * background.
	 */
	protected void startLoadingService() {
		setLoaded(true);
		final Task<String> s = LazyLoadingStringProperty.this.createTask();

		LazyLoadingThreads.getExecutorService().submit(s);

		s.setOnFailed(e -> {
			setValue(s.getException().getLocalizedMessage());
			afterLoaded();
		});

		s.setOnSucceeded(e -> {
			setValue(s.getValue());
			afterLoaded();

		});

	}

	/**
	 * Returns a {@link Task} that will calculate this Property's value in the
	 * background.
	 *
	 * @return a {@link Task} that will calculate this Property's value in the
	 *         background.
	 */
	protected abstract Task<String> createTask();
}
