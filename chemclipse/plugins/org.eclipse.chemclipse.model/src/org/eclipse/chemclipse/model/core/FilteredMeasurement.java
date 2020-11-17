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
 * Alexander Kerner - implementation
 * Philip Wenig - this concept should be improved
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;
import org.eclipse.chemclipse.processing.ProcessorFactory;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.filter.FilterContext;
import org.eclipse.chemclipse.processing.filter.Filtered;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * This class is meant as a class for Filters that wants to filter some aspects
 * of a {@link IMeasurement}, the class simply delegates to an original
 * {@link IMeasurement} and returns all his data to the caller. A filter can now
 * extend this class and return for example a filtered set of data, or use the
 * setters to override the original data
 *
 * <b>Important</b> This class is not meant for format readers, these should
 * extend {@link AbstractMeasurement} instead and implement the interface on a
 * reader specific class
 *
 * @author Christoph Läubrich
 *
 */
public class FilteredMeasurement<FilteredType extends IMeasurement, ConfigType> implements IMeasurement, Filtered<FilteredType, ConfigType> {

	private static final long serialVersionUID = 2L;
	//
	private final FilteredType measurement;
	private String dataName;
	private String detailedInfo;
	private Double sampleWeight;
	private String sampleWeightUnit;
	private String barcodeType;
	private String barcode;
	private String sampleGroup;
	private String shortInfo;
	private String miscInfoSeparated;
	private String miscInfo;
	private Date date;
	private String operator;
	private final Map<String, IMeasurementResult<?>> measurementResults = new HashMap<>(1);
	private final Map<String, String> headerMap = new HashMap<>(1);
	//
	private transient FilterContext<FilteredType, ConfigType> context;

	public FilteredMeasurement(FilterContext<FilteredType, ConfigType> context) {

		this.context = context;
		if(context == null) {
			throw new IllegalArgumentException("filtered measurement can't be null!");
		}
		measurement = context.getFilteredObject();
	}

	@Override
	public boolean isKeyProtected(String key) {

		return false;
	}

	public FilteredType getFilteredObject() {

		return measurement;
	}

