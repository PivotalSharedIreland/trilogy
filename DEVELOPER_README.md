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

# Packaging executable
`./gradlew distShadowZip`