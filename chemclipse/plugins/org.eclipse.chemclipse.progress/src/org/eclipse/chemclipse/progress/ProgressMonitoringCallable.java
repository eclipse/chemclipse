/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.progress;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Extends {@link Callable} by {@link Consumer consumers} which can be used to
 * propagate progress information.
 * <p>
 *
 * <pre>
 * &#64;Override
 * protected Void call() throws Exception {
 * 	final DeleteReferencePeaksCallable ca = new DeleteReferencePeaksCallable(elements);
 * 	ca.setProgressConsumer(this::updateProgress);
 * 	ca.setProgressMessageConsumer(this::updateMessage);
 * 	return ca.call();
 * }
 * </pre>
 * </p>
 *
 * @see Consumer
 * @see BiConsumer
 *
 *
 * @author Alexander Kerner
 *
 * @param <T>
 *            the type of the result
 */
public abstract class ProgressMonitoringCallable<T> implements Callable<T> {

	private BiConsumer<Double, Double> progressConsumer;

	private Consumer<String> progressMessageConsumer;

	public BiConsumer<Double, Double> getProgressConsumer() {
		return progressConsumer;
	}

	public void updateProgress(final double cnt, final double total) {
		if (progressConsumer != null) {
			progressConsumer.accept(cnt, total);
		}
	}

	public void updateMessage(final String message) {
		if (progressMessageConsumer != null) {
			progressMessageConsumer.accept(message);
		}
	}

	public void updateProgress(final long cnt, final long total) {
		updateProgress((double) cnt, (double) total);
	}

	public Consumer<String> getProgressMessageConsumer() {
		return progressMessageConsumer;
	}

	public ProgressMonitoringCallable<T> setProgressConsumer(final BiConsumer<Double, Double> progressConsumer) {
		this.progressConsumer = progressConsumer;
		return this;
	}

	public ProgressMonitoringCallable<T> setProgressMessageConsumer(final Consumer<String> progressMessageConsumer) {
		this.progressMessageConsumer = progressMessageConsumer;
		return this;
	}

}
