package org.eclipse.chemclipse.model.filter;

import java.util.Collection;

import org.eclipse.chemclipse.filter.Filter;
import org.eclipse.chemclipse.filter.FilterChain;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A {@link Filter} Extension interface for filters that can work on {@link IMeasurement}s.
 * This is part of the Chemclipse FilterFramework, to make the Filter available simply register it with the OSGi Servicefactory under the {@link Filter} interface, implementors are encouraged to also register each filter under all sub(filter) interface.
 * 
 * @author Christoph Läubrich
 *
 * @param <ConfigType>
 */
public interface IMeasurementFilter<ConfigType> extends Filter<ConfigType> {

	/**
	 * Filters the given Collection of {@link IMeasurement}s with this filter and returns the result.
	 * The resulting Collection could either be the same or a ne collection, might have more or less items
	 * 
	 * @param filterItems
	 * @param configuration
	 *            the configuration to apply or <code>null</code> if no special configuration is desired
	 * @param nextFilter
	 *            the next filter to invoke, implementors might allow further processing with other filters in the chain by calling {@link FilterChain#doFilter(Object, org.eclipse.chemclipse.processing.core.MessageConsumer)}
	 * @param messageConsumer
	 *            Filters are meant to not throwing checked exceptions nor return no result if something goes wrong but report problems to the {@link MessageConsumer} this allows the upstream caller to decide what to do
	 * @param monitor
	 *            a {@link IProgressMonitor} to report progress of the filtering or <code>null</code> if no progress is desired
	 * @return a {@link IProcessingResult} that describes the outcome of the filtering, the result will be {@link Boolean#TRUE} if any item in the list was filter or {@link Boolean#FALSE} if no item was filtered or there was an error. The messages of the {@link IProcessingResult} may contain further information
	 * @throws IllegalArgumentException
	 *             if any of the given {@link IMeasurement} are incompatible with this filter ({@link #acceptsIMeasurement(IMeasurement)} returns <code>false</code> for them)
	 */
	Collection<? extends IMeasurement> filterIMeasurements(Collection<? extends IMeasurement> filterItems, ConfigType configuration, FilterChain<Collection<? extends IMeasurement>> nextFilter, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException;

	/**
	 * Checks if the given {@link IMeasurement} is compatible with this filter, that means that this filter can be applied without throwing an {@link IllegalArgumentException}
	 * 
	 * @param item
	 *            the {@link IMeasurement} to check
	 * @return <code>true</code> if this {@link IMeasurement} can be applied, <code>false</code> otherwise
	 */
	boolean acceptsIMeasurement(IMeasurement item);

	/**
	 * Creates a new configuration that is specially suited for the given {@link IMeasurement} type
	 * 
	 * @param item
	 * @return
	 */
	default ConfigType createConfiguration(IMeasurement item) {

		return createNewConfiguration();
	}
}