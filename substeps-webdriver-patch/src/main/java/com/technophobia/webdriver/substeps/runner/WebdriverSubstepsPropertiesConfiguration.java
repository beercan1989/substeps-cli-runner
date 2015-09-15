/*
 *	Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps.
 *
 *    Substeps is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.technophobia.webdriver.substeps.runner;

import java.io.File;
import java.net.URL;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.model.Configuration;

public enum WebdriverSubstepsPropertiesConfiguration implements WebdriverSubstepsConfiguration {

    INSTANCE; // uninstantiable

    private final Logger logger = LoggerFactory.getLogger(WebdriverSubstepsPropertiesConfiguration.class);

    /**
     * URL base for all substep actions. [file:///]
     */
    private final String baseUrl;

    /**
     * Type of WebDriver to use: HTMLUNIT, CHROME, FIREFOX, IE. [HTMLUNIT]
     */
    private final DefaultDriverType driverType;

    /**
     * TODO: Determine exactly what this actually does [10]
     */
    private final long defaultWebDriverTimeoutSecs;

    /**
     * Locale to set the WebDriver instance to use. [en-gb]
     */
    private final String driverLocale;

    /**
     * Factory to create WebDriver instances. [com.technophobia.webdriver.substeps.runner.DefaultWebDriverFactory]
     */
    private final Class<? extends WebDriverFactory> webdriverFactoryClass;

    /**
     * This should never be set to false on a headless build slave. [true]
     */
    private final boolean visualWebdriverCloseOnFail;

    /**
     * Should the WebDriver instance be closed after use. [true]
     */
    private final boolean shutdownWebdriver;

    /**
     * Should the WebDriver instance be reused or a new one created. [false]
     */
    private final boolean reuseWebdriver;

    /**
     * Should we disable javascript execution when using HtmlUnit. [false]
     */
    private final boolean htmlunitDisableJs;

    /**
     * Host name for HtmlUnit proxy. []
     */
    private final String htmlUnitProxyHost;

    /**
     * Port number for HtmlUnit proxy. [8080]
     */
    private final Integer htmlUnitProxyPort;


    WebdriverSubstepsPropertiesConfiguration() {

        logger.info("Using Typesafe Config version of WebdriverSubstepsPropertiesConfiguration!");

        //
        // Load all properties under [co.uk.baconi.substeps.driver]
        //
        final Config properties = ConfigFactory.load().getConfig("co.uk.baconi.substeps.driver");

        //
        // Basic Properties
        //
        baseUrl = determineBaseURL(properties.getString("baseUrl"));
        driverType = DefaultDriverType.valueOf(properties.getString("driverType").toUpperCase());
        defaultWebDriverTimeoutSecs = properties.getInt("timeoutInSeconds");
        driverLocale = properties.getString("locale");

        final String webdriverFactoryClassName = properties.getString("factory");
        try {
            webdriverFactoryClass = Class.forName(webdriverFactoryClassName).asSubclass(WebDriverFactory.class);
        } catch (final ClassNotFoundException ex) {
            throw new IllegalStateException(
                "'co.uk.baconi.substeps.driver.factory' is invalid with value '" + webdriverFactoryClassName + "'", ex
            );
        }

        //
        // Visual Specific Properties
        //
        visualWebdriverCloseOnFail = properties.getBoolean("visual.closeOnFail");
        shutdownWebdriver = properties.getBoolean("visual.shutdown");
        reuseWebdriver = properties.getBoolean("visual.reuse");

        //
        // HtmlUnit Specific Properties
        //
        htmlunitDisableJs = properties.getBoolean("htmlunit.disableJavascript");
        htmlUnitProxyHost = properties.getString("htmlunit.proxy.host");
        htmlUnitProxyPort = properties.getInt("htmlunit.proxy.port");

        logger.info("Using properties: " + properties.root().render(ConfigRenderOptions.concise().setFormatted(true)));
    }


    public String baseURL() {
        return baseUrl;
    }


    public DefaultDriverType driverType() {
        return driverType;
    }


    public String driverLocale() {
        return driverLocale;
    }


    public boolean shutDownWebdriver() {
        return shutdownWebdriver;
    }


    public boolean isJavascriptDisabledWithHTMLUnit() {
        return htmlunitDisableJs;
    }


    public boolean closeVisualWebDriveronFail() {
        return visualWebdriverCloseOnFail;
    }


    public boolean reuseWebDriver() {
        return reuseWebdriver;
    }


    public long defaultTimeout() {
        return defaultWebDriverTimeoutSecs;
    }


    public String getHtmlUnitProxyHost() {
        return htmlUnitProxyHost;
    }


    public Integer getHtmlUnitProxyPort() {
        return htmlUnitProxyPort;
    }


    public Class<? extends WebDriverFactory> getWebDriverFactoryClass() {
        return webdriverFactoryClass;
    }


    private String determineBaseURL(final String baseUrlProperty) {

        final String resolvedBaseUrl;
        final String property = removeTrailingSlash(baseUrlProperty);

        if (!property.startsWith("http") && !property.startsWith("file://")) {

            resolvedBaseUrl = removeTrailingSlash(new File(property).toURI()
                .toString());
        } else {
            resolvedBaseUrl = property;
        }

        return resolvedBaseUrl;
    }


    private String removeTrailingSlash(final String string) {
        if (string.endsWith("/")) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }
}
