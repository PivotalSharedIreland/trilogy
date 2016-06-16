# Trilogy

Trilogy is a tool for testing SQL stored procedures and functions. Test cases are represented by plain text files utilizing markdown format, which makes them easily readable and editable.

## Command-line options
```
$ ./trilogy <filename>[ <filename>...] --db-url=<jdbc url>
```

## Test case file format

```
# TEST CASE <procedure/function name>
<test case description>
## TEST
<test description>
### DATA
|V_IN_ARG_1 | V_IN_ARG_2   | V_INOUT_ARG1 | V_INOUT_ARG1$ | V_OUT_ARG1$ | =ERROR= |
|----------:|--------------|--------------|---------------|-------------|---------|
|123        |Ramen shrimps |   __NULL__   | 25            | Soup        |         |
|234        |Red wine      |   __NULL__   | 41            | Mulled      |         |
### ASSERTIONS
#### SQL <optional? assertion description>
DECLARE
  V_COUNT INTEGER
BEGIN
  SELECT COUNT(*) INTO V_COUNT FROM DUAL;
  IF V_COUNT > 0 THEN
    RETURN TRUE;
  ELSE
    RETURN FALSE;
  END IF;
END
```