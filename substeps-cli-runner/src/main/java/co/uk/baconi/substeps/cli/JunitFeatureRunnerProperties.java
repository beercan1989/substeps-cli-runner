/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package co.uk.baconi.substeps.cli;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;

public enum JunitFeatureRunnerProperties {

    PROPERTIES; // uninstantiable


    /**
     * All properties under "co.uk.baconi.substeps", including environment specific if available.
     */
    private final Config properties;

    /**
     * Directory containing your features.
     */
    private final String featuresDirectory;

    /**
     * Directory containing your substeps.
     */
    private final String substepsDirectory;

    /**
     * List of the tags required of a feature to run them
     */
    private final List<String> tags;

    /**
     * TODO: Determin what this property actually do.
     */
    private final boolean strict;

    /**
     * TODO: Determin what this property actually do.
     */
    private final List<String> nonStrictKeywordPrecedence;

    /**
     * The Description Provider. - TODO: Determin what that actaully means.
     */
    private final String descriptionProvider;

    /**
     * List of SubStep Implementations, classes that implement annotations in: {@link com.technophobia.substeps.model.SubSteps}
     */
    private final List<String> stepImplementations;

    /**
     * List of Before And After Implementations, classes that implement annotations in: {@link com.technophobia.substeps.runner.setupteardown.Annotations}
     */
    private final List<String> beforeAndAfter;

    /**
     * Enables the running of the {@link com.technophobia.substeps.report.ExecutionReportBuilder}
     */
    private final boolean reportEnabled;

    /**
     * Implementation of the {@link com.technophobia.substeps.report.ExecutionReportBuilder}
     */
    private final String reportBuilder;

    /**
     * Directory to place the output of the {@link com.technophobia.substeps.report.ExecutionReportBuilder}
     */
    private final String reportOutputLocation;

    JunitFeatureRunnerProperties() {

        //
        // All properties under "co.uk.baconi.substeps", including environment specific if available.
        //
        final String propertyBase = "co.uk.baconi.substeps.cli";
        final String environmentProperty = "environment";
        final Config systemProperties = ConfigFactory.systemProperties();
        if (systemProperties.hasPath(environmentProperty)) {
            final String environment = systemProperties.getString(environmentProperty);
            // Load properties using ${environment}.conf falling back on the normal Typesafe Config structure.
            properties = ConfigFactory.
                    parseResourcesAnySyntax(environment).
                    withFallback(ConfigFactory.load()).
                    getConfig(propertyBase);
        } else {
            // Load properties without environment specific configuration.
            properties = ConfigFactory.
                    load().
                    getConfig(propertyBase);
        }

        this.featuresDirectory = properties.getString("featuresDirectory");
        this.substepsDirectory = properties.getString("substepsDirectory");
        this.tags = properties.getStringList("tags");
        this.strict = properties.getBoolean("strict");
        this.nonStrictKeywordPrecedence = properties.getStringList("nonStrictKeywordPrecedence");
        this.descriptionProvider = properties.getString("descriptionProvider");
        this.stepImplementations = properties.getStringList("implementations.steps");
        this.beforeAndAfter = properties.getStringList("implementations.beforeAndAfter");
        this.reportEnabled = properties.getBoolean("report.enabled");
        this.reportBuilder = properties.getString("report.builder");
        this.reportOutputLocation = properties.getString("report.outputLocation");
    }

    public Config getProperties() {
        return properties;
    }

    public String getFeaturesDirectory() {
        return featuresDirectory;
    }

    public String getSubstepsDirectory() {
        return substepsDirectory;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isStrict() {
        return strict;
    }

    public List<String> getNonStrictKeywordPrecedence() {
        return nonStrictKeywordPrecedence;
    }

    public String getDescriptionProvider() {
        return descriptionProvider;
    }

    public List<String> getStepImplementations() {
        return stepImplementations;
    }

    public List<String> getBeforeAndAfter() {
        return beforeAndAfter;
    }

    public boolean isReportEnabled() {
        return reportEnabled;
    }

    public String getReportBuilder() {
        return reportBuilder;
    }

    public String getReportOutputLocation() {
        return reportOutputLocation;
    }

}
