/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;

public class PcaNormalizationData {

	static final public String CENTERING_MEAN = "centering mean";
	static final public String CENTERING_MEDIAN = "centering meadian";
	static final public String CENTERING_NONE = "centering none";
	static final public String SCALING_AUTO = "Autoscaling";
	static final public String SCALING_LEVEL = "Level scaling";
	static final public String SCALING_MAXIMUM = "Level maximum";
	static final public String SCALING_NONE = "Scaling none";
	static final public String SCALING_PARETO = "Pareto scaling";
	static final public String SCALING_RANGE = "Range scaling";
	static final public String SCALING_VAST = "Vast scaling";
	static final public String TRANSFORMATION_LOG10 = "transformation log10";
	static final public String TRANSFORMATION_NONE = "transformation none";
	static final public String TRANSFORMATION_POWER = "transformation powder";
	private String centering;
	private String scalingType;
	private Function<Double, Double> transformation;
	private Function<Double, Double> transformationNone = d -> d;
	private Function<Double, Double> transfromationLog10 = d -> 10.0 * Math.log10(Math.abs(d));
	private String transfromationName;
	private Function<Double, Double> transfromationPower = d -> Math.sqrt(Math.abs(d));

	public PcaNormalizationData() {
		this.transformation = transformationNone;
		this.transfromationName = TRANSFORMATION_NONE;
		centering = CENTERING_MEAN;
		scalingType = SCALING_AUTO;
	}

	private void autoscaling(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			final double mean = getCenteringValue(sample);
			final double deviation = getStandartDeviation(sample);
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = 0;
				if(deviation != 0) {
					normData = (data - mean) / deviation;
				}
				d.setNormalizedData(normData);
			});
		}
	}

	public String getCentering() {

		return centering;
	}

	private double getCenteringValue(ISample sample) {

		List<ISampleData> sampleData = sample.getSampleData();
		switch(centering) {
			case CENTERING_MEAN:
				return sampleData.stream().filter(d -> !d.isEmpty()).mapToDouble(s -> transformation.apply(s.getData())).summaryStatistics().getAverage();
			case CENTERING_MEDIAN:
				List<Double> data = sampleData.stream().filter(d -> !d.isEmpty()).mapToDouble(s -> transformation.apply(s.getData())).sorted().boxed().collect(Collectors.toList());
				int lenght = data.size();
				double median = lenght % 2 == 0 ? (data.get(lenght / 2 - 1) + data.get(lenght / 2)) / 2.0 // even
						: data.get(lenght / 2); //
				return median;
			case CENTERING_NONE:
				return 0;
			default:
				throw new RuntimeException("undefine centering");
		}
	}

	private double getMax(ISample sample) {

		return sample.getSampleData().stream().filter(d -> !d.isEmpty()).mapToDouble(s -> transformation.apply(s.getData())).summaryStatistics().getMax();
	}

	private double getMin(ISample sample) {

		return sample.getSampleData().stream().filter(d -> !d.isEmpty()).mapToDouble(s -> transformation.apply(s.getData())).summaryStatistics().getMin();
	}

	public String getScalingType() {

		return scalingType;
	}

	private double getStandartDeviation(ISample sample) {

		return Math.sqrt(Math.abs(getVariance(sample)));
	}

	public String getTransformation() {

		return transfromationName;
	}

	private double getVariance(ISample sample) {

		List<ISampleData> sampleData = sample.getSampleData();
		int count = sampleData.size();
		if(count > 1) {
			final double mean = getCenteringValue(sample);
			double sum = sampleData.stream().filter(d -> !d.isEmpty()).mapToDouble(d -> {
				double data = transformation.apply(d.getData());
				return (data - mean) * (data - mean);
			}).sum();
			return sum / (count - 1);
		}
		return 0;
	}

	private void levelscaling(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			final double mean = getCenteringValue(sample);
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = 0;
				if(mean != 0) {
					normData = (data - mean) / mean;
				}
				d.setNormalizedData(normData);
			});
		}
	}

	private void maxScaling(IPcaResults pcaResults) {

		pcaResults.getSampleList().forEach(s -> {
			final double mean = getCenteringValue(s);
			final double max = getMax(s);
			s.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = 0;
				if(max != 0) {
					normData = (data - mean) / (max);
				}
				d.setNormalizedData(normData);
			});
		});
	}

	public void normalize(IPcaResults pcaResults) {

		switch(scalingType) {
			case SCALING_AUTO:
				autoscaling(pcaResults);
				break;
			case SCALING_LEVEL:
				levelscaling(pcaResults);
				break;
			case SCALING_PARETO:
				paretoScaling(pcaResults);
				break;
			case SCALING_RANGE:
				rangeScaling(pcaResults);
				break;
			case SCALING_VAST:
				vastscaling(pcaResults);
				break;
			case SCALING_MAXIMUM:
				maxScaling(pcaResults);
				break;
			case SCALING_NONE:
				scalingNone(pcaResults);
				break;
		}
	}

	private void paretoScaling(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			final double mean = getCenteringValue(sample);
			final double deviationSqrt = Math.sqrt(getStandartDeviation(sample));
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = 0;
				if(deviationSqrt != 0) {
					normData = (data - mean) / deviationSqrt;
				}
				d.setNormalizedData(normData);
			});
		}
	}

	private void rangeScaling(IPcaResults pcaResults) {

		pcaResults.getSampleList().forEach(s -> {
			final double mean = getCenteringValue(s);
			final double max = getMax(s);
			final double min = getMin(s);
			s.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = 0;
				if(max != min) {
					normData = (data - mean) / (max - min);
				}
				d.setNormalizedData(normData);
			});
		});
	}

	private void scalingNone(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			final double mean = getCenteringValue(sample);
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = data - mean;
				d.setNormalizedData(normData);
			});
		}
	}

	public void setCentering(String centering) {

		if(centering.equals(CENTERING_MEAN) || centering.equals(CENTERING_MEDIAN) || centering.equals(CENTERING_NONE)) {
			this.centering = centering;
		}
	}

	public void setRawData(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = d.getData();
				d.setNormalizedData(data);
			});
		}
	}

	public void setScalingType(String scaling) {

		if(scaling.equals(SCALING_AUTO) || //
				scaling.equals(SCALING_NONE) || //
				scaling.equals(SCALING_LEVEL) || //
				scaling.equals(SCALING_MAXIMUM) || //
				scaling.equals(SCALING_PARETO) || //
				scaling.equals(SCALING_RANGE) || //
				scaling.equals(SCALING_VAST)) { //
			this.scalingType = scaling;
		}
	}

	public void setTransformation(String transformation) {

		switch(transformation) {
			case TRANSFORMATION_NONE:
				this.transformation = transformationNone;
				this.transfromationName = TRANSFORMATION_NONE;
				break;
			case TRANSFORMATION_LOG10:
				this.transformation = transfromationLog10;
				this.transfromationName = TRANSFORMATION_LOG10;
				break;
			case TRANSFORMATION_POWER:
				this.transformation = transfromationPower;
				this.transfromationName = TRANSFORMATION_POWER;
				break;
		}
	}

	private void vastscaling(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			final double mean = getCenteringValue(sample);
			final double variace = getVariance(sample);
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = transformation.apply(d.getData());
				double normData = (data - mean) / variace * mean;
				d.setNormalizedData(normData);
			});
		}
	}
}
