<!--
## Facilitando a vida dos desenvolvedores: implementando construtores de objetos inteligentes
-->

Costumamos a ouvir "Facilite a vida do seu cliente". Mas, e quanto aos desenvolvedores que vão trabalhar com o código que você escreve? Com certeza é importante facilitar a vida deles também.

Independente do cliente alvo da sua solução, você estará criando código que será mantido por outros desenvolvedores incluindo você também muitas vezes. Escolher boas abordagens e técnicas irá facilitar a vida deles ou a sua vida provavelmente. Ser um desenvolvedor eficaz não é apenas sobre escrever código que funcione, mas também sobre escrever código que seja fácil de ler, de entender e de manter.

O tema sobre boas práticas e técnicas é muito extenso e, eu não estou aqui para dizer o que você deve ou não fazer. Eu acredito que a melhor abordagem depende do contexto.

Neste conteúdo, vamos discutir como facilitar a vida dos desenvolvedores usando boas estratégias para construir objetos complexos.

### O cenário: O objeto complexo

Uma boa maneira de aprender coisas é através de exemplos. Então, aqui está o nosso desafio:

Precisamos criar um objeto `Notification` que tenha os seguintes atributos obrigatórios:

- `title`: o título da notificação;
- `message`: o conteúdo da notificação;
- `recipient`: a pessoa que receberá a notificação;

e os atributos opcionais:

- `highPriority`: uma flag para indicar se a notificação é de alta prioridade. O padrão é `false`;
- `type`: o tipo da notificação. Uma enumeração com os seguintes valores suportados: `GENERAL`, `INFO`, `WARNING`, `ERROR`. O padrão é `Type.GENERAL`;
- `attachment`: um texto com o caminho para o arquivo de anexo. O padrão é `null`;

Abaixo, segue mais requisitos que precisamos atender:

- `title`: é obrigatório e não pode ser nulo;
- `message`: é obrigatório e não pode ser nulo;
- `recipient`: é obrigatório e não pode ser nulo;
- `highPriority`: é opcional, mas não pode ser nulo;
- `type`: é opcional, mas não pode ser nulo;

Temos aqui uma inicial definição para nossa classe `Notification`:

```java
public class Notification {

    private String title; // mandatory
    private String message; // mandatory
    private String recipient; // mandatory
    private boolean isHighPriority; // optional
    private Type type; // optional
    private String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    // omitted getters
}


```

Alguns desenvolvedores poderiam argumentar: _"Podemos usar o construtor padrão e os setters para definir os atributos opcionais"_. Bom, vamos tentar seguir este argumento:

```java
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.Objects.requireNonNull;

public class Notification {

    private String title; // mandatory
    private String message; // mandatory
    private String recipient; // mandatory
    private boolean highPriority; // optional
    private Type type = Type.GENERAL; // optional
    private String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    public void setTitle(String title) {
        this.title = requireNonNull(title, "title is required");
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = requireNonNull(message, "message is required");
    }

    public String getMessage() {
        return message;
    }

    public void setRecipient(String recipient) {
        this.recipient = requireNonNull(recipient, "recipient is required");
    }

    public String getRecipient() {
        return recipient;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Optional<Type> getType() {
        return ofNullable(type);
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public  Optional<String> getAttachment() {
        return attachment;
    }

    // omitted hash and equals methods 
}
```

Analisando o código acima, podemos ver que o desenvolvedor deve chamar os setters para definir os atributos. O código para criar um objeto `Notification` provavelmente seria assim:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        Notification notification = new Notification();
        notification.setTitle("New message");
        notification.setMessage("Hello, world!");
        notification.setRecipient("johndoe@system.com");
        notification.setHighPriority(true);
        notification.setType(Notification.Type.INFO);
        notification.setAttachment("/path/to/attachment.txt");
    }
}
```

No código acima, podemos destacar algumas desvantagens e problemas com essa abordagem:

1. **Objetos `Notification` podem ser instanciados com estado inválido**: as restrições do nosso desafio estão sendo violadas.

2. **Objetos `Notification` não são thread-safe**: os setters podem ser chamados por múltiplas threads ao mesmo tempo, causando problemas de condições de corrida (race conditions), um problema muito comum que podem ocorrer em aplicações multithreaded.

3. **Os desenvolvedores devem chamar os setters para definir tanto os atributos obrigatórios quanto os opcionais**: é um processo um tanto verboso e **propenso a erros** porque o desenvolvedor pode esquecer de definir algum de seus atributos, fazendo com que o objeto entre em um estado inválido;

4. **Não está claro como o objeto deve ser criado**: o desenvolvedor deverá ler a documentação para saber quais atributos são obrigatórios e quais são opcionais para assim poder defini-los;

Mas também, podemos destacar algumas coisas boas nesta abordagem: **os desenvolvedores podem chamar os setters dos atributos opcionais conforme necessário, dando muita flexibilidade na utilização**.

Dados os problemas acima, vamos tentar endereçá-los:

1. **Objetos `Notification` podem ser instanciados com estado inválido**: as restrições do nosso desafio estão sendo violadas.

Okay, você poderia dizer: "_Não é um grande problema! Podemos usar o construtor para definir os atributos obrigatórios e os setters para definir os atributos opcionais_". Vamos tentar seguir este argumento:

```java
import static java.util.Optional.ofNullable;
import static java.util.Objects.requireNonNull;

