## Exercises

### Exercise 1

Using Modern CSP, specify the behavior of a traffic light that repeatedly turns green, then yellow, and
then red.

#### Answer

`Light = green -> yellow -> red -> Light`

### Exercise 2

Using Communicating Sequential Processes, define a process Copy that copies a character from
process Bergen to process Vestland.

#### Answer

```
Process Copy {
    Char c;
    True ->
    do Bergen?c -> Vestland!c od
}
```


