Tags: @all @example

Feature: An example feature, where you navigate the G2G3 Digital website.

  Scenario: You browse through the G2G3 Digital website
    Given I go to the home page
    Then the page title is "Home | G2G3.DIGITAL"
    And if I click the "CAPABILITIES" navigation link
    Then the page title is "Capabilities | G2G3.DIGITAL"
    And if I click the "RESULTS" navigation link
    Then the page title is "Results | G2G3.DIGITAL"
    And if I click the "BLOG" navigation link
    Then the page title is "Blog | G2G3.DIGITAL"
    And if I click the "JOBS" navigation link
    Then the page title is "Jobs | G2G3.DIGITAL"
    And if I click the "CONTACT US" navigation link
    Then the page title is "Contact us | G2G3.DIGITAL"
