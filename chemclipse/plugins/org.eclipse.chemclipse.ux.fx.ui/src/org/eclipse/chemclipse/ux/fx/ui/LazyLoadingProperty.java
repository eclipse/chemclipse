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

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;

public abstract class LazyLoadingProperty<T> extends SimpleObjectProperty<T> {

	private boolean loaded = false;

	private final ChangeListener<T> valueChangeListener = (o, ov, nv) -> {
		valueExternallyUpdated(nv);
	};

	/**
	 * Is called after the background task's finished (success or failure). Override
	 * if needed. E.g. to bind the value afterwards.
	 */
	protected void afterLoaded() {
		// nothing

	}

	/**
	 * Returns a {@link Task} that will calculate this Property's value in the
	 * background.
	 *
	 * @return a {@link Task} that will calculate this Property's value in the
	 *         background.
	 */
	protected abstract Task<T> createTask();

	protected T getFailedValue(final Throwable t) {
		return null;
	}

	@Override
	public T getValue() {
		if (!loaded) {
			startLoadingService();
		}
		return super.getValue();
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(final boolean loaded) {

		// the value has been loaded. Trigger callback for write-access from now on.
		if (!this.loaded && loaded) {
			addListener(valueChangeListener);
		}
		// the loaded property has been reset manually. Remove change listener
		if (this.loaded && !loaded) {
			removeListener(valueChangeListener);
		}

		this.loaded = loaded;
	}

	/**
	 * Starts the {@link Task} that will calculate this Property's value in the
	 * background.
	 */
	protected void startLoadingService() {
		setLoaded(true);
		final Task<T> s = LazyLoadingProperty.this.createTask();

		LazyLoadingThreads.getExecutorService().submit(s);

		s.setOnFailed(e -> {
			setValue(getFailedValue(e.getSource().getException()));
			afterLoaded();
		});

		s.setOnSucceeded(e -> {
			setValue(s.getValue());
			afterLoaded();

		});

	}

	/**
	 * Override this callback method to handle external value-change-events
	 * (not-lazily-loaded). This callback is only called, if the value is updated
	 * manually, i.e., not via the lazy-loading mechanism.
	 *
	 * @param nv
	 *            the new value
	 */
	protected void valueExternallyUpdated(final T nv) {
		// override if needed

	}

}
