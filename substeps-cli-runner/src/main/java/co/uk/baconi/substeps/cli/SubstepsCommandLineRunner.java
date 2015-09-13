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

import co.uk.baconi.junit.BasicRunListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

@RunWith(JunitFeatureRunnerUsingProperties.class)
public class SubstepsCommandLineRunner {

    public static void main(final String[] args) {

        // Setup JUnit reporting.
        final JUnitCore jUnitCore = new JUnitCore();

        // TODO - Find an implementation that doesn't break JUnit runs.
        //jUnitCore.addListener(new XmlRunListener("reports/junit/junit_report.xml")); // TODO - Extract to a property

        // Add my basic run listener.
        jUnitCore.addListener(new BasicRunListener());

        // Run JUnit.
        final Result result = jUnitCore.run(SubstepsCommandLineRunner.class);

        // Return non-zero status code if unsuccessful.
        System.exit(result.wasSuccessful() ? 0 : 1);
    }
}
