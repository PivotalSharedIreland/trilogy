# Trilogy

Trilogy is a tool for testing SQL stored procedures and functions. Test cases are represented by plain text files utilizing markdown format, which makes them easily readable and editable.

## Command-line options
Single test case run
```
$ ./trilogy <filename> --db-url=<jdbc url>
```

Project run
```
$ ./trilogy --project=<path-to-project> --db-url=<jdbc url>
```


## Standalone test case file format

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
#### SQL
<assertion description>
``` <SQL assertion> ```
```

The `TEST` and `SQL` sections can be repeated multiple times. The `SQL` assertion can span multiple lines.

## Project structure
    [project root]
    ┝[src]┐
    │      ├ 001.$get_user.sql
    │      ├ 002.check_balance.sql
    │      └ 003.send_message.sql
    ┕[tests]┐
             ┝[fixtures]┐
             │           ├ [setup]┐
             │           │        ├ [balance]┐
             │           │        │              ├ prepay_user_with_low_balance.sql
             │           │        │              └ user_with_low_balance.sql
             │           │        └ messages.sql
             │           ├ [teardown]┐
             │           │           ├ messages.sql
             │           │           ├ transactions.sql
             │           │           └ users.sql
             │           └ schema.sql
             ├ [messaging]┐
             │            ├ online.stt
             │            └ offline.stt
             │
             ├ balance.stt
             ├ prepay_sender.stt
             └ billpay_sender.stt

### Project test case format
```
# TEST CASE <procedure/function name>
<test case description>
## BEFORE ALL
- <Setup fixture name>
- <Another setup fixture name>
## BEFORE EACH TEST
- <Setup fixture name>
## BEFORE EACH ROW
- <Setup fixture name>
- <Another setup fixture name>
## AFTER EACH ROW
- <Teardown fixture name>
- <Another teardown fixture name>
## AFTER EACH TEST
- <Teardown fixture name>
## AFTER ALL
- <Teardown fixture name>
## TEST
<test description>
### DATA
|V_IN_ARG_1 | V_IN_ARG_2   | V_INOUT_ARG1 | V_INOUT_ARG1$ | V_OUT_ARG1$ | =ERROR= |
|----------:|--------------|--------------|---------------|-------------|---------|
|123        |Ramen shrimps |   __NULL__   | 25            | Soup        |         |
|234        |Red wine      |   __NULL__   | 41            | Mulled      |         |
### ASSERTIONS
#### SQL
<assertion description>
``` <SQL assertion> ```
```

### Referencing fixtures
When a fixture is referenced in the test case, regular words can be used. The reference will be then transforms the words to locate the file. For example, given the project structure above any "BEFORE *" section can reference the fixtures as "Messages", "Some folder / prepay user with LOW balance" or "some folder/user with low balance". Similarly, any "AFTER *" section can reference the fixtures as "Messages", "TransActions" or "users". At the moment only alpha-numeric filenames with underscores are supported.
For example:
```
### BEFORE ALL
- Messages
- Balance / Prepay user with low balance
```
will instruct the framework to load the `tests/fixtures/setup/messages.sql` and `tests/fixtures/setup/balance/prepay_user_with_low_balance.sql` in the order specified before running the test case.
When a referenced fixture is not found, the whole test suite fails.

### Automatic schema load
When a `schema.sql` is placed into the `tests/fixtures` directory, it would be automatically executed before the project runs.

### Loading source files
The source files are executed in alphabetical order.