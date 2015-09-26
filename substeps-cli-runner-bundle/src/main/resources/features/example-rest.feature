Tags: @non-visual

Feature: A feature to test posting data to a json rest end point

  Scenario: A scenario where we post a value to an end point and get a response
    Given I am creating an API call to a json test website to validate some json
    Then I execute the API call
    And I get a successful response
      Containing a valid JSON object
      Containing a valid validation result