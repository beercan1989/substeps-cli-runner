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

package co.uk.baconi.junit;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;

public class BasicRunListener extends RunListener {

    private static final Logger LOG = LoggerFactory.getLogger(BasicRunListener.class);

    @Override
    public void testRunFinished(final Result result) throws Exception {

        if(LOG.isDebugEnabled()) {

            final String osSpecificNewLine = String.format("%n");
            final String linePrefix = "  ##  ";

            final StringBuilder results = new StringBuilder();

            results.append("JUnit Test Results:").
                    append(osSpecificNewLine);

            results.append(linePrefix).
                    append("Was Successful: \t").
                    append(result.wasSuccessful()).
                    append(osSpecificNewLine);

            results.append(linePrefix).
                    append("Run Time: \t").
                    append(NumberFormat.getInstance().format((double) result.getRunTime() / 1000)).
                    append(" seconds").
                    append(osSpecificNewLine);

            results.append(linePrefix).
                    append("Run Count: \t").
                    append(result.getRunCount()).
                    append(osSpecificNewLine);

            results.append(linePrefix).
                    append("Failure Count: \t").
                    append(result.getFailureCount()).
                    append(osSpecificNewLine);

            results.append(linePrefix).
                    append("Ignore Count: \t").
                    append(result.getIgnoreCount()).
                    append(osSpecificNewLine);

            LOG.debug(results.toString());
        }
    }
}