public class Notification {

    private String title; // mandatory
    private String message; // mandatory
    private String recipient; // mandatory
    private boolean highPriority; // optional
    private Type type = Type.GENERAL; // optional
    private String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    public Notification(String title, String message, String recipient) {
        setTitle(title);
        setMessage(message);
        setRecipient(recipient);
    }

    public void setTitle(String title) {
        this.title = requireNonNull(title, "title is required");
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = requireNonNull(message, "message is required");
    }

    public String getMessage() {
        return message;
    }

    public void setRecipient(String recipient) {
        this.recipient = requireNonNull(recipient, "recipient is required");
    }

    public String getRecipient() {
        return recipient;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Optional<Type> getType() {
        return ofNullable(type);
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public  Optional<String> getAttachment() {
        return attachment;
    }

    // omitted hash and equals methods 
}
```

Vamos atualizar o `NotificationProgram` que cria um objeto `Notification`:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        Notification notification = new Notification("New message", "Hello, world!", "johndoe@system.com");
        notification.setHighPriority(true);
        notification.setType(Notification.Type.INFO);
        notification.setAttachment("/path/to/attachment.txt");
    }
}
```

Aparentemente resolvemos o primeiro problema, certo? Bem, vamos analisar o código novamente:

Com as mudanças os objetos `Notification` serão instanciados com referências não nulas para os atributos obrigatórios. Mas, ainda temos alguns problemas: **o construtor `Notification` com todos os atributos obrigatórios é propenso a erros!** Agora, os desenvolvedores podem definir os atributos na ordem errada. Por exemplo, o desenvolvedor pode definir o `recipient` antes do `title`, o que invalida o estado do objeto fazendo com que os campos contenham o valor errado. Tal problema só poderá ser detectado em um processo de depuração ou analisando a saída derivada desses objetos `Notification` inválidos.

Talvez dois ou três argumentos com o mesmo tipo não seja um grande problema, mas na perspectiva dos desenvolvedores que irão usar nossa classe, não é algo fácil de lidar, e se tivermos mais atributos? O construtor se tornará ainda mais complexo e propenso a erros.

Vamos continuar, mas agora tentando resolver o segundo problema:

2. **Objetos `Notification` não são thread-safe**: os setters podem ser chamados por múltiplas threads ao mesmo tempo, causando problemas de condições de corrida (race conditions), um problema muito comum que podem ocorrer em aplicações multithreaded.

A forma mais fácil de tornar os objetos `Notification` thread-safe é tornar os getters e setters `synchronized` usando `Locks` do pacote `java.util.concurrent.locks`. Isso funcionaria, mas tem algumas desvantagens que precisamos considerar:

- **Não é escalável e propenso a erros**: quando for necessário adicionar mais atributos, os desenvolvedores devem garantir que qualquer estado de escrita e leitura será `synchronized`, o que pode levar a problemas de desempenho e deadlock de threads indesejados;
- **É uma abordagem verbosa**: o desenvolvedor deve escrever muito código para tornar o objeto thread-safe.
- **Não é eficiente**: sincronizar o acesso para ler e escrever dados pode causar problemas de desempenho em aplicações multithreaded.
- **Não temos clareza sobre como o objeto deve ser criado**: o desenvolvedor deve ler a documentação para saber quais atributos são obrigatórios e quais são opcionais para assim poder defini-los;

Todas essas preocupações são necessárias apenas pelo fato que essa classe é base para instâncias de objetos mutáveis. Se o seu cenário requer objetos mutáveis, então faz sentido colocar esforço para lidar com todas essas preocupações. Caso contrário, se o seu cenário permitir que você use objetos imutáveis, então você pode evitar todas essas preocupações.

Por esse motivo precisamos ter clareza sobre os requisitos da sua solução antes de começar a codificar. Isso ajudará você a escolher a melhor abordagem para resolver o problema. Vamos voltar para a nossa classe `Notification` e tentar torná-la imutável.

Objetos imutáveis são seguros para threads por natureza porque não podem ser modificados após a criação. É uma boa prática tornar seus objetos imutáveis sempre que possível.

Podemos então usar construtores específicos para tornar o objeto `Notification` imutável. Veja abaixo:

```java
import static java.util.Optional.ofNullable;
import static java.util.Objects.requireNonNull;

public class Notification {

    private final String title; // mandatory
    private final String message; // mandatory
    private final String recipient; // mandatory
    private final boolean highPriority; // optional
    private final Type type; // optional
    private final String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    public Notification(String title,
                        String message,
                        String recipient,
                        boolean highPriority,
                        Type type,
                        String attachment) {
        this.title = requireNonNull(title, "title is required");
        this.message = requireNonNull(message, "message is required");
        this.recipient = requireNonNull(recipient, "recipient is required");
        this.highPriority = highPriority;
        this.type = ofNullable(type).orElse(Type.GENERAL);
        this.attachment = attachment;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipient() {
        return recipient;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public Optional<Type> getType() {
        return ofNullable(type);
    }

    public  Optional<String> getAttachment() {
        return attachment;
    }

    // omitted hash and equals methods 
}
```

Agora, vamos atualizar o `NotificationProgram` que cria um objeto `Notification`:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        Notification notification = new Notification(
                "New message",
                "Hello, world!",
                "johndoe@system.com",
                true,
                Notification.Type.INFO,
                "/path/to/attachment.txt");
    }
}
```

Agora os objetos `Notification` são imutáveis e seguros para threads. O desenvolvedor pode instanciar o objeto com todos os atributos obrigatórios e opcionais em uma única linha de código. O objeto será criado em um estado válido, e o desenvolvedor não pode alterar seu estado após a criação.

Desde o Java 16, podemos usar a palavra-chave `record` para criar objetos imutáveis, é o que chamamos Java Records. Se você estiver usando o Java 16 ou superior, eu recomendo fortemente que você use Java Records para criar objetos imutáveis. Vamos ver como podemos refatorar a classe `Notification` para se tornar um Java Record:

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }
    
    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }
}
```

