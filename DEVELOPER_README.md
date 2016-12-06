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

If the test cases refer to existing procedures, and do not create them, run `./gradlew oracleBootstrap`
before running those test cases to make sure that the procedures they refer to will be created.