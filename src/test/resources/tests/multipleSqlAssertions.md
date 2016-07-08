## TEST
Test description

### DATA
|PARAM1  |PARAM2 |=ERROR=|
|--------|------:|-------|
| FOO    | 12    |       |
|__NULL__| 0     |       |
| BAR    | -18   |       |
|        | 12    |       |

### ASSERTIONS
#### SQL
Assertion description
```
DECLARE
    l_count NUMBER;
    wrong_count EXCEPTION;
BEGIN
    SELECT count(*) INTO l_count FROM dual;
    IF l_count = 0
    THEN
        RAISE wrong_count;
    END IF;
END;
```
#### SQL
Assertion description
```
DECLARE
    alt_count NUMBER;
    wrong_count EXCEPTION;
BEGIN
    SELECT count(*) INTO alt_count FROM dual;
    IF alt_count = 0
    THEN
        RAISE wrong_count;
    END IF;
END;
```
