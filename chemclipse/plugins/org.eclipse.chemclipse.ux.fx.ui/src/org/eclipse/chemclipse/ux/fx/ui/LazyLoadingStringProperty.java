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

import java.util.concurrent.ExecutorService;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public abstract class LazyLoadingStringProperty extends SimpleStringProperty {

	private static final ExecutorService exe = LazyLoadingThreads.getExecutorService();

	public static final String DEFAULT_LOADING_STRING = "Thinking..";

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
			Platform.runLater(() -> startLoadingService());
			return loadingString;
		}
		return super.getValue();
	}

	protected void startLoadingService() {

		final Service<String> s = new Service<String>() {

			@Override
			protected Task<String> createTask() {
				return LazyLoadingStringProperty.this.createTask();
			}
		};

		s.setExecutor(exe);

		s.setOnFailed(e -> {
			setValue(s.getException().getLocalizedMessage());
			setLoaded(true);
		});

		s.setOnSucceeded(e -> {
			setValue(s.getValue());
			// System.err.println("Finished");
			setLoaded(true);
		});
		s.start();
		// System.err.println("Started");
	}

	protected abstract Task<String> createTask();
}