Menos código, mais legibilidade e mais manutenível. Esse é o poder dos Java Records.

Voltando para nosso desafio, algo ainda não está certo: se `highPriority`, `type` e `attachment` são atributos opcionais, por que é necessário fornecer cada valor no construtor?

Podemos resolver esse problema usando uma abordagem tradicional chamada **Telescoping constructors**.

### Uma abordagem tradicional: Telescoping constructors

A common approach to object creation is to provide multiple constructors with different numbers of parameters. Each constructor calls application constructor with the required parameters and sets the optional parameters to default values. It's called **telescoping constructors**. You can use this approach on any java class, including Java Records.

Uma abordagem comum para a criação de objetos é fornecer vários construtores com diferentes números ou tipos de parâmetros. Cada construtor chama o construtor de aplicação com os parâmetros obrigatórios e define os parâmetros opcionais com valores padrão. Isso é o que chamamos de **telescoping constructors** (não encontrei nome em português aqui... se vc souber, mande nos comentários!). Você pode usar essa abordagem em qualquer classe Java, incluindo Java Records.

Vamos ver como ficará nossa classe utilizando essa abordagem:

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }
    
    public Notification (String title, String message, String recipient) {
        this(title, message, recipient, false, Type.GENERAL, null);
    }
    
    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }
}
```

Agora, os desenvolvedores poderão criar objetos `Notification` com apenas os atributos obrigatórios. Os atributos opcionais serão definidos com valores padrão. Lembrando que objetos `Notification` continuam sendo imutáveis e seguros para threads.

Vamos atualizar o `NotificationProgram` que cria um objeto `Notification`:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        var notificationWithDefaultOptionalValues =
                new Notification(
                        "New message", 
                        "Hello, world!", 
                        "johndoe@system.com");
        
        var notificationWithCustomOptionalValues =
                new Notification(
                        "Another message",
                        "Oh no! Something wrong happened",
                        "johndoe@system.com",
                        true,
                        Notification.Type.ERROR,
                        "/path/to/attachment.txt");
    }
}
```

Ótimo! Vamos revisar os problemas que tivemos e como os resolvemos:

1. **Objetos `Notification` somente podem ser instanciados com estado válido**: os construtores garantem que o objeto será criado em um estado válido;

2. **Objetos `Notification` são imutáveis e seguros para threads**: a imutabilidade garante que os objetos `Notification` são seguros para threads;

Bom, o terceiro problema sobre a verbosidade e propensão a erros dos construtores ainda existem. Quanto ao quarto item, podemos prover uma documentação que ajudará os desenvolvedores a saber qual construtor deve ser usado com a ordem dos argumentos. Mas nesse ponto, acredito que podemos fazer mais para facilitar a vida do desenvolvedor, não?

### Favoreça Static Factory Methods ao invés de Construtores

Para ajudar os desenvolvedores a saber qual construtor deve ser usado, podemos fornecer métodos fábrica estáticos (Static Factory Methods) para criar objetos.

Static factory methods são métodos estáticos que retornam uma instância de uma determinada classe ou subclasse. Eles podem ter nomes que podém auxiliar quais os atributos que precisam ser fornecidos para criar um determinado objeto, facilitando assim a vida dos desenvolvedores a criar tais objetos.

Talvez você já tenha ouvido falar sobre o padrão "Factory Method" do livro Design Pattern (Gang of Four) antes, mas o Static Factory Method não é uma implementação direta desse padrão. O propósito pode até ser o mesmo, mas não há um padrão equivalente no livro Gang of Four para os Static Factory Methods, na verdade.

Algumas vantagens de usar métodos fábrica estáticos são:

- **Static factory methods tem nomes que descrevem o objeto que está sendo retornado**: o desenvolvedor pode saber qual construtor deve ser usado lendo o nome do método;
- **Static factory methods não precisam criar um novo objeto em cada invocação**: eles podem retornar o mesmo objeto se o objeto for imutável, economizando memória e recursos da CPU;
- **Static factory methods podem retornar qualquer objeto do tipo e/ou seus subtipos**: eles podem retornar um objeto de um subtipo da classe, facilitando a criação de objetos com diferentes configurações.
- **Static factory methods podem retornar objetos tipo e seus subtipos dependendo dos argumentos de entrada fornecidos**: diferentes dos construtores de uma classe que apenas retornam uma instância do seu tipo criando uma nova instância toda vez que são chamados, os métodos fábrica estáticos podem aplicar lógicas específicas e retornar objetos do tipo solicitado ou seus subtipos.

