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

import com.technophobia.substeps.execution.node.IExecutionNode;
import com.technophobia.substeps.execution.node.RootNode;
import com.technophobia.substeps.report.ExecutionReportBuilder;
import com.technophobia.substeps.runner.DescriptionProvider;
import com.technophobia.substeps.runner.JunitFeatureRunner;
import com.typesafe.config.ConfigRenderOptions;
import org.junit.Assert;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static uk.co.baconi.substeps.cli.JunitFeatureRunnerProperties.PROPERTIES;

/**
 * Purpose is to provide the ability to run as JUnit but configure using a properties file instead of annotations.
 * Created by JBacon on 13/09/2015.
 */
public class JunitFeatureRunnerUsingProperties extends JunitFeatureRunner {

    private static final Logger LOG = LoggerFactory.getLogger(JunitFeatureRunnerUsingProperties.class);

    private RootNode rootNode;
    private ExecutionReportBuilder reportBuilder;

    public JunitFeatureRunnerUsingProperties(final Class<?> classContainingTheTests) throws MalformedURLException {

        LOG.debug("JunitFeatureRunnerUsingProperties with class: " + classContainingTheTests.getSimpleName());

        LOG.trace("Running with properties: " + PROPERTIES.getProperties().root().render(ConfigRenderOptions.concise().setFormatted(true)));

        //
        // Step Implementations
        //
        final List<Class<?>> stepImplementations = new ArrayList<Class<?>>();
        for (final String step : PROPERTIES.getStepImplementations()) {
            try {
                final Class<?> stepImpl = Class.forName(step);
                stepImplementations.add(stepImpl);
            } catch (final ClassNotFoundException e) {
                LOG.error("Exception", e);
                Assert.fail("failed to instantiate step implementation: " + step);
            }
        }

        //
        // Feature and Substeps directories
        //
        final String featuresDirectory = PROPERTIES.getFeaturesDirectory();
        final String substepsDirectory = PROPERTIES.getSubstepsDirectory();

        //
        // Tags
        //
        final StringBuilder tagList = new StringBuilder();
        for (final String tag : PROPERTIES.getTags()) {
            tagList.append(tag.trim());
            tagList.append(" ");
        }
        final int lastIndexOfSpace = tagList.lastIndexOf(" ");
        if (lastIndexOfSpace >= 0) {
            tagList.deleteCharAt(lastIndexOfSpace);
        }


        //
        // Strict
        //
        final boolean strict = PROPERTIES.isStrict();

        //
        // Non Strict Keyword Precedence
        //
        final List<String> nonStrictKeywordPrecedence = PROPERTIES.getNonStrictKeywordPrecedence();

        //
        // Description Provider
        //
        final String descriptionProvider = PROPERTIES.getDescriptionProvider();
        final Class<? extends DescriptionProvider> descriptionProviderImpl = getImpl(descriptionProvider, DescriptionProvider.class);

        //
        // Before and After Implementations
        //
        final List<Class<?>> beforeAndAfterImplementations = new ArrayList<Class<?>>();
        for (final String beforeAndAfter : PROPERTIES.getBeforeAndAfter()) {
            try {
                final Class<?> beforeAndAfterImplementation = Class.forName(beforeAndAfter);
                beforeAndAfterImplementations.add(beforeAndAfterImplementation);
            } catch (final ClassNotFoundException e) {
                LOG.error("Exception", e);
                Assert.fail("failed to instantiate step implementation: " + beforeAndAfter);
            }
        }

        //
        // Mimicking JunitFeatureRunner constructor to complete setup
        //
        init(
                classContainingTheTests,
                stepImplementations,
                featuresDirectory,
                tagList.length() > 0 ? tagList.toString() : null,
                substepsDirectory,
                strict,
                nonStrictKeywordPrecedence.toArray(new String[nonStrictKeywordPrecedence.size()]),
                descriptionProviderImpl,
                beforeAndAfterImplementations.toArray(new Class[beforeAndAfterImplementations.size()])
        );

        //
        // Report Builder
        //
        if (PROPERTIES.isSubstepsReportEnabled()) {
            final String reportBuilderProperty = PROPERTIES.getSubstepsReportBuilder();
            final Class<? extends ExecutionReportBuilder> reportBuilderImpl = getImpl(reportBuilderProperty, ExecutionReportBuilder.class);
            try {
                reportBuilder = reportBuilderImpl.newInstance();
                reportBuilder.setOutputDirectory(new File(PROPERTIES.getSubstepsReportOutputLocation()));
            } catch (final InstantiationException e) {
                LOG.error("Exception", e);
                Assert.fail("failed to instantiate report builder: " + reportBuilderImpl.getName() + ":" + e.getMessage());
            } catch (final IllegalAccessException e) {
                LOG.error("Exception", e);
                Assert.fail("failed to instantiate report builder: " + reportBuilderImpl.getName() + ":" + e.getMessage());
            }

            // Get a hold of the RootNode
            final IExecutionNode rootExecutionNode = getRootExecutionNode();
            try {
                rootNode = (RootNode) rootExecutionNode;
            } catch (final ClassCastException e) {
                LOG.error("Exception", e);
                Assert.fail("rootNode was not an instance of RootNode: " + rootExecutionNode.getClass().getName() + ":" + e.getMessage());
            }
        }
    }

    @Override
    public void run(final RunNotifier junitNotifier) {
        // Run as normal
        super.run(junitNotifier);

        // Build report
        if (reportBuilder != null) {
            reportBuilder.addRootExecutionNode(rootNode);
            reportBuilder.buildReport();
        }
    }

    private <A> Class<? extends A> getImpl(final String className, final Class<A> wantedType) {
        try {
            final Class<?> clazz = Class.forName(className);
            if (wantedType.isAssignableFrom(clazz)) {
                return (Class<A>) clazz;
            }
        } catch (final ClassNotFoundException e) {
            LOG.error("Exception", e);
        } catch (final ClassCastException e) {
            LOG.error("Exception", e);
        }

        Assert.fail("failed to instantiate description provider: " + className);
        return wantedType;
    }
}
