# Complier
简易编译器，根据自定义语法说明文件（产生式），进行LL(1)语法分析并生成三地址码

```java
S->begin SList end
S->id:=E
S->if C then S S'
S->while C do S
S'->else S
S'->null
SList->S SList'
SList'->;SList
SList'->null
C->E C'
C'->>E
C'-><E
C'->=E
C'->>=E
C'-><>E
C'-><=E
E->T E'
E'->+T E'
E'->-T E'
E'->&T E'
E'->null
T->F T'
T'->*F T'
T'->/F T'
T'->null
F->(E)
F->id
F->int8
F->int10
F->int16
```