Antes de por nossos dedos no código, vamos pensar sobre como aplicar os métodos fábrica estáticos no nosso desafio. De acordo com o conceito de métodos fábrica estáticos, podemos criar um método fábrica estático para cada combinação de atributos. Isso até facilitará a vida do desenvolvedor, mas como devemos implementar esses métodos fábrica estáticos?

De fato, para nosso desafio, se concentrarmos em fornecer métodos fábrica estáticos para todas as combinações possíveis, resultaríamos em uma classe grande com muitos métodos fábrica estáticos. São cerca de 16 variações de métodos fábrica estáticos! Talvez não seja uma boa ideia ter muitos métodos fábrica estáticos em uma classe. Possivelmente isso irá tornar a classe mais difícil de entender e consequentemente de manter. Vamos mudar nosso ponto de vista: em vez de cobrir estaticamente todas as combinações possíveis, podemos fornecer métodos fábrica estáticos com os atributos permitindo assim realizar as possíveis combinações. Vamos ver como ficaria.

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@lombok.Builder
public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public static Notification createNotification(Type type, String title, String message, String recipient) {
        return new Notification(title, message, recipient, false, type, null);
    }

    public static Notification createHighPriorityNotification(Type type, String title, String message, String recipient) {
        return new Notification(title, message, recipient, true, type, null);
    }

    public static Notification createNotificationWithAttachment(Type type, String title, String message, String recipient, String attachment) {
        return new Notification(title, message, recipient, false, type, attachment);
    }

    public static Notification createHighPriorityNotificationWithAttachment(Type type, String title, String message, String recipient, String attachment) {
        return new Notification(title, message, recipient, true, type, attachment);
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }
    
}
```

Agora, desenvolvedores podem criar objetos `Notification` usando métodos fábrica estáticos. Os métodos fábrica estáticos têm nomes que descrevem quais os atributos que definiram o objeto que está sendo retornado, facilitando assim a vida dos desenvolvedores a saber qual construtor deve ser usado. E como estamos ainda utilizando Java Records, os objetos `Notification` continuam sendo imutáveis e seguros para threads.

Vamos atualizar o `NotificationProgram` que cria um objeto `Notification`:

```java

public class NotificationProgram {

    public static void main(String[] args) {
        var generalNotificationWithoutAttachment = Notification
                .createNotification(
                        Notification.Type.GENERAL,
                        "General Notification",
                        "This is a general notification",
                        "johndoe@system.com");

        var highPrioryInfoNotification = Notification
                .createHighPriorityNotification(
                        Notification.Type.INFO,
                        "High Priority Info Notification",
                        "This is a high priority info notification",
                        "johndoe@system.com");
    }
}
```

Que legal! Estamos melhorando nosso código passo a passo. Talvez a forma como nossa classe foi implementada até o momento esteja okay para alguns casos, mas acredito que podemos fazer melhor!

Em nossa implementação, cada método fábrica estático está exigindo quatro argumentos. Não é um grande problema, mas e se tivermos mais atributos? Os métodos fábrica estáticos se tornarão ainda mais complexos e propensos a erros. Vamos ver como podemos resolver esse problema.

### Muitos parâmetros? Use o padrão Builder

The Builder pattern is a creational design pattern that allows you to construct complex objects step by step. It's useful when you have many optional attributes in your class and you want to make the object creation more readable and maintainable.

O padrão Builder é um padrão de design criacional que permite construir objetos complexos passo a passo. É útil quando você tem muitos atributos em sua classe e deseja tornar a criação do objeto mais legível e manutenível.

Algumas biliotecas como Lombok, ou até plugins de IDEs como IntelliJ IDEA, podem gerar Builders para você. Isso é incrível, mas precisamos entender como ele funciona para poder usá-lo efetivamente.

Vamos ver como podemos implementar o padrão Builder para nossa classe `Notification` usando Lombok, por exemplo:

```java
import lombok.Builder;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@Builder
public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }
}
```

Por baixos dos panos, o Lombok irá gerar para você todas as classes de builder para a classe `Notification`. No final, teremos um resultado similar ao abaixo:

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static NotificationDataBuilder builder() {
        return new NotificationDataBuilder();
    }

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }

    public static class NotificationBuilder {
        private String title;
        private String message;
        private String recipient;
        private boolean highPriority;
        private Type type;
        private String attachment;

        NotificationBuilder() {
        }

        public NotificationDataBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationDataBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationDataBuilder recipient(String recipient) {
            this.recipient = recipient;
            return this;
        }

        public NotificationDataBuilder highPriority(boolean highPriority) {
            this.highPriority = highPriority;
            return this;
        }

        public NotificationDataBuilder type(Type type) {
            this.type = type;
            return this;
        }

        public NotificationDataBuilder attachment(String attachment) {
            this.attachment = attachment;
            return this;
        }

        public Notification build() {
            return new Notification(title, message, recipient, highPriority, type, attachment);
        }
    }
}
```

