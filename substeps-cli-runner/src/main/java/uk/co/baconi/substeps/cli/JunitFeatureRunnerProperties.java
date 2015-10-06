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

package uk.co.baconi.substeps.cli;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public enum JunitFeatureRunnerProperties {

    PROPERTIES; // uninstantiable


    /**
     * All properties under "substeps.ci", including environment specific if available.
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
    private final boolean substepsReportEnabled;

    /**
     * Implementation of the {@link com.technophobia.substeps.report.ExecutionReportBuilder}
     */
    private final String substepsReportBuilder;

    /**
     * Directory to place the output of the {@link com.technophobia.substeps.report.ExecutionReportBuilder}
     */
    private final String substepsReportOutputLocation;

    /**
     * Enables the running of the JUnit XML report generator.
     */
    private final boolean junitReportEnabled;

    /**
     * File to place the output of the JUnit XML report generator.
     */
    private final String junitReportOutputLocation;

    JunitFeatureRunnerProperties() {

        final Logger logger = LoggerFactory.getLogger(JunitFeatureRunnerProperties.class);

        //
        // All properties under "substeps.cli", including environment specific if available.
        //
        final String propertyBase = "substeps.cli";
        final String environmentProperty = "environment";
        final Config systemProperties = ConfigFactory.systemProperties();

        if (systemProperties.hasPath(environmentProperty)) {

            final String environment = systemProperties.getString(environmentProperty);

            logger.debug("Picking up custom property file for environment [" + environment + "]");

            // Load properties using ${environment}.conf falling back on the normal Typesafe Config structure.
            properties = ConfigFactory.
                    parseResourcesAnySyntax(environment).
                    withFallback(ConfigFactory.load()).
                    getConfig(propertyBase);
        } else {

            logger.debug("No custom environment property file being used");

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
        this.substepsReportEnabled = properties.getBoolean("reports.substeps.enabled");
        this.substepsReportBuilder = properties.getString("reports.substeps.builder");
        this.substepsReportOutputLocation = properties.getString("reports.substeps.outputLocation");
        this.junitReportEnabled = properties.getBoolean("reports.junit.enabled");
        this.junitReportOutputLocation = properties.getString("reports.junit.outputLocation");
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

    public boolean isSubstepsReportEnabled() {
        return substepsReportEnabled;
    }

    public String getSubstepsReportBuilder() {
        return substepsReportBuilder;
    }

    public String getSubstepsReportOutputLocation() {
        return substepsReportOutputLocation;
    }

    public boolean isJunitReportEnabled() {
        return junitReportEnabled;
    }

    public String getJunitReportOutputLocation() {
        return junitReportOutputLocation;
    }
}
