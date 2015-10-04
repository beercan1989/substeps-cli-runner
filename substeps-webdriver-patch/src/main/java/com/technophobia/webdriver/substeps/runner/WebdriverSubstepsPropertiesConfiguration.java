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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public enum WebdriverSubstepsPropertiesConfiguration implements WebdriverSubstepsConfiguration {

    INSTANCE; // uninstantiable

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

        final Logger logger = LoggerFactory.getLogger(WebdriverSubstepsPropertiesConfiguration.class);
        logger.info("Using Typesafe Config version of WebdriverSubstepsPropertiesConfiguration!");

        //
        // Load all properties under [substeps.driver], including environment specific if available.
        //
        final Config properties;

        final String environmentProperty = "environment";
        final String propertyBase = "substeps.driver";
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

        //
        // Basic Properties
        //
        baseUrl = determineBaseURL(properties.getString("baseUrl"));
        driverType = DefaultDriverType.valueOf(properties.getString("webdriver.driverType").toUpperCase());
        defaultWebDriverTimeoutSecs = properties.getInt("webdriver.timeoutInSeconds");
        driverLocale = properties.getString("webdriver.locale");

        final String webdriverFactoryClassName = properties.getString("webdriver.factory");
        try {
            webdriverFactoryClass = Class.forName(webdriverFactoryClassName).asSubclass(WebDriverFactory.class);
        } catch (final ClassNotFoundException ex) {
            throw new IllegalStateException(
                    "'substeps.driver.factory' is invalid with value '" + webdriverFactoryClassName + "'", ex
            );
        }

        //
        // Visual Specific Properties
        //
        visualWebdriverCloseOnFail = properties.getBoolean("webdriver.visual.closeOnFail");
        shutdownWebdriver = properties.getBoolean("webdriver.visual.shutdown");
        reuseWebdriver = properties.getBoolean("webdriver.visual.reuse");

        //
        // HtmlUnit Specific Properties
        //
        htmlunitDisableJs = properties.getBoolean("webdriver.htmlunit.disableJavascript");
        htmlUnitProxyHost = properties.getString("webdriver.htmlunit.proxy.host");
        htmlUnitProxyPort = properties.getInt("webdriver.htmlunit.proxy.port");

        logger.info("Using properties: " + properties.root().render(ConfigRenderOptions.concise().setFormatted(true)));
    }


    @Override
    public String baseURL() {
        return baseUrl;
    }


    @Override
    public DefaultDriverType driverType() {
        return driverType;
    }


    @Override
    public String driverLocale() {
        return driverLocale;
    }


    @Override
    public boolean shutDownWebdriver() {
        return shutdownWebdriver;
    }


    @Override
    public boolean isJavascriptDisabledWithHTMLUnit() {
        return htmlunitDisableJs;
    }


    @Override
    public boolean closeVisualWebDriveronFail() {
        return visualWebdriverCloseOnFail;
    }


    @Override
    public boolean reuseWebDriver() {
        return reuseWebdriver;
    }


    @Override
    public long defaultTimeout() {
        return defaultWebDriverTimeoutSecs;
    }


    @Override
    public String getHtmlUnitProxyHost() {
        return htmlUnitProxyHost;
    }


    @Override
    public Integer getHtmlUnitProxyPort() {
        return htmlUnitProxyPort;
    }


    @Override
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