O Lombok fornece muitas anotações para gerar código boilerplate para você. A anotação `@Builder` gera uma classe builder para a classe anotada. A classe builder gerada pelo Lombok tem uma interface fluente onde os desenvolvedores podem chamar de forma encadeada os métodos para definir os atributos da classe anotada e um método `build()` para criar uma instância da classe anotada.

Builders podem ser implementados de várias maneiras, como usando o estilo tradicional utilizando métodos setter, mas é comum implementá-los seguindo o estilo de design **Fluent API**.

O estilo de design **Fluent API** é enfatizado pela chamada encadeada de métodos. Ele permite que os desenvolvedores chamem métodos de forma encadeada, tornando o código mais legível e manutenível. É usado em muitas bibliotecas, frameworks e APIs para melhorar a experiência do desenvolvedor. Pode ser aplicado ao padrão Builder com certeza, mas não se limita a esse uso. Pode ser usado em **DSLs (Domain-Specific Languages)** e muitos outros contextos.

Particularmente eu prefiro ter essas classes explicitamente no meu código. Isso me ajuda a entender como o padrão Builder funciona e, o mais importante na minha opinião: ele libera os desenvolvedores de terem o Lombok configurado em suas IDEs. Uma dependência a menos para se configurar e se preocupar!

Vamos ver a flexibilidade que o padrão Builder fornece aos desenvolvedores:

```java
public class NotificationProgram {
    public static void main(String[] args) {
        var generalNotification = Notification.builder()
                .title("Hello")
                .message("Hello World")
                .recipient("johndoe@system.com")
                .build();

        // do something with generalNotification

        var highPriorityInfoNotificationWithAttachment = Notification.builder()
                .title("Hello")
                .message("Hello World")
                .recipient("johndoe@system.com")
                .type(Type.INFO)
                .highPriority(true)
                .attachment("attachment.pdf")
                .build();
        
        // do something with highPriorityInfoNotificationWithAttachment;
    }
}
```

Agora, desenvolvedores poderão criar objetos `Notification` usando o padrão Builder. O padrão Builder permite que os desenvolvedores construam objetos complexos passo a passo, tornando a criação do objeto mais legível e manutenível.

Bom, tal builder como esses podem até ajudar facilitar a vida de alguns desenvolvedores, principalmente os desenvolvedores que estão criando o builder, mas e quanto aos desenvolvedores que irão usar o builder?

Mas o que você quer dizer com isso? - você pode perguntar. É essa é uma ótima pergunta!

Antes de adicionar a solução do builder na classe `Notification`, os desenvolvedores precisavam passar os argumentos obrigatórios para os métodos fábrica estáticos para criar objetos `Notification`. O compilador Java irá forçar o desenvolvedor a passar os argumentos obrigatórios e necessários para os métodos fábrica estáticos para criar objetos `Notification` com estado válido. A solução builder que adicionamos a nossa classe não fornece essa capacidade de maneira padrão. Veja o código abaixo:

```java
public class NotificationProgram {
    public static void main(String[] args) {
        var anotherNotification = Notification.builder()
                .recipient("johndoe@system.com")
                .build();
    }
}
```

Você pode dizer: "_Não é um grande problema! A classe respeitará suas restrições e nenhuma instância inválida será criada! Ele lançará exceções para o chamador!_". Sim, é verdade, mas essas mesmas exceções serão lançadas apenas em tempo de execução. E isso não é bom para ninguém!

Erros em tempo de execução explodem em produção, e esses, quando não tratados, afetam a imagem e percepção do cliente final da solução. Isso exigirá uma maneira inteligente de lidar com esses cenários, forçando os desenvolvedores a espalhar a lógica de tratamento de erros em cada ponto que irá utilizar esse código. Espalhar código e forçar esse cenário não é uma boa prática!

Em resumo, erros de compilação (compilation errors) ou de tempo de execução (runtime errors) ainda demonstram que existem problemas na solução, mas erros de compilação ajudam os desenvolvedores a descobrir problemas em tempo de compilação, o que é melhor! Vamos tentar usar o padrão Builder para impor as restrições da classe `Notification` em tempo de compilação.

### Restringindo a ordem das chamadas de métodos no padrão Builder

O padrão Builder permite que os desenvolvedores construam objetos complexos passo a passo. O padrão Builder pode ser usado para impor as restrições da classe em tempo de compilação.

Nossa implementação do Builder não restringe a ordem das chamadas de métodos. O desenvolvedor pode chamar os métodos em qualquer ordem, o que pode levar a objetos inválidos. Isso acontece porque o `NotificationBuilder` expõe todos os atributos para serem definidos pelo desenvolvedor de maneira livre. Com isso, não conseguimos exigir que os métodos obrigatórios sejam chamados e assim garantir que os atributos obrigatórios tenham sido definidos. Para restringir essa ordem das chamadas de métodos, podemos estar utilizando uma variação do padrão Builder, o **padrão Step Builder**.

