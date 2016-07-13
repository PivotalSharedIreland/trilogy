# Trilogy

Trilogy is a tool for testing SQL stored procedures and functions. Test cases are represented by plain text files utilizing markdown format, which makes them easily readable and editable.
## Requirements
- JRE 1.7 or later installed
- JDBC driver for the database under test available on the classpath.

##Latest version
0.1-pre ([download](https://github.com/PivotalSharedIreland/trilogy/releases/download/0.1-pre/trilogy-0.1-pre.jar))

## Command-line options
- Single test case run:
    ```
    $ java -jar trilogy.jar <filename> [--db_url=<JDBC url>] [--db_user=<username>] [--db_password=<password>]
    ```
    where `filename` is path to the `.stt` test file

- Project run:
    ```
    $ java -jar trilogy.jar --project=<path to project> [--db_url=<JDBC url>] [--db_user=<username>] [--db_password=<password>] [--skip_schema_load]
    ```
    add the `skip_schema_load` flag to indicate that the `tests/fixtures/schema.sql` should not be loaded if it is present in the project.

The `db_url` should point to the database under test. Make sure that the appropriate JDBC driver is available on the classpath.
Also, the `db_url`, `db_user` and `db_password` can be specified as environment variables.

## Standalone test case file format
Standalone test cases are good for small ad-hoc tests that do not require loading of the schema, procedure code or fixtures.
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

## Test projects
Test projects are more powerful, they consist of a number of files that have to be organized in a structure outlined below. A project must have at least one test case. Additionally it can have a database schema, any number SQL procedure scripts and fixtures.
### Project structure
    [project root]
    ┝[src]┐
    │      ├ 001.$get_user.sql
    │      ├ 002.check_balance.sql
    │      └ 003.send_message.sql
    ┕[tests]┐
             ┝[fixtures]┐
             │          ├ [setup]┐
             │          │        ├ [balance]┐
             │          │        │          ├ prepay_user_with_low_balance.sql
             │          │        │          └ user_with_low_balance.sql
             │          │        └ messages.sql
             │          ├ [teardown]┐
             │          │           ├ messages.sql
             │          │           ├ transactions.sql
             │          │           └ users.sql
             │          └ schema.sql
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
will instruct the framework to load the `tests/fixtures/setup/messages.sql` and `tests/fixtures/setup/balance/prepay_user_with_low_balance.sql` before running the test case in the order specified.
When a referenced fixture is not found, the whole test suite fails.

### Testing for errors
An optional special column `=ERROR=` can be used in the test `DATA` section for testing error conditions. Leave it blank in order to specify that no error is expected during the call. Alternatively, an error substring or a wildcard keyword `ANY` can be used. For example, when a database throws an error with text `ORA-20111: User specified error is thrown`, any of the following values will ensure that the test will pass: `ANY`, `aNy`, `ORA-20111`, `user`, `Error`, `Error is thrown`.

### Automatic schema load
When a `schema.sql` is placed into the `tests/fixtures` directory, it would be automatically executed before the project runs.

### Loading source files
The source files are executed in alphabetical order.