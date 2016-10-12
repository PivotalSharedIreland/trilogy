# Required setup
[Oracle 12c](https://hub.docker.com/r/sath89/oracle-12c/) running with user app_user/secret

The successful test needs the following procedure to be available to the app_user:
```
CREATE OR REPLACE PROCEDURE DEGENERATE
(
  V_IN IN INTEGER
, V_OUT OUT INTEGER
) AS
BEGIN
  SELECT V_IN  INTO V_OUT FROM DUAL;
END DEGENERATE;
```

# Creating a release

When creating a release, the following steps should be followed:
- Update `build.gradle.kts` with the version number
- Update `README.md` with the version number
- When committing those changes, tag the commit with the version number, and push the tag to repository
- Build the JAR by running `./gradlew bootRepackage`
- Upload the resulting JAR to Github release associated with the version tag