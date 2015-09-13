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

    ; // No Instances

    /**
     * All properties under "co.uk.baconi.substeps"
     */
    public static final Config PROPERTIES = ConfigFactory.load().getConfig("co.uk.baconi.substeps");

    /**
     * Directory containing your features.
     */
    public static final String FEATURE_DIRECTORY = PROPERTIES.getString("cli.featuresDirectory");

    /**
     * Directory containing your substeps.
     */
    public static final String SUBSTEPS_DIRECTORY = PROPERTIES.getString("cli.substepsDirectory");

    /**
     * List of the tags required of a feature to run them
     */
    public static final List<String> TAGS = PROPERTIES.getStringList("cli.tags");

    /**
     * TODO: Determin what this property actually do.
     */
    public static final boolean STRICT = PROPERTIES.getBoolean("cli.strict");

    /**
     * TODO: Determin what this property actually do.
     */
    public static final List<String> NON_STRICT_KEYWORD_PRECEDENCE = PROPERTIES.getStringList("cli.nonStrictKeywordPrecedence");

    /**
     * The Description Provider. - TODO: Determin what that actaully means.
     */
    public static final String DESCRIPTION_PROVIDER = PROPERTIES.getString("cli.descriptionProvider");

    /**
     * List of SubStep Implementations, classes that implement annotations in: {@link com.technophobia.substeps.model.SubSteps}
     */
    public static final List<String> SETP_IMPLS = PROPERTIES.getStringList("cli.implementations.steps");

    /**
     * List of Before And After Implementations, classes that implement annotations in: {@link com.technophobia.substeps.runner.setupteardown.Annotations}
     */
    public static final List<String> BEFORE_AND_AFTER = PROPERTIES.getStringList("cli.implementations.beforeAndAfter");

    /**
     * Enables the running of the {@link com.technophobia.substeps.report.ExecutionReportBuilder}
     */
    public static final boolean REPORT_ENABLED = PROPERTIES.getBoolean("cli.report.enabled");

    /**
     * Implementation of the {@link com.technophobia.substeps.report.ExecutionReportBuilder}
     */
    public static final String REPORT_BUILDER = PROPERTIES.getString("cli.report.builder");

    /**
     * Directory to place the output of the {@link com.technophobia.substeps.report.ExecutionReportBuilder}
     */
    public static final String REPORT_OUTPUT_LOCATION = PROPERTIES.getString("cli.report.outputLocation");

}