Primeiro, vamos quebrar o processo realizado pelo `NotificationBuilder` em alguns passos. Cada passo será responsável por definir um grupo específico de atributos. Aqui está nosso plano:

* Vamos garantir que `title`, `message` e `recipient` sejam definidos nessa ordem específica; Às vezes é importante seguir uma ordem predefinida durante a instanciação de um objeto. Na verdade, esse não é o nosso caso, no entanto, para fins de aprendizado, vamos fazer dessa forma. Uma vez que esses atributos obrigatórios são definidos, vamos permitir que o desenvolvedor possa construir o objeto `Notification` com os valores padrão para os atributos opcionais, ou...

* Vamos permitir que os desenvolvedores definam `highPriority`, `type` e `attachment` em qualquer ordem. Como esses atributos são opcionais, devemos permitir que os desenvolvedores possam construir o objeto `Notification` a qualquer momento a partir deste ponto;

Vamos ver como ficará:

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public Notification(String title, String message, String recipient) {
        this(title, message, recipient, false, null, null);
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static final class NotificationBuilder {

        public NotificationBuilderWithTitle title(String title) {
            return new NotificationBuilderWithTitle(title);
        }

    }

    public record NotificationBuilderWithTitle(String title) {

        public NotificationBuilderWithTitleMessage message(String message) {
            return new NotificationBuilderWithTitleMessage(this.title, message);
        }
    }

    public record NotificationBuilderWithTitleMessage(String title, String message) {

        public NotificationBuilderWithTitleMessageRecipient recipient(String recipient) {
            return new NotificationBuilderWithTitleMessageRecipient(this.title, this.message, recipient);
        }
    }

    public record NotificationBuilderWithTitleMessageRecipient(String title,
                                                               String message,
                                                               String recipient) {

        public Notification build() {
            return new Notification(title, message, recipient);
        }
    }
    
    public static final class NotificationBuilder {

        public NotificationBuilderWithTitle title(String title) {
            return new NotificationBuilderWithTitle(title);
        }

    }

    public record NotificationBuilderWithTitle(String title) {

        public NotificationBuilderWithTitleMessage message(String message) {
            return new NotificationBuilderWithTitleMessage(this.title, message);
        }
    }

    public record NotificationBuilderWithTitleMessage(String title, String message) {

        public NotificationBuilderWithTitleMessageRecipient recipient(String recipient) {
            return new NotificationBuilderWithTitleMessageRecipient(this.title, this.message, recipient);
        }
    }

    public record NotificationBuilderWithTitleMessageRecipient(String title,
                                                               String message,
                                                               String recipient) {

        public Notification build() {
            return new Notification(title, message, recipient);
        }
    }
}
```

Agora, desenvolvedores devem seguir a ordem das chamadas de métodos para criar um objeto `Notification`. A classe `NotificationBuilderWithTitle` é responsável por definir o atributo `title`. A classe `NotificationBuilderWithTitleMessage` é responsável por definir o atributo `message`. A classe `NotificationBuilderWithTitleMessageRecipient` é responsável por definir o atributo `recipient`. A classe `NotificationBuilderWithTitleMessageRecipient` tem um método `build()` para criar um objeto `Notification`. Vamos destacar alguns pontos:

1. **Todos os objetos criados pelo processo de construção são seguros para threads**, o que significa que os desenvolvedores podem criar objetos `Notification` em um ambiente multithreaded sem problemas;
2. **Os métodos oferecidos pelos objetos builder são nomeados**, o que significa que os desenvolvedores podem saber qual método deve ser chamado em seguida para criar um objeto `Notification`;
3. **A ordem das chamadas de métodos é imposta pelo padrão Builder**, o que significa que os desenvolvedores devem seguir a ordem das chamadas de métodos para criar um objeto `Notification` e o compilador irá impor essa restrição;
4. **Esse builder prove uma API fluente**, o que significa que os desenvolvedores podem criar objetos `Notification` de uma maneira legível e manutenível.

Que massa! Vamos continuar a implementação para suportar a definição dos atributos opcionais.

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public Notification(String title, String message, String recipient) {
        this(title, message, recipient, false, null, null);
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static final class NotificationBuilder {

        public NotificationBuilderWithTitle title(String title) {
            return new NotificationBuilderWithTitle(title);
        }

    }

    public record NotificationBuilderWithTitle(String title) {

        public NotificationBuilderWithTitleMessage message(String message) {
            return new NotificationBuilderWithTitleMessage(this.title, message);
        }
    }

    public record NotificationBuilderWithTitleMessage(String title, String message) {

        public NotificationBuilderWithTitleMessageRecipient recipient(String recipient) {
            return new NotificationBuilderWithTitleMessageRecipient(this.title, this.message, recipient);
        }
    }

    public record NotificationBuilderWithTitleMessageRecipient(String title,
                                                               String message,
                                                               String recipient) {

        public Notification build() {
            return new Notification(title, message, recipient);
        }
    }
    
    public static final class NotificationBuilder {

        public NotificationBuilderWithTitle title(String title) {
            return new NotificationBuilderWithTitle(title);
        }

    }

    public record NotificationBuilderWithTitle(String title) {

        public NotificationBuilderWithTitleMessage message(String message) {
            return new NotificationBuilderWithTitleMessage(this.title, message);
        }
    }

    public record NotificationBuilderWithTitleMessage(String title, String message) {

        public NotificationBuilderWithTitleMessageRecipient recipient(String recipient) {
            return new NotificationBuilderWithTitleMessageRecipient(this.title, this.message, recipient);
        }
    }

    public record NotificationBuilderWithTitleMessageRecipient(String title,
                                                               String message,
                                                               String recipient) {

        public Notification build() {
            return new Notification(title, message, recipient);
        }

        public NotificationBuilderWithTitleMessageRecipientAndMore addMore() {
            return new NotificationBuilderWithTitleMessageRecipientAndMore(
                    this.title,
                    this.message,
                    this.recipient
            );
        }
    }

    public record NotificationBuilderWithTitleMessageRecipientAndMore(String title,
                                                                      String message,
                                                                      String recipient,
                                                                      Type type,
                                                                      boolean highPriority,
                                                                      String attachment) {

        public NotificationBuilderWithTitleMessageRecipientAndMore(String title, String message, String recipient) {
            this(title, message, recipient, null, false, null);
        }

        public NotificationBuilderWithTitleMessageRecipientAndMore highPriority(boolean highPriority) {
            return new NotificationBuilderWithTitleMessageRecipientAndMore(
                    this.title,
                    this.message,
                    this.recipient,
                    this.type,
                    highPriority,
                    this.attachment
            );
        }

        public NotificationBuilderWithTitleMessageRecipientAndMore attachment(String attachment) {
            return new NotificationBuilderWithTitleMessageRecipientAndMore(
                    this.title,
                    this.message,
                    this.recipient,
                    this.type,
                    this.highPriority,
                    attachment
            );
        }

        public NotificationBuilderWithTitleMessageRecipientAndMore type(Type type) {
            return new NotificationBuilderWithTitleMessageRecipientAndMore(
                    this.title,
                    this.message,
                    this.recipient,
                    type,
                    this.highPriority,
                    this.attachment
            );
        }

        public Notification build() {
            return new Notification(
                    this.title,
                    this.message,
                    this.recipient,
                    this.highPriority,
                    this.type,
                    this.attachment
            );
        }
    }
    
}

```

