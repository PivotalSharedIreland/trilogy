## TEST
Generic test with some assertions
```
BEGIN
  NULL;
END;
```

### ASSERTIONS
#### SQL
Assertion description 1
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
Assertion description 2
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