	@Override
	public void addMeasurementResult(IMeasurementResult<?> measurementResult) {

		if(measurementResult != null) {
			measurementResults.put(measurementResult.getIdentifier(), measurementResult);
		}
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {

		if(measurement.getClass().isAssignableFrom(adapter)) {
			// if we can cast the filtered object itself we return it here, that way a filtered object can always be adapted to itself
			return adapter.cast(measurement);
		}
		return measurement.getAdapter(adapter);
	}

	@Override
	public FilterContext<FilteredType, ConfigType> getFilterContext() {

		return context;
	}

	@Override
	public String getHeaderData(String key) {

		String value = headerMap.get(key);
		if(value != null) {
			return value;
		}
		return measurement.getHeaderData(key);
	}

	@Override
	public String getHeaderDataOrDefault(String key, String defaultValue) {

		String data = getHeaderData(key);
		if(data != null) {
			return data;
		}
		return defaultValue;
	}

	@Override
	public IMeasurementResult<?> getMeasurementResult(String identifier) {

		IMeasurementResult<?> result = measurementResults.get(identifier);
		if(result != null) {
			return result;
		}
		return measurement.getMeasurementResult(identifier);
	}

	@Override
	public boolean headerDataContainsKey(String key) {

		if(headerMap.containsKey(key)) {
			return true;
		}
		return measurement.headerDataContainsKey(key);
	}

	@Override
	public void putHeaderData(String key, String value) {

		headerMap.put(key, value);
	}

	@Override
	public void putHeaderData(Map<String, String> headerData) {

		for(Map.Entry<String, String> entry : headerData.entrySet()) {
			putHeaderData(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void removeHeaderData(String key) throws InvalidHeaderModificationException {

		String remove = headerMap.remove(key);
		if(remove == null) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void deleteMeasurementResult(String identifier) {

		IMeasurementResult<?> remove = measurementResults.remove(identifier);
		if(remove == null) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Map<String, String> getHeaderDataMap() {

		HashMap<String, String> map = new HashMap<>();
		map.putAll(measurement.getHeaderDataMap());
		map.putAll(headerMap);
		return Collections.unmodifiableMap(map);
	}

	@Override
	public void removeAllMeasurementResults() {

		throw new UnsupportedOperationException();
	}

	@Override
	public String getOperator() {

		if(operator != null) {
			return operator;
		}
		return measurement.getOperator();
	}

	@Override
	public void setOperator(String operator) {

		this.operator = operator;
	}

	@Override
	public Date getDate() {

		if(date != null) {
			return date;
		}
		return measurement.getDate();
	}

	@Override
	public Collection<IMeasurementResult<?>> getMeasurementResults() {

		ArrayList<IMeasurementResult<?>> list = new ArrayList<>();
		list.addAll(measurementResults.values());
		list.addAll(measurement.getMeasurementResults());
		return Collections.unmodifiableCollection(list);
	}

	@Override
	public void setDate(Date date) {

		this.date = date;
	}

	@Override
	public String getMiscInfo() {

		if(miscInfo != null) {
			return miscInfo;
		}
		return measurement.getMiscInfo();
	}

	@Override
	public void setMiscInfo(String miscInfo) {

		this.miscInfo = miscInfo;
	}

	@Override
	public String getMiscInfoSeparated() {

		if(miscInfoSeparated != null) {
			return miscInfoSeparated;
		}
		return measurement.getMiscInfoSeparated();
	}

	@Override
	public void setMiscInfoSeparated(String miscInfoSeparated) {

		this.miscInfoSeparated = miscInfoSeparated;
	}

	@Override
	public String getShortInfo() {

		if(shortInfo != null) {
			return shortInfo;
		}
		return measurement.getShortInfo();
	}

	@Override
	public void setShortInfo(String shortInfo) {

		this.shortInfo = shortInfo;
	}

	@Override
	public String getDetailedInfo() {

		if(detailedInfo != null) {
			return detailedInfo;
		}
		return measurement.getDetailedInfo();
	}

	@Override
	public void setDetailedInfo(String detailedInfo) {

		this.detailedInfo = detailedInfo;
	}

	@Override
	public String getSampleGroup() {

		if(sampleGroup != null) {
			return sampleGroup;
		}
		return measurement.getSampleGroup();
	}

	@Override
	public void setSampleGroup(String sampleGroup) {

		this.sampleGroup = sampleGroup;
	}

	@Override
	public String getBarcode() {

		if(barcode != null) {
			return barcode;
		}
		return measurement.getBarcode();
	}

	@Override
	public void setBarcode(String barcode) {

		this.barcode = barcode;
	}

	@Override
	public String getBarcodeType() {

		if(barcodeType != null) {
			return barcodeType;
		}
		return measurement.getBarcodeType();
	}

	@Override
	public void setBarcodeType(String barcodeType) {

		this.barcodeType = barcodeType;
	}

	@Override
	public double getSampleWeight() {

		if(sampleWeight != null) {
			return sampleWeight;
		}
		return measurement.getSampleWeight();
	}

	@Override
	public void setSampleWeight(double sampleWeight) {

		this.sampleWeight = sampleWeight;
	}

	@Override
	public String getSampleWeightUnit() {

		if(sampleWeightUnit != null) {
			return sampleWeightUnit;
		}
		return measurement.getSampleWeightUnit();
	}

	@Override
	public void setSampleWeightUnit(String sampleWeightUnit) {

		this.sampleWeightUnit = sampleWeightUnit;
	}

	@Override
	public String getDataName() {

		if(dataName != null) {
			return dataName;
		}
		return measurement.getDataName();
	}

	@Override
	public void setDataName(String dataName) {

		this.dataName = dataName;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

		out.defaultWriteObject();
		out.writeObject(context.getFilterTime());
		out.writeObject(context.getFilteredObject());
		ConfigType filterConfig = context.getFilterConfig();
		if(filterConfig instanceof Serializable) {
			out.writeObject(filterConfig);
		} else {
			out.writeObject(null);
		}
		Filter<ConfigType> filter = context.getFilter();
		if(filter == null) {
			out.writeObject(null);
		} else {
			out.writeObject(filter.getID());
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

		in.defaultReadObject();
		Date filterTime = (Date)in.readObject();
		FilteredType filteredObject = (FilteredType)in.readObject();
		ConfigType filterConfig = (ConfigType)in.readObject();
		String filterID = (String)in.readObject();
		this.context = new FilterContext<FilteredType, ConfigType>() {

			private Filter<ConfigType> filter;

			@Override
			public FilteredType getFilteredObject() {

				return filteredObject;
			}

			@Override
			public Date getFilterTime() {

				return filterTime;
			}

			@Override
			public Filter<ConfigType> getFilter() {

				if(filter == null && filterID != null) {
					BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
					if(bundleContext != null) {
						ServiceReference<ProcessorFactory> reference = bundleContext.getServiceReference(ProcessorFactory.class);
						if(reference != null) {
							ProcessorFactory service = bundleContext.getService(reference);
							if(service != null) {
								Collection<Filter<ConfigType>> filters = service.getProcessors(ProcessorFactory.genericClass(Filter.class), (filter, properties) -> filter.getID().equals(filterID));
								for(Filter<ConfigType> filter : filters) {
									this.filter = filter;
								}
								bundleContext.ungetService(reference);
							}
						}
					}
				}
				return filter;
			}

			@Override
			public ConfigType getFilterConfig() {

				return filterConfig;
			}
		};
	}
}