Agora, após definir os atributos obrigatórios, os desenvolvedores podem criar objetos `Notification` definindo os atributos opcionais em qualquer ordem. A classe `NotificationBuilderWithTitleMessageRecipientAndMore` é responsável por definir os atributos opcionais e criar o objeto `Notification`. Nesse ponto, os desenvolvedores podem definir os atributos opcionais ou chamar o método `build()` para criar um objeto `Notification` arbitrariamente. Vamos destacar alguns pontos:

1. **O builder permite que os desenvolvedores definam os atributos opcionais em qualquer ordem**, o que significa que os desenvolvedores podem criar objetos `Notification` com os atributos opcionais em qualquer ordem;

2. **O builder permite que os desenvolvedores criem objetos `Notification` arbitrariamente**, o que significa que os desenvolvedores podem definir os atributos opcionais ou criar o objeto `Notification` em qualquer ponto do processo de construção;

Vamos atualizar o `NotificationProgram` que cria um objeto `Notification` usando o builder que implementamos:

```java
import notification.Notification;

public class NotificationProgram {

    public static void main(String[] args) {
        var generalNotification = Notification.builder()
                .title("Another title")
                .message("Another message")
                .recipient("johndoe@system.com")
                .build();

        // do something with generalNotification

        var highPriorityWarningNotification = Notification.builder()
                .title("Warning title")
                .message("Attention people!")
                .recipient("johndoe@sytem.com")
                .addMore()
                .highPriority(true)
                .type(Notification.Type.WARNING)
                .build();

        // do something with highPriorityWarningNotification

        var highPriorityErrorNotificationWithAttachment = Notification.builder()
                .title("Warning title")
                .message("Attention people!")
                .recipient("johndoe@sytem.com")
                .addMore()
                .type(Notification.Type.ERROR)
                .attachment("error.log")
                .highPriority(true)
                .build();

        // do something with highPriorityErrorNotificationWithAttachment
    }

}
```

Essa implementação do builder vai além do tradicional padrão Builder.

Como podemos ver no código anterior, os desenvolvedores podem criar objetos `Notification` com os atributos opcionais em qualquer ordem e, ao mesmo tempo, ele impõe as restrições da classe `Notification` em tempo de compilação forçando a definição dos atributos obrigatórios, tornando a criação do objeto mais legível e manutenível.

Como o tio Ben Parker costumava dizer - "Com grandes poderes vêm grandes responsabilidades" - implementar o padrão Builder dessa forma tornará o código complexo, tornando-o mais difícil de entender e alterar, provavelmente. É um trade-off que você deve considerar ao usar o padrão Builder.

Uma vez que você tenha que lidar com muitos atributos (sendo eles obrigatórios ou não) para criar objetos, o padrão Builder e suas variações podem ser uma boa escolha. Como o builder está recebendo ajuda do compilador, refatorar o código será mais fácil e seguro.

