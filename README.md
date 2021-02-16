# verifier

### Building
`mvn package`

Should produce jar in /target/verifier-1.0-SNAPSHOT-jar-with-dependencies.jar

### Usage
`java -jar verifier-1.0-SNAPSHOT-jar-with-dependencies.jar <automata file> <ltl formula> [<output file>]`

```
<automata file>      automata file path
<ltl formula>        ltl formula (user parentheses if in contains spaces)
[<output file>]      optional output file path
```

### ltl
ltl grammar is located at ./src/main/resources/Ltl.g4

## Example
```java -jar verifier-1.0-SNAPSHOT-jar-with-dependencies.jar ./src/test/resources/test1.xml "G (F POWER_ON)"```

```
G (F POWER_ON) - not holds
------------------------------
In:
0. [Var(Start)]
1. [Var(Start), Var(tick)]
2. [Var(Start), Var(tick), Var(hal_init)]
3. [Var(Start), Var(tick), Var(tim4_enable)]
4. [Var(PRESTART)]
5. [Var(PRESTART), Var(tick)]
6. [Var(PRESTART), Var(tick), Var(shell_deinit)]
7. [Var(PRESTART), Var(tick), Var(bq_deinit)]
8. [Var(PRESTART), Var(tick), Var(pin_reset_s1)]
9. [Var(PRESTART), Var(tick), Var(pin_reset_s2)]
10. [Var(PRESTART), Var(tick), Var(pin_reset_s3)]
11. [Var(PRESTART), Var(tick), Var(delay_5000)]
12. [Var(POWER_ON)]
13. [Var(POWER_ON), Var(CHG)]
14. [Var(CPU_ON)]
15. [Var(CPU_ON), Var(CHG)]
16. [Var(BAT_ONLY)]
17. [Var(BAT_ONLY), Var(CHG)]
18. [Var(CPU_ON)]
19. [Var(CPU_ON), Var(CHG)]
20. [Var(BAT_ONLY)]
21. [Var(BAT_ONLY), Var(CHG)]
22. [Var(CPU_ON)]
------------------------------
-> 19. - cycle

```
