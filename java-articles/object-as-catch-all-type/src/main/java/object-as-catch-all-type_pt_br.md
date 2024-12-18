<!--
## Por que não é uma boa prática usar 'Object' como um tipo genérico em Java?
[PT-BR] Por que não é uma boa prática usar 'Object' como um tipo genérico em Java?
-->
Caso queira conferir esse conteúdo em inglês, fique a vontade clicando aqui: **[The Challenges of Using 'Object' as a Catch-All Type in Java](https://dev.to/dearrudam/the-challenges-of-using-object-as-a-catch-all-type-in-java-43le)**.

Durante uma sessão de mentoria, conversando sobre Generics em Java com o desenvolvedor mentorado, percebemos que alguns conceitos precisam ser dominados para trazer mais valor quanto as motivações e o real valor na utilização de Generics no Java. Daí surgiu a pergunta: "Por que não é uma boa prática usar `Object` como um tipo genérico em Java?"

Achei esta pergunta é muito interessante e é por isso que estou abordando esse assunto neste conteúdo.

Muito bem, vamos lá!

Como um desenvolvedor Java, você deve saber que `java.lang.Object` é a raiz da hierarquia de classes na linguagem. Toda classe herda de `Object`, incluindo arrays. Isso significa que todos os objetos são, por padrão, instâncias de `Object`.

![`java.lang.Object` é a raiz da hierarquia de classes na linguagem](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/tjpmiis6zga4zizm3958.png)

Bem, se toda classe herda de `Object`, por que não é uma boa prática usar `Object` como um tipo genérico em Java? Vamos conferir!

### Usando Object para Armazenar Qualquer Tipo de Objeto

Quando declaramos variáveis, você pode tipar a variável como `Object` como para armazenar qualquer objeto quando o seu tipo específico é desconhecido.

```java
    Object infoA = "Maximillian"; // Isso funciona porque String é um Object!
Object infoB = 45; // Isso funciona, porque Integer é um Object!
```

Tais declarações podem compor um programa Java válido, como mostrado abaixo:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = "Maximillian"; // Isso funciona, pois String é um Object!
        System.out.println(infoA);
    }
}
```

A saida deste programa será:

```shell
$ java ObjectAsCatchAllTypeProgram.java
Maximillian
```

Isso acontece porque `String` é uma subclasse de `Object`, logo o compilador não irá encontrar nenhum problema sintático. Além disso, você pode alterar o valor de `infoA` para um objeto de qualquer outro tipo, e o programa ainda continuará funcionando:


```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = 3.14; // Isso funciona porque Double é um Object!
        System.out.println(infoA);
    }
}
```

A saída será:

```shell
$ java ObjectAsCatchAllTypeProgram.java
3.14
```

Como podemos ver, o programa funciona independentemente do tipo de objeto atribuído à variável `infoA`.

Mas quais são os benefícios de usar `Object` como um tipo? Alguns desenvolvedores podem dizer: "Isso torna o código mais flexível e reutilizável." Mas será que isso é verdade?

### Limitações ao Usar Object: Falta de Segurança de Tipos

Sim, não existe uma "bala de prata" na área de desenvolvimento de software. Cada decisão tem seus prós e contras. Vamos explorar as limitações de usar `Object` como um tipo genérico.

Independente do tipo de objeto, você só pode interagir com ele através de sua **interface**. Neste contexto, a palavra "interface" significa os métodos que estão expostos através da classe de seu tipo.

Uma **classe** é a receita ou um modelo para os objetos derivados dessa tal classe. Quando você define uma classe, você está especificando seu tipo, a estrutura e o comportamento dos objetos criados a partir dela. A classe define os atributos (o estado, ou seja, seus dados) e métodos (comportamento) que os objetos terão. Esses métodos permitem que os objetos interajam entre si.

A classe `Object` inclui alguns métodos comuns, como `toString()`, `equals()` e `hashCode()`. Esses métodos são úteis em muitos casos, mas não são suficientes quando você deseja interagir com métodos específicos de uma classe particular.

Vamos supor que estamos armazenando um objeto `String` em uma variável declarada como `Object`, e queremos obter o comprimento do valor( a string) que foi atribuído. Veja o que acontece:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = "Maximillian"; // Isso funciona porque String é um Object, mas...
        System.out.println(infoA.length());
    }
}
```

Tentando executar o código acima, teremos o seguinte resultado:

```shell
$ java ObjectAsCatchAllTypeProgram.java
ObjectAsCatchAllTypeProgram.java:5: error: cannot find symbol
        System.out.println(infoA.length());
                                ^
  symbol:   method length()
  location: variable infoA of type Object
1 error
error: compilation failed
```

Quando você declara uma variável como `Object`, você perde a segurança de tipos, para os íntimos, **Type safety**. **Type safety** é uma característica do Java que impede que você atribua um objeto de um tipo a uma variável de outro tipo. Essa característica ajuda a detectar erros em tempo de compilação, tornando seu código mais confiável. No exemplo acima, o compilador não sabe que `infoA` é um instancia de um objeto do tipo `String`, então ele não permitirá que você chame o método `length()` nele, gerando assim um erro de compilação (**compilation error**).