### Conclusão

Neste conteúdo, discutimos algumas abordagens para criar objetos com muitos atributos obrigatórios e opcionais. Começamos com a abordagem tradicional, usando construtores e setters para criar objetos. Vimos que essa abordagem pode levar a objetos inválidos, problemas de segurança de threads e código verboso. Em seguida, exploramos algumas abordagens como:

* Telescope constructors;
* Static Method Factory;
* Builder pattern;
* Fluent API design style;
* Step Builder pattern.

Toda a abordagem têm seus prós e contras. A abordagem dos construtores telescópicos pode resolver alguns cenários, mas pode ser propensa a erros e verbosa ao lidar com muitos atributos. O Static Method Factory pode oferecer uma boa alternativa para construir objetos quando poucos atributos são necessários. O padrão Builder permite que os desenvolvedores construam objetos complexos passo a passo usando o estilo de design Fluent API, fornecendo uma interface fluente, tornando a criação do objeto mais legível e manutenível e, o padrão Step Builder pode ser usado para impor as restrições da classe em tempo de compilação. No final, pudemos ver como essas abordagens podem ajudar os desenvolvedores a facilitar suas vidas ao criar objetos com muitos atributos.

### Lições aprendidas

- Tornar a vida dos desenvolvedores mais fácil é tão importante quanto tornar a vida dos clientes finais mais fácil;
- A abordagem **Telescoping Constructors** pode resolver alguns cenários, mas pode ser propensa a erros e verbosa ao lidar com muitos atributos;
- **Static Method Factory** pode oferecer uma boa alternativa para construir objetos quando poucos atributos são necessários;
- O padrão **Builder** permite que os desenvolvedores construam objetos complexos passo a passo;
- O estilo de design **Fluent API** pode ajudar os desenvolvedores a criar código especializado focado em encadeamento de métodos, melhorando a experiência do desenvolvedor. Normalmente é usado para expressar linguagens específicas de domínio. Em nosso contexto, foi usado para criar um builder mais fácil de usar, permitindo que os desenvolvedores criem objetos de uma forma legível e manutenível;
- A variação do padrão Builder chamado **Step Builder pattern** permite que os desenvolvedores criem objetos complexos definindo os atributos seguindo uma ordem predefinida;

### Pensamentos finais

Eu espero que você tenha gostado deste conteúdo! Se você tiver alguma dúvida ou feedback, por favor, sinta-se à vontade para entrar em contato. Eu adoraria ouvir de você!

Muitos projetos open-source em Java trazem essas abordagens para criar objetos com muitos atributos opcionais. O Lombok, por exemplo, fornece a anotação `@Builder` para gerar o padrão Builder para você, mas é importante entender como ele funciona para poder usá-lo de maneira eficiente.

Os design patterns e estilos de design de código podem (pra não dizer que devem) ser misturados e combinados conforme necessário para resolver o problema que estão resolvendo. **Não existe bala de prata no desenvolvimento de software**. Cada abordagem tem seus prós e contras e ninguém melhor do que você para saber qual é a melhor para o seu cenário.

Para ver um bom exemplo que usa algumas dessas técnicas na prática, dê uma olhada no projeto Eclipse JNoSQL, na classe [org.eclipse.jnosql.mapping.semistructured.AbstractSemiStructuredTemplate](https://github.com/eclipse-jnosql/jnosql/blob/ecf992ba9bb6aaf2f816e9e21802258c2037736c/jnosql-mapping/jnosql-mapping-semistructured/src/main/java/org/eclipse/jnosql/mapping/semistructured/AbstractSemiStructuredTemplate.java#L295) no método `QueryMapper.MapperFrom select(Class<T> type)`. Ele usa um estilo de design Fluent API para ajudar os usuários a realizar consultas para recuperar dados de implementações de banco de dados NoSQL semi-estruturados.

Se você quer aprender mais sobre o padrão Builder, eu recomendo os seguintes recursos:

- [Fluent-API: Creating Easier, More Intuitive Code With a Fluent API by Otavio Santana](https://dzone.com/articles/java-fluent-api)

- [Effective Java - Item 1: Consider Static Factory Methods Instead Of Constructors](https://www.amazon.com/Effective-Java-3rd-Joshua-Bloch/dp/0134685997)

- [Effective Java - Item 2: Consider a builder when faced with many constructor parameters](https://www.amazon.com/Effective-Java-3rd-Joshua-Bloch/dp/0134685997)

- [Design Patterns: Elements of Reusable Object-Oriented Software](https://www.amazon.com/Design-Patterns-Elements-Reusable-Object-Oriented/dp/0201633612)

- [Builder Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/builder)

- [Builder Pattern - Wikipedia](https://en.wikipedia.org/wiki/Builder_pattern)

Além disso, eu gostaria de recomendar que você coloque essas abordagens em prática no seu dia a dia. Isso ajudará você a entender quando usar cada uma e como aplicá-las de forma eficaz.

Gostou deste conteúdo? Se sim, por favor, compartilhe com seus amigos e colegas. Além disso, não se esqueça de me seguir nas redes sociais para ficar atualizado com os últimos conteúdos e atualizações.

Até o próximo conteúdo!