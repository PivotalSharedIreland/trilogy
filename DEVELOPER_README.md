# Required setup
[Oracle 12c](https://hub.docker.com/r/sath89/oracle-12c/) running with user app_user/secret

# Creating a release

When creating a release, the following steps should be followed:
- Update `build.gradle.kts` with the version number
- Update `README.md` with the version number
- When committing those changes, tag the commit with the version number, and push the tag to repository
- Build the JAR by running `./gradlew bootRepackage`
- Upload the resulting JAR to Github release associated with the version tag

# Acceptance

Acceptance can be done by running:

```
./gradlew bootRepackage && java -jar build/libs/trilogy.jar --db_url=... --db_user=... --db_password=... (<path_to_testcase>|--project=<path_to_test_project>)
```

with the appropriate options passed to trilogy. The DB URL, user name and password can also be set
via the environment variables `DB_URL`, `DB_USER` and `DB_PASSWORD` respectively.

Example projects and test cases for acceptance can be found in the `src/test/resources` directory. 
Generally, the required test case could be found there, but a new one can be added when needed.

A `DEGENERATE` stored procedure is available for Oracle and can be loaded by running 
`./gradlew oracleBootstrap`. It has two arguments, `V_IN IN INTEGER` and `V_OUT OUT INTEGER`, and 
 simply loops whatever in receives in `V_IN` back into `V_OUT`.