### Limitações ao Usar Object: A necessidade de conversão de tipos explícita

Para interagir com um objeto como seu tipo específico, você precisa convertê-lo para esse tipo. A conversão, ou em "javanês", **Casting**, é o processo de converter um objeto de um tipo para outro. Em Java, você pode converter um objeto (fazer um **casting**) para um tipo de subclasse ou superclasse. Vamos converter `infoA` para `String` antes de chamar o método `length()` para corrigir o erro de compilação mostrado anteriormente:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = "Maximillian"; // Isso funciona porque String é um Object, mas...
        String name = (String) infoA;
        System.out.println(name.length());
    }
}
```

Com isso, a saída será:

```shell
$ java ObjectAsCatchAllTypeProgram.java
11
```

Neste ponto, você pode pensar: "Não é um grande problema; eu posso apenas fazer um casting do objeto para seu tipo antes de interagir com ele." Será? Mesmo para um codebase de médio pra grande?

### Limitações ao Usar Object: Propenso a erros em tempo de execução

Vamos explorar um cenário diferente:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = 11; // Isso funciona porque Integer é um Object, mas
        String name = (String) infoA;  // Isso não é um String, é um Integer!!!
        System.out.println(name.length());
    }
}
```

Executando este código, teremos:

```shell
$ java ObjectAsCatchAllTypeProgram.java
Exception in thread "main" java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String (java.lang.Integer and java.lang.String are in module java.base of loader 'bootstrap')
        at ObjectAsCatchAllTypeProgram.main(ObjectAsCatchAllTypeProgram.java:4)
```

Um **ClassCastException** foi lançado porque estamos tentando fazer um cast de um objeto `Integer` para um objeto `String`. `Integer` não é uma subclasse de `String`, então o cast falha. Este tipo de erro é o que chamados de **erro em tempo de execução**, ou **runtime error**.

Vamos explorar outro caso:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = null; // Oops, estamos atribuindo null, e agora...?!?!?
        String name = (String) infoA;
        System.out.println(name.length());
    }
}
```

E como resultado, teremos:

```shell
$ java ObjectAsCatchAllTypeProgram.java
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "<local2>" is null
        at ObjectAsCatchAllTypeProgram.main(ObjectAsCatchAllTypeProgram.java:5)
```

Um **NullPointerException** ocorre porque estamos tentando chamar um método a partir de uma variável onde seu valor é uma referência nula.

Você pode pensar: "Eu posso facilmente corrigir isso usando um bloco `try-catch` ou o operador `instanceof`." Vamos tentar lidar com isso usando ambos os métodos:

Usando um bloco `try-catch`:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = null; // Oops, estamos atribuindo null, e agora...?!?!?
        try {
            String name = (String) infoA;
            System.out.println(name.length());
        } catch (NullPointerException ex) {
            System.out.println("infoA não pode ser convertido pois seu valor é null");
        } catch (ClassCastException ex) {
            System.out.println("infoA não pode ser convertido para String");
        }
    }
}
```

Output:

```shell
$ java ObjectAsCatchAllTypeProgram.java
infoA não pode ser convertido pois seu valor é null
```

Usando o operador `instanceof`:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = null; // Oops, estamos atribuindo null, e agora...?!?!?
        if (infoA instanceof String) {
            String name = (String) infoA;
            System.out.println(name.length());
        } else {
            System.out.println("infoA não pode ser convertido para String");
        }
    }
}
```

Output:

```shell
$ java ObjectAsCatchAllTypeProgram.java
infoA não pode ser convertido para String
```

Estratégias para tratar esses cenários podem ajudar sim, mas elas não são tão práticas caso seja preciso utilizá-las toda vez que você precisar interagir com um objeto do tipo `Object`.

Usando o operador `instanceof` é uma boa prática quando você precisa verificar o tipo de um objeto antes de fazer um cast. No entanto, isso pode tornar seu código mais complexo e difícil de ler, pois é preciso além de passar pelo operador `instanceof` precisaremos fazer o casting. Já o bloco `try-catch` é útil para lidar com exceções, mas também pode tornar seu código mais verboso e difícil de manter.

Se você está usando Java 14 ou superior, podemos usar o recurso Pattern Matching for Instanceof ([JEP305](https://openjdk.java.net/jeps/305)). Esse aprimoramento de linguagem integrado nos ajuda a escrever um código melhor e mais legível.

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = null;
        if (infoA instanceof String name) { // muito mais elegante, não é? :-)
            System.out.println(name.length());
        } else {
            System.out.println("infoA não pode ser convertido para String");
        }
    }
}
```

Aqui está a saída:


```shell
$ java ObjectAsCatchAllTypeProgram.java
infoA não pode ser convertido para String
```

### Conclusão

Nós aprendemos que declarar variáveis como `java.lang.Object` é geralmente considerado uma má prática, a menos que haja uma razão específica e convincente. Usar `Object` sacrifica a segurança de tipos, a legibilidade e a manutenibilidade. Na maioria dos casos, é melhor usar um tipo mais específico para aproveitar ao máximo o sistema de tipagem forte do Java.

Durante nossa exploração, descobrimos que erros de compilação (**compilation error**s) e erros em tempo de execução (**runtime error**s) podem ocorrer ao usar `Object` como um tipo genérico. Vamos recapitular as diferenças entre esses dois tipos de erros:

- Erros de compilação (**compilation errors**) ocorrem durante a fase de compilação quando o código não pode ser convertido em bytecode. Esses erros previnem que o programa seja compilado.

- Errors de tempo de execução (**runtime errors**) ocorrem após a compilação bem-sucedida e podem fazer com que o programa se comporte de forma imprevisível ou falhe. Erros de tempo de execução são tipicamente mais problemáticos porque podem afetar ambientes de produção.

Ambos os tipos de erros indicam problemas, mas os **erros em tempo de execução** são geralmente mais graves porque podem afetar os usuários e causar comportamentos inesperados, enquanto os **erros de compilação** são mais fáceis de resolver durante o processo de desenvolvimento do software.

Uma boa prática é implementar uma boa estratégia de tratamento de erros (**Error Handler**) para lidar com erros em tempo de execução, capturando as exceções e registrando-as corretamente para ajudá-lo a depurar e corrigir os problemas quando eles surgirem (e vão surgir!).

### Lições Aprendidas

Através deste conteúdo, exploramos os desafios de usar `Object` como um tipo genérico. Aprendemos que usar `Object` pode levar a:

- **Lack of type safety** (Falta de segurança de tipos): Se o compilador não sabe o tipo específico de um objeto declarado como `Object`, logo ele não pode detectar erros relacionados ao tipo em tempo de compilação. Isso pode levar a erros em tempo de execução ao interagir com os objetos do tipo `Object`.

- **A necessidade de conversão de tipos explícita** : Para interagir com um objeto como seu tipo específico, você precisa convertê-lo para esse tipo. Fazer isso em demasia espalhando essas instruções podem tornar seu código mais complexo e difícil de ler.

- **Propenso a erros em tempo de execução**: Usar `Object` como um tipo genérico pode levar a erros em tempo de execução, como `ClassCastException` e `NullPointerException`. Esses erros podem ser difíceis de rastrear, depurar e corrigir, especialmente em codebases grandes.

- **Necessidade adicional de tratamento de erros**: Para prevenir erros em tempo de execução, você pode precisar usar blocos `try-catch` ou o operador `instanceof`. Embora essas estratégias possam ajudar, elas podem tornar seu código mais verboso e difícil de manter. Recomendo usar uma boa estratégia de tratamento de erros para lidar com erros em tempo de execução, capturando as exceções e registrando-as corretamente para ajudá-lo a depurar e corrigir os problemas.

### Considerações finais

Nem sempre usar `Object` como um tipo genérico é a melhor solução. Em alguns casos, usar `Object` pode ser mais apropriado. Por exemplo, quando você não tem controle sobre o tipo de objeto que será manipulado. No entanto, é importante entender as limitações e desafios associados ao uso de `Object` e saber quando é apropriado usá-lo.

Quando questões sobre o que é certo ou errado na área de desenvolvimento de software surgem em qualquer discussão, costumamos ver desenvolvedores respondendo assim: "Ah, isso depende do contexto com o qual você está trabalhando." E sim, eles estão certos!

Mas, uma vez que você conhece o contexto, "depende" não é mais uma resposta tão válida assim. No fim, o contexto do problema deve ser utilizado para nos guiar a tomar uma melhor decisão!

Então, favoreça tipos mais específicos sempre que possível para aproveitar ao máximo o poder do sistema de tipagem do Java. Isso ajudará você a escrever um código mais legível, manutenível e confiável.

O que você acha de usar `Object` como um tipo genérico em Java? Você tem alguma experiência ou melhores práticas para compartilhar? Sinta-se à vontade para deixar seus pensamentos nos comentários abaixo!


### Próximos passos

Parabéns por chegar ao final deste conteúdo! Espero que você tenha achado informativo e útil.

Os conceitos aprendidos neste conteúdo são essenciais para entender a motivação por trás do uso de Generics no Java. Generics é um recurso poderoso que permite escrever um código mais flexível e seguro em relação ao tipo, fornecendo verificação de tipo em tempo de compilação.

Gosto deste conteúdo? Se sim, por favor, compartilhe com seus amigos e colegas. Estou aceitando sugestões de assuntos para os próximos e futuros conteúdos, então, sinta-se à vontade para sugerir nos comentários, ok?

Também não se esqueça de me seguir nas redes sociais para ficar atualizado com os últimos conteúdos e atualizações.

Até a próxima!