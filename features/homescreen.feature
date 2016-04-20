@both
Scenario: User cannot complete a login on the home screen without entering a password
Given that the user can see text "Login"
and the user can see text "Forgotten your password"
When the user enters "jeffhaynie" into field with id "username."
and the user clicks on view with id "signinButton."
Then they see alert "Please enter Password"
and the user dismisses alert by clicking on the "OK" button
and takes screenshot "endOfLoginValidationFailureScenario"